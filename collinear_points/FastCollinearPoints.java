/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private final LineSegment[] segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }

        ArrayList<LineSegment> segmentList = new ArrayList<>();
        Point[] aux = Arrays.copyOf(points, points.length);
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];

            Arrays.sort(aux);
            Arrays.sort(aux, point.slopeOrder());

            int min = 0;
            while (min < aux.length && point.slopeTo(aux[min]) == Double.NEGATIVE_INFINITY)
                min++;

            int max = min;
            while (min < aux.length) {
                while (max < aux.length && point.slopeTo(aux[min]) == point.slopeTo(aux[max]))
                    max++;

                if (max - min >= 3) {
                    Point pMin = aux[min].compareTo(point) < 0 ? aux[min] : point;
                    Point pMax = aux[max - 1].compareTo(point) > 0 ? aux[max - 1] : point;
                    if (point == pMin) {
                        segmentList.add(new LineSegment(pMin, pMax));
                    }
                }
                min = max;
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
