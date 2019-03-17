/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private final LineSegment[] segments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }

        Point[] aux = Arrays.copyOf(points, points.length);
        for (int i = 0; i < aux.length; i++) {
            Point p1 = aux[i];
            if (p1 == null) {
                throw new IllegalArgumentException();
            }
            for (int j = i + 1; j < aux.length; j++) {
                Point p2 = aux[j];
                if (p2 == null) {
                    throw new IllegalArgumentException();
                }

                if (p1.compareTo(p2) == 0) {
                    throw new IllegalArgumentException();
                }
            }
        }

        Arrays.sort(aux);
        ArrayList<LineSegment> segmentList = new ArrayList<>();
        for (int i = 0; i < aux.length; i++) {
            for (int j = i + 1; j < aux.length; j++) {
                for (int k = j + 1; k < aux.length; k++) {
                    for (int m = k + 1; m < aux.length; m++) {
                        Point p1 = aux[i];
                        Point p3 = aux[k];
                        Point p2 = aux[j];
                        Point p4 = aux[m];
                        if (p1.slopeTo(p2) == p1.slopeTo(p3) && p1.slopeTo(p2) == p1.slopeTo(p4))
                            segmentList.add(new LineSegment(p1, p4));
                    }
                }
            }
        }

        segments = segmentList.toArray(new LineSegment[0]);
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(segments, segments.length);
    }
}
