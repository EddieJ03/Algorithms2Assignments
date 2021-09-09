/* *****************************************************************************
 *  Name: Edward Jin
 *  Date: 09/05/2021
 *  Description: Creation of Circular Suffix Array to be leveraged in Burrows-Wheeler Transform
 **************************************************************************** */


public class CircularSuffixArray {

    private class Pair {
        String s;

        // i is the offset value
        int i;

        Pair(String s, int i) {
            this.s = s;
            this.i = i;
        }

        public char charAt(int pos) {
            return s.charAt((i + pos) % s.length());
        }
    }

    private int length;
    private int[] ind;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        length = s.length();

        Pair[] originalSuffix = new Pair[length];

        for (int i = 0; i < length; i++) {
            originalSuffix[i] = new Pair(s, i);
        }

        sort(originalSuffix, 0, length - 1, 0);

        ind = new int[length];

        for (int i = 0; i < length; i++) {
            ind[i] = originalSuffix[i].i;
        }
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return ind[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray csa = new CircularSuffixArray(s);

        // should print (s.length())
        StdOut.println(csa.length());

        int[] a = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            a[i] = csa.index(i);
            StdOut.println("Index of " + i + " is " + a[i]);
        }
    }

    private void sort(Pair[] a, int lo, int hi, int d) {
        if (hi <= lo || d >= length) return;

        int lt = lo, gt = hi;

        int v = a[lo].charAt(d);

        int i = lo + 1;

        while (i <= gt) {
            int t = a[i].charAt(d);

            if (t < v) exch(a, lt++, i++);
            else if (t > v) exch(a, i, gt--);
            else i++;
        }

        sort(a, lo, lt - 1, d);
        sort(a, lt, gt, d+1);
        sort(a, gt + 1, hi, d);
    }

    private void exch(Pair[] a, int i, int j) {
        Pair temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

}