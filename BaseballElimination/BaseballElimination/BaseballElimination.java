/* *****************************************************************************
 *  Name: Edward Jin
 *  Date: 8/13/2021
 *  Description: Solving the baseball elimination problem by building a Flow Network and running Ford-Fulkerson
 **************************************************************************** */

// import edu.princeton.cs.algs4.In;
// import edu.princeton.cs.algs4.Queue;
// import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Stack;

public class BaseballElimination {

    private int numTeams;

    private HashMap<String, Integer> teamNames;
    private HashMap<Integer, String> reverseTeamNames;
    private int[] wins, losses, remaining;
    private int[][] gamesLeftAgainst;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException();

        teamNames = new HashMap<String, Integer>();
        reverseTeamNames = new HashMap<Integer, String>();
        In in = new In(filename);
        numTeams = in.readInt();

        wins = new int[numTeams];
        losses = new int[numTeams];
        remaining = new int[numTeams];

        gamesLeftAgainst = new int[numTeams][numTeams];

        int counter = 0;

        while (counter < numTeams) {
            String str = in.readString();

            teamNames.put(str, counter);
            reverseTeamNames.put(counter, str);

            wins[counter] = in.readInt();

            losses[counter] = in.readInt();

            remaining[counter] = in.readInt();

            for (int i = 0; i < numTeams; i++) {
                gamesLeftAgainst[counter][i] = in.readInt();
            }

            counter += 1;
        }

    }

    // number of teams
    public int numberOfTeams() {
        return numTeams;
    }

    // all teams
    public Iterable<String> teams() {
        Queue<String> allTeams = new Queue<>();
        for (String name : teamNames.keySet()) {
            allTeams.enqueue(name);
        }
        return allTeams;
    }

    // number of wins for given team
    public int wins(String team) {
        if (team == null || !teamNames.containsKey(team)) throw new IllegalArgumentException();
        return wins[teamNames.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (team == null || !teamNames.containsKey(team)) throw new IllegalArgumentException();
        return losses[teamNames.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (team == null || !teamNames.containsKey(team)) throw new IllegalArgumentException();
        return remaining[teamNames.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null || !teamNames.containsKey(team1) || !teamNames
                .containsKey(team2)) throw new IllegalArgumentException();

        return gamesLeftAgainst[teamNames.get(team1)][teamNames.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (team == null || !teamNames.containsKey(team)) throw new IllegalArgumentException();

        if (numTeams == 1) return false;

        int theoreticalMaxWins = wins[teamNames.get(team)] + remaining[teamNames.get(team)], maxflow
                = 0;

        for (int i = 0; i < numTeams; i++) {
            if (wins[i] > theoreticalMaxWins) return true;
        }

        for (int i = 0; i < numTeams; i++) {
            for (int j = i + 1; j < numTeams; j++) {
                if (i != teamNames.get(team) && j != teamNames.get(team)) {
                    maxflow += gamesLeftAgainst[i][j];
                }
            }
        }

        int totVertices = numTeams - 1 + (((numTeams - 1) * (numTeams - 2)) / 2) + 2;

        FlowNetwork network = buildNetwork(totVertices, team, theoreticalMaxWins);

        FordFulkerson doubleF = new FordFulkerson(network, totVertices - 2, totVertices - 1);

        return doubleF.value() != maxflow;
    }

    private FlowNetwork buildNetwork(int totVertices, String team, int theoreticalMaxWins) {
        FlowNetwork network = new FlowNetwork(totVertices);

        int[] otherWins = new int[numTeams - 1];

        int counter2 = 0, counter = 0;

        for (int i = 0; i < numTeams; i++) {
            if (i != teamNames.get(team)) {
                otherWins[counter2] = wins[i];
                counter2 += 1;
            }
        }

        int[][] gamesLeftRemainingTeams = new int[numTeams - 1][numTeams - 1];

        for (int i = 0; i < numTeams; i++) {
            counter2 = 0;
            for (int j = 0; j < numTeams; j++) {
                if (i != teamNames.get(team) && j != teamNames.get(team)) {
                    gamesLeftRemainingTeams[counter][counter2] = gamesLeftAgainst[i][j];
                    counter2++;
                }
            }
            if (i != teamNames.get(team)) {
                counter++;
            }
        }

        counter = 0;

        for (int i = 0; i < numTeams - 1; i++) {

            for (int j = i + 1; j < numTeams - 1; j++) {

                network.addEdge(new FlowEdge(totVertices - 2, counter, gamesLeftRemainingTeams[i][j]));

                network.addEdge(new FlowEdge(counter, i + (((numTeams - 1) * (numTeams - 2)) / 2), Double.POSITIVE_INFINITY));

                network.addEdge(new FlowEdge(counter++, j + (((numTeams - 1) * (numTeams - 2)) / 2), Double.POSITIVE_INFINITY));

            }

            network.addEdge(new FlowEdge(i + (((numTeams - 1) * (numTeams - 2)) / 2), totVertices - 1, theoreticalMaxWins - otherWins[i]));

        }

        return network;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null || !teamNames.containsKey(team)) throw new IllegalArgumentException();

        if (!isEliminated(team)) return null;

        Stack<String> setR = new Stack<String>();

        if (numTeams == 1) return setR;

        int theoreticalMaxWins = wins[teamNames.get(team)] + remaining[teamNames.get(team)];

        for (int i = 0; i < numTeams; i++) {
            if (theoreticalMaxWins < wins[i]) {
                for (String name : teamNames.keySet()) {
                    if (teamNames.get(name) == i) {
                        setR.push(name);
                        return setR;
                    }
                }
            }
        }

        String[] currTeams = new String[numTeams - 1];

        int counter = 0;

        for (String teamName : teamNames.keySet()) {
            if (!teamName.equals(team)) {
                currTeams[counter++] = teamName;
            }
        }

        int totVertices = numTeams - 1 + (((numTeams - 1) * (numTeams - 2)) / 2) + 2;

        FlowNetwork network = buildNetwork(totVertices, team, theoreticalMaxWins);

        FordFulkerson doubleF = new FordFulkerson(network, totVertices - 2, totVertices - 1);

        boolean addOne = false;

        for (int i = totVertices - 1 - numTeams; i < totVertices - 2; i++) {
            if (i - totVertices + 1 + numTeams >= teamNames.get(team)) addOne = true;
            if (doubleF.inCut(i)) {
                setR.push(reverseTeamNames.get(addOne ? i - totVertices + 2 + numTeams : i - totVertices + 1 + numTeams));
            }
        }

        return setR;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
