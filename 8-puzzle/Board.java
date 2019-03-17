/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;

public class Board {
    private static final int BLANK = 0;
    private final int n;
    private final int[][] blocks;
    private Board twin;
    private Integer hamming;
    private Integer manhattan;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        n = blocks.length;
        this.blocks = copyBlocks(blocks);
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of blocks out of place
    public int hamming() {
        if (hamming == null) {
            hamming = 0;
            for (int i = 1; i <= n * n - 1; i++) {
                int row = (i - 1) / n;
                int col = (i - 1) % n;
                int val = blocks[row][col];
                if (val != i) {
                    hamming++;
                }
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if (manhattan == null) {
            manhattan = 0;
            for (int row = 1; row <= n; row++) {
                for (int col = 1; col <= n; col++) {
                    int expected = (row - 1) * n + col;
                    int val = blocks[row - 1][col - 1];
                    if (val != BLANK && val != expected) {
                        int r2 = 1 + (val - 1) / n;
                        int c2 = 1 + (val - 1) % n;
                        manhattan += Math.abs(r2 - row) + Math.abs(c2 - col);
                    }
                }
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        if (twin == null) {
            int block1 = randomBlock();
            int block2 = block1;
            while (block2 == block1) {
                block2 = randomBlock();
            }

            twin = new Board(blocks);
            int row1 = block1 / n;
            int col1 = block1 % n;
            int row2 = block2 / n;
            int col2 = block2 % n;

            exchange(twin.blocks, row1, col1, row2, col2);
        }

        return twin;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (this == y) {
            return true;
        }
        if (!Board.class.isAssignableFrom(y.getClass())) {
            return false;
        }
        Board that = (Board) y;
        if (this.n != that.n) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] != that.blocks[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int row = -1;
        int col = -1;
        for (int i = 0; i < n * n; i++) {
            row = i / n;
            col = i % n;
            if (blocks[row][col] == BLANK) {
                break;
            }
        }

        ArrayList<Board> boards = new ArrayList<>();
        int[][] dirs = new int[][] {
                { -1, 0 },
                { 0, -1 },
                { 1, 0 },
                { 0, 1 }
        };
        for (int[] direction : dirs) {
            int row2 = row + direction[1];
            int col2 = col + direction[0];
            if (row2 >= 0 && row2 < n && col2 >= 0 && col2 < n) {
                Board newBoard = new Board(blocks);
                exchange(newBoard.blocks, row, col, row2, col2);
                boards.add(newBoard);
            }
        }
        return boards;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private static void exchange(int[][] tiles, int r1, int c1, int r2, int c2) {
        int temp = tiles[r1][c1];
        tiles[r1][c1] = tiles[r2][c2];
        tiles[r2][c2] = temp;
    }

    private static int[][] copyBlocks(int[][] tiles) {
        int[][] blocks2 = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                blocks2[i][j] = tiles[i][j];
            }
        }
        return blocks2;
    }

    private int randomBlock() {
        int val;
        int row;
        int col;
        int i;
        do {
            i = StdRandom.uniform(n * n);
            row = i / n;
            col = i % n;
            val = blocks[row][col];
        } while (val == BLANK);
        return i;
    }

    // unit tests (not graded)
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println(initial);
        StdOut.println(initial.twin());
        StdOut.println(initial.twin().equals(initial));
    }
}
