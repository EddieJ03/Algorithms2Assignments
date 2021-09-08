class Main {
  public static void main(String[] args) {
    BaseballElimination bball = new BaseballElimination("teams24.txt");

    // FlowNetwork fl = bball.buildNetwork(totVertices(4), "Ireland", 4);

    // System.out.println(fl);

    // FordFulkerson doubleF = new FordFulkerson(fl, totVertices(4) - 2, totVertices(4) - 1);

    // System.out.println(fl);

    // for(int i = 0; i < totVertices(4); i++) {
    //   System.out.println(i + " marked: " + doubleF.inCut(i));
    // }

    for(String str : bball.certificateOfElimination("Team13")) {
      System.out.println(str);
    }
  }

  public static int totVertices(int numTeams) {
    return numTeams - 1 + (((numTeams - 1) * (numTeams - 2)) / 2) + 2;
  }
}