/* *****************************************************************************
 *  Name: Edward Jin
 *  Date: 09/04/2021
 *  Description: Move To Front Encoding of Data
 **************************************************************************** */


// import edu.princeton.cs.algs4.BinaryStdIn;
// import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

        private static final int R = 256;

        // apply move-to-front encoding, reading from standard input and writing to standard output
        public static void encode() {
            char[] eightBit = new char[R];
            for(int i = 0; i < R; i++) {
                eightBit[i] = (char) i;
            }

            int indexOfChar = 0;

            while(!BinaryStdIn.isEmpty()) {
                char c = BinaryStdIn.readChar();
                
                for(int i = 0; i < R; i++) {
                    if(eightBit[i] == c) {
                        indexOfChar = i;
                        break;
                    }
                }

                for(int i = indexOfChar; i > 0; i--) {
                    eightBit[i] = eightBit[i - 1];
                }

                eightBit[0] = c;

                BinaryStdOut.write(indexOfChar, 8);
            }

            BinaryStdOut.close();
        }

        // apply move-to-front decoding, reading from standard input and writing to standard output
        public static void decode() {

            char[] eightBit = new char[R];

            for(int i = 0; i < R; i++) {
                eightBit[i] = (char) i;
            }

            while(!BinaryStdIn.isEmpty()) {
                int c = BinaryStdIn.readInt(8);

                char newChar = eightBit[c];

                for(int i = c; i > 0; i--) {
                    eightBit[i] = eightBit[i - 1];
                }
                
                BinaryStdOut.write(newChar);

                eightBit[0] = newChar;
            }

            BinaryStdOut.close();

        }

        // if args[0] is "-", apply move-to-front encoding
        // if args[0] is "+", apply move-to-front decoding
        public static void main(String[] args) {
            if (args[0].equals("-")) {
                encode();
            } else if(args[0].equals("+")) {
                decode();
            }
        }

    }

