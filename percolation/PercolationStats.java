/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE_RATIO = 1.96;

    private final double[] fractions;

    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        final int n2 = n * n;

        fractions = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);

            while (!percolation.percolates()) {
                int row = StdRandom.uniform(1, n+1);
                int col = StdRandom.uniform(1, n+1);

                percolation.open(row, col);
            }

            fractions[i] = ((double) percolation.numberOfOpenSites()) / n2;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(fractions);
    }

    public double stddev() {
        return StdStats.stddev(fractions);
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - ((CONFIDENCE_RATIO * stddev()) / Math.sqrt(fractions.length));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + ((CONFIDENCE_RATIO * stddev()) / Math.sqrt(fractions.length));
    }

    public static void main(String[] args) {
        final int n = Integer.parseInt(args[0]);
        final int t = Integer.parseInt(args[1]);

        PercolationStats percolationStats = new PercolationStats(n, t);

        System.out.println("mean = " + percolationStats.mean());
        System.out.println("stddev = " + percolationStats.stddev());
        System.out.println("95% confidence interval = [" +
                                   percolationStats.confidenceLo() + ", " +
                                   percolationStats.confidenceHi() + "]");
    }
}
