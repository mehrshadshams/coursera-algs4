/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] seq = createSeq();

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            for (char i = 0; i < seq.length; i++) {
                if (c == seq[i]) {
                    BinaryStdOut.write(i);
                    for (int j = i; j > 0; j--) {
                        seq[j] = seq[j-1];
                    }
                    seq[0] = c;
                    break;
                }
            }
        }

        // close output stream
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] seq = createSeq();

        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readChar();
            char c = seq[i];
            BinaryStdOut.write(c);
            for (int j = i; j > 0; j--) {
                seq[j] = seq[j-1];
            }
            seq[0] = c;
        }

        // close output stream
        BinaryStdOut.close();
    }

    private static char[] createSeq() {
        char[] seq = new char[R];
        for (int i = 0; i < R; i++) {
            seq[i] = (char) (i);
        }
        return seq;
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if ("-".equals(args[0])) {
            encode();
        }
        else if ("+".equals(args[0])) {
            decode();
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
