/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static final double BORDER_ENERGY = 1000.0;

    private int[][] image;
    private boolean transposed = false;
    private int height;
    private int width;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        height = picture.height();
        width = picture.width();
        this.image = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                image[i][j] = picture.getRGB(j, i);
            }
        }
    }

    private int[][] transpose(int[][] img) {
        int h = img.length, w = img[0].length;
        int[][] transpose = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                transpose[i][j] = img[j][i];
            }
        }
        return transpose;
    }

    private void transposeImage() {
        image = transpose(image);
        transposed = !transposed;
    }

    // current picture
    public Picture picture() {
        if (transposed) {
            transposeImage();
        }

        Picture picture = new Picture(width(), height());
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                picture.setRGB(col, row, image[row][col]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IllegalArgumentException();

        return energy(image, x, y);
    }

    private double energy(int[][] img, int x, int y) {
        int h = img.length, w = img[0].length;
        if (x == 0 || y == 0 || x == w - 1 || y == h - 1)
            return BORDER_ENERGY;

        final int right = img[y][x + 1];
        final int left = img[y][x - 1];
        final int top = img[y - 1][x];
        final int bottom = img[y + 1][x];

        final double dx = computeDiffs(right, left);
        final double dy = computeDiffs(top, bottom);

        return Math.sqrt(dx + dy);
    }

    private double[][] toEnergyMatrix(int[][] array) {
        int h = array.length, w = array[0].length;
        double[][] returnDouble = new double[h][w];
        for (int row = 0; row < h; row++)
            for (int col = 0; col < w; col++)
                returnDouble[row][col] = energy(array, col, row);

        return returnDouble;
    }

    private double computeDiffs(int lt, int rb) {
        int rbRed = (rb >> 16) & 0xFF;
        int rbGreen = (rb >> 8) & 0xFF;
        int rbBlue = (rb >> 0) & 0xFF;

        int ltRed = (lt >> 16) & 0xFF;
        int ltGreen = (lt >> 8) & 0xFF;
        int ltBlue = (lt >> 0) & 0xFF;

        double dr = rbRed - ltRed;
        double dg = rbGreen - ltGreen;
        double db = rbBlue - ltBlue;

        return (dr * dr) + (dg * dg) + (db * db);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed) {
            transposeImage();
        }
        double[][] energy = toEnergyMatrix(image);
        SeamShortestPath seamShortestPath = new SeamShortestPath(energy);
        int[] out = seamShortestPath.getPath();

        // transposeImage();

        return out;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transposed) {
            transposeImage();
        }
        double[][] energy = toEnergyMatrix(image);
        SeamShortestPath seamShortestPath = new SeamShortestPath(energy);
        return seamShortestPath.getPath();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (height() <= 1) throw new IllegalArgumentException();
        if (seam.length != width()) throw new IllegalArgumentException();

        if (!transposed) {
            transposeImage();
        }

        removeSeam(seam);

        height--;

        // transposeImage();
    }

    private void removeSeam(int[] seam) {
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }

        int h = image.length, w = image[0].length;
        int[][] image2 = new int[h][w - 1];
        for (int i = 0; i < seam.length; i++) {
            int col = seam[i];
            System.arraycopy(image[i], 0, image2[i], 0, col);
            System.arraycopy(image[i], col + 1, image2[i], col, w - col - 1);
        }

        image = image2;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (width() <= 1) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();

        if (transposed) {
            transposeImage();
        }

        removeSeam(seam);

        width--;
    }

    private class SeamShortestPath {
        private final double[][] energy;
        private final double[] distTo;
        private final int[] edgeTo;

        SeamShortestPath(double[][] energy) {
            int ehight = energy.length, ewidth = energy[0].length;
            this.energy = energy;
            this.distTo = new double[ehight * ewidth + 2];
            this.edgeTo = new int[ehight * ewidth + 2];

            for (int col = 0; col <= ewidth; col++) {
                edgeTo[col] = -1;
            }

            for (int row = 1; row < ehight; row++) {
                for (int col = 0; col < ewidth; col++) {
                    int index = row * ewidth + col + 1;
                    this.distTo[index] = Double.POSITIVE_INFINITY;
                }
            }

            for (int row = 0; row < ehight - 1; row++) {
                for (int col = 0; col < ewidth; col++) {
                    relax(row, col, row + 1, col - 1);
                    relax(row, col, row + 1, col);
                    relax(row, col, row + 1, col + 1);
                }
            }

            double min = Double.POSITIVE_INFINITY;
            int minIndex = -1;
            for (int i = 0; i < ewidth; i++) {
                int index = (ehight - 1) * ewidth + i + 1;
                if (distTo[index] < min) {
                    min = distTo[index];
                    minIndex = index;
                }
            }

            edgeTo[edgeTo.length - 1] = minIndex;
        }

        private void relax(int row, int col, int row2, int col2) {
            int w = energy[0].length;
            if (col2 < 0 || col2 >= w) return;
            int index1 = row * w + col + 1;
            int index2 = row2 * w + col2 + 1;
            final double temp = distTo[index1] + energy[row][col];
            if (distTo[index2] > temp) {
                distTo[index2] = temp;
                edgeTo[index2] = index1;
            }
        }

        public int[] getPath() {
            int h = energy.length, w = energy[0].length;

            int[] result = new int[h];
            int index = edgeTo[edgeTo.length - 1];
            int counter = h - 1;
            while (index >= 0) {
                result[counter] = (index - 1) % w;
                counter--;
                index = edgeTo[index];
            }

            return result;
        }
    }
}
