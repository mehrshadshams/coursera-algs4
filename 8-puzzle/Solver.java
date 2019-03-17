/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final boolean solvable;
    private final int totalMoves;
    private final Stack<Board> solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }

        int moves = 0;
        MinPQ<GameNode> gamePQ = new MinPQ<>();
        MinPQ<GameNode> twinPQ = new MinPQ<>();
        gamePQ.insert(new GameNode(initial, null, moves));
        twinPQ.insert(new GameNode(initial.twin(), null, moves));

        GameNode current = null;
        boolean foundSolution = false;
        while (!gamePQ.isEmpty()) {
            current = gamePQ.delMin();
            if (current.board.isGoal()) {
                foundSolution = true;
                break;
            }
            GameNode twin = twinPQ.delMin();
            if (twin.board.isGoal()) {
                break;
            }

            expandMoves(current, gamePQ);
            expandMoves(twin, twinPQ);
        }
        solvable = foundSolution;
        this.totalMoves = solvable ? current.moves : -1;
        this.solution = new Stack<>();
        if (solvable) {
            GameNode node = current;
            while (node != null) {
                solution.push(node.board);
                node = node.predecessor;
            }
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return totalMoves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    private void expandMoves(GameNode gameNode, MinPQ<GameNode> pq) {
        final Iterable<Board> neighbors = gameNode.board.neighbors();
        for (Board neighboar : neighbors) {
            Board prevBoard = gameNode.predecessor != null ? gameNode.predecessor.board : null;
            if (!neighboar.equals(prevBoard)) {
                pq.insert(new GameNode(neighboar, gameNode, gameNode.moves + 1));
            }
        }
    }

    private class GameNode implements Comparable<GameNode> {
        private final Board board;
        private final GameNode predecessor;
        private final int moves;
        private final int priority;

        private GameNode(Board board, GameNode predecessor, int moves) {
            this.board = board;
            this.predecessor = predecessor;
            this.moves = moves;
            this.priority = board.manhattan() + moves;
        }

        @Override
        public int compareTo(GameNode that) {
            return Integer.compare(priority, that.priority);
        }

        @Override
        public String toString() {
            return  "priority  =" + priority + "\n" +
                    "moves     =" + moves + "\n" +
                    "manhattan =" + board.manhattan() + "\n" +
                    board.toString();
        }
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
