/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.LinkedQueue;

import java.util.LinkedList;
import java.util.List;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);

        int index = -1;
        char[] t = new char[s.length()];
        for (int i = 0; i < circularSuffixArray.length(); i++) {
            int idx = circularSuffixArray.index(i);
            if (idx == 0) {
                if (index != -1) throw new RuntimeException("");
                index = i;
            }
            if (idx > 0) {
                t[i] = s.charAt(idx - 1);
            }
            else {
                t[i] = s.charAt(s.length() - 1);
            }
        }

        BinaryStdOut.write(index);
        for (char c : t) {
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();

        LinkedQueue[] counts = new LinkedQueue[R];

        List<Character> lastChars = new LinkedList<>();
        int i = 0;
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            lastChars.add(c);
            if (counts[c] == null) {
                counts[c] = new LinkedQueue<Integer>();
            }
            counts[c].enqueue(i);
            i++;
        }

        Character[] firstChars = lastChars.toArray(new Character[0]);

        countingSort(firstChars);

        int[] next = new int[firstChars.length];

        for (i = 0; i < next.length; i++) {
            char ch = firstChars[i];
            int j = (Integer) counts[ch].dequeue();
            next[i] = j;
        }

        int n = first;
        for (int j = 0; j < next.length; j++) {
            BinaryStdOut.write(firstChars[n]);
            n = next[n];
        }

        BinaryStdOut.close();
    }

    private static void countingSort(Character[] array) {
        int n = array.length;
        Character[] aux = new Character[n];
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++)
            count[array[i] + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        for (int i = 0; i < n; i++)
            aux[count[array[i]]++] = array[i];
        for (int i = 0; i < n; i++)
            array[i] = aux[i];
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if ("-".equals(args[0])) {
            transform();
        }
        else if ("+".equals(args[0])) {
            inverseTransform();
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
