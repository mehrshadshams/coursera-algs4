/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph graph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(graph, w);

        return length(bfsA, bfsB);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(graph, w);

        return ancestor(bfsA, bfsB);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(graph, w);

        return length(bfsA, bfsB);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(graph, w);

        return ancestor(bfsA, bfsB);
    }

    private int ancestor(BreadthFirstDirectedPaths bfsA, BreadthFirstDirectedPaths bfsB) {
        int minLength = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < graph.V(); i++) {
            if (bfsA.hasPathTo(i) && bfsB.hasPathTo(i)) {
                int dist = bfsA.distTo(i) + bfsB.distTo(i);
                if (dist < minLength) {
                    minLength = dist;
                    ancestor = i;
                }
            }
        }

        return ancestor;
    }

    private int length(BreadthFirstDirectedPaths bfsA, BreadthFirstDirectedPaths bfsB) {
        int minLength = Integer.MAX_VALUE;
        for (int i = 0; i < graph.V(); i++) {
            if (bfsA.hasPathTo(i) && bfsB.hasPathTo(i)) {
                int dist = bfsA.distTo(i) + bfsB.distTo(i);
                if (dist < minLength) {
                    minLength = dist;
                }
            }
        }

        return minLength < Integer.MAX_VALUE ? minLength : -1;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
