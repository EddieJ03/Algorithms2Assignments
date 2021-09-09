/* *****************************************************************************
 *  Name: Edward Jin
 *  Date: 09/07/2021
 *  Description: Implementation of Burrows-Wheeler tranform and inverse transform
 **************************************************************************** */

// import edu.princeton.cs.algs4.BinaryStdIn;
// import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        
        StringBuilder str = new StringBuilder();

        int index = 0;

        CircularSuffixArray csa = new CircularSuffixArray(s);

        for (int i = 0; i < s.length(); i++) {

            int num = csa.index(i);

            str.append(s.charAt(num - 1 < 0 ? s.length() - 1 : num - 1));

            if (num == 0) {
                index = i;
            }

        }

        BinaryStdOut.write(index, 32);

        BinaryStdOut.write(str.toString());

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {

        int first = BinaryStdIn.readInt();

        StringBuilder tempStr = new StringBuilder();

        while (!BinaryStdIn.isEmpty()) {
            tempStr.append(BinaryStdIn.readChar());
        }

        char[] t = new char[tempStr.length()];

        for (int j = 0; j < tempStr.length(); j++) {
          t[j] = tempStr.charAt(j);
        }

        int[] next = new int[t.length];

        for (int i = 0; i < next.length; i++) {
            next[i] = i;
        }

        sort(t, next);

        char c;

        for (int i = 0; i < t.length; i++) {
            c = t[first];
            BinaryStdOut.write(c);
            first = next[first];
        }

        BinaryStdOut.close();

    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        }
    }

    // use key indexed counting for t[]
    private static void sort(char[] a, int[] ind) {

        int n = a.length;
        int R = 256;   // extend ASCII alphabet size
        char[] aux = new char[n];
        int[] indAux = new int[n];

        // compute frequency counts
        int[] count = new int[R + 1], indCount = new int[R + 1];

        for (int i = 0; i < n; i++) {
            count[a[i] + 1]++;
            indCount[a[i] + 1]++;
        }

        // compute cumulates
        for (int r = 0; r < R; r++) {
            count[r + 1] += count[r];
            indCount[r + 1] += indCount[r];
        }

        // move data
        for (int i = 0; i < n; i++) {
            aux[count[a[i]]++] = a[i];
            indAux[indCount[a[i]]++] = ind[i];
        }

        for (int i = 0; i < n; i++) {
            a[i] = aux[i];
            ind[i] = indAux[i];
        }

    }

}
