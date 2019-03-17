/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {
    // circular suffix array of s
    private final String text;
    private final SortedSuffix[] suffixes;

    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        text = s;
        suffixes = new SortedSuffix[s.length()];
        for (int i = 0; i < s.length(); i++) {
            suffixes[i] = new SortedSuffix(i);
        }

        Arrays.sort(suffixes);
    }

    // length of s
    public int length() {
        return suffixes.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0) throw new IllegalArgumentException();
        return suffixes[i].suffixIndex;
    }

    private static String suffix(String s, int i) {
        return s.substring(i) + s.substring(0, i);
    }

    private class SortedSuffix implements Comparable<SortedSuffix> {
        private final int suffixIndex;

        private SortedSuffix(int suffixIndex) {
            this.suffixIndex = suffixIndex;
        }

        @Override
        public String toString() {
            return suffix(text, suffixIndex);
        }

        @Override
        public int compareTo(SortedSuffix other) {
            int i = suffixIndex, j = other.suffixIndex;

            final String s = text;

            char c1 = s.charAt(i);
            char c2 = s.charAt(j);
            i++;
            j++;
            while (c1 == c2 && i != suffixIndex && j != other.suffixIndex) {
                if (i >= s.length()) {
                    i = 0;
                }
                if (j >= s.length()) {
                    j = 0;
                }
                c1 = s.charAt(i);
                c2 = s.charAt(j);
                i++;
                j++;
            }

            return Character.compare(c1, c2);
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        final String s = "BAAAABBAAB";
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);

        // String[] suffixes = new String[s.length()];
        // for (int i = 0; i < s.length(); i++) {
        //     suffixes[i] = circularSuffixArray.suffixes[i].toString();
        // }
        // String[] suffixes2 = new String[s.length()];
        // for (int i = 0; i < s.length(); i++) {
        //     suffixes2[i] = CircularSuffixArray.suffix(s, i);
        // }
        //
        // Arrays.sort(suffixes2);
        // boolean eq = Objects.deepEquals(suffixes, suffixes2);

        StdOut.println(circularSuffixArray.index(5));
    }
}
