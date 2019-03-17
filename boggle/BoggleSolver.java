/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Set;
import java.util.TreeSet;

public class BoggleSolver {

    private final BoggleTrieSET trie;
    private boolean[][] visited;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new BoggleTrieSET();
        for (String word : dictionary) {
            trie.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> words = new TreeSet<>();
        visited = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                dfs(board, "", words, trie.getRoot(), row, col);
            }
        }
        return words;
    }

    private void dfs(BoggleBoard board, String prefix, Set<String> words, BoggleTrieSET.Node parent,
                     int row, int col) {
        if (row < 0 || row >= board.rows() || col < 0 || col >= board.cols()) return;
        if (visited[row][col]) return;

        final char letter = board.getLetter(row, col);
        String ch = String.valueOf(letter);
        if ("Q".equalsIgnoreCase(ch)) {
            ch = "QU";
        }

        prefix = prefix + ch;

        if (prefix.length() >= 3) {
            final BoggleTrieSET.Node node = trie.get(prefix);
            if (node == null) {
                return;
            }

            if (node.hasString()) {
                words.add(prefix);
            }

            parent = node;
        }

        visited[row][col] = true;
        dfs(board, prefix, words, parent, row + 1, col);
        dfs(board, prefix, words, parent, row - 1, col);
        dfs(board, prefix, words, parent, row, col + 1);
        dfs(board, prefix, words, parent, row, col - 1);
        dfs(board, prefix, words, parent, row - 1, col - 1);
        dfs(board, prefix, words, parent, row - 1, col + 1);
        dfs(board, prefix, words, parent, row + 1, col - 1);
        dfs(board, prefix, words, parent, row + 1, col + 1);
        visited[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException();
        if (word.length() < 3 || !trie.contains(word)) return 0;
        if (word.length() <= 4) return 1;
        if (word.length() == 5) return 2;
        if (word.length() == 6) return 3;
        if (word.length() == 7) return 5;

        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;

        Stopwatch sw = new Stopwatch();
        final Iterable<String> allValidWords = solver.getAllValidWords(board);
        final double elapsed = sw.elapsedTime();

        for (String word : allValidWords) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score + ", Elapsed = " + elapsed);
    }
}
