/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordNet {
    private final SAP sap;
    private final Map<Integer, Set<String>> vertexToNouns;
    private final Map<String, Set<Integer>> nounToVertex;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) throw new IllegalArgumentException();
        if (hypernyms == null) throw new IllegalArgumentException();

        final In synsetsIn = new In(synsets);
        vertexToNouns = new HashMap<>();
        nounToVertex = new HashMap<>();
        while (synsetsIn.hasNextLine()) {
            String next = synsetsIn.readLine();
            String[] parts = next.split(",");
            final int vertexId = Integer.parseInt(parts[0]);
            final String[] nouns = parts[1].split(" ");

            if (!vertexToNouns.containsKey(vertexId)) {
                vertexToNouns.put(vertexId, new HashSet<>());
            }

            for (String noun : nouns) {
                vertexToNouns.get(vertexId).add(noun);

                if (!nounToVertex.containsKey(noun)) {
                    nounToVertex.put(noun, new HashSet<>());
                }

                nounToVertex.get(noun).add(vertexId);
            }
        }

        Digraph graph = new Digraph(nounToVertex.size());

        final In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String next = hypernymsIn.readLine();
            String[] parts = next.split(",");
            if (parts.length > 0) {
                final int synsetId = Integer.parseInt(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    int w = Integer.parseInt(parts[i]);
                    graph.addEdge(synsetId, w);
                }
            }
        }

        this.sap = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToVertex.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        final Set<Integer> index = getVertex(word);
        return !index.isEmpty();
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        Set<Integer> v = getVertex(nounA);
        Set<Integer> w = getVertex(nounB);

        if (v.isEmpty() || w.isEmpty()) throw new IllegalArgumentException();

        return sap.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Set<Integer> v = getVertex(nounA);
        Set<Integer> w = getVertex(nounB);

        if (v.isEmpty() || w.isEmpty()) throw new IllegalArgumentException();

        int ancestor = sap.ancestor(v, w);
        if (ancestor >= 0)
            return String.join(" ", vertexToNouns.get(ancestor));

        return null;
    }

    private Set<Integer> getVertex(String word) {
        if (word == null) throw new IllegalArgumentException();
        if (!nounToVertex.containsKey(word)) throw new IllegalArgumentException();

        return nounToVertex.get(word);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // Stopwatch sw = new Stopwatch();
        // WordNet wordNet = new WordNet(args[0], args[1]);
        // System.out.println(sw.elapsedTime());
        // for (String noun : wordNet.nouns()) {
        //     System.out.println(noun);
        // }
    }
}
