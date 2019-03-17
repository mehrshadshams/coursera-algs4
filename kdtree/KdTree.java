/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public class KdTree {
    private static final boolean VERTICAL = false;

    private Node root;
    private int size;

    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        if (root == null) return 0;
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (!contains(p)) {
            root = insert(root, root, p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return contains(root, p);
    }

    // draw all points to standard draw
    public void draw() {
        if (root == null) return;

        Queue<NodePair> queue = new Queue<>();
        Queue<Point2D> points = new Queue<>();
        queue.enqueue(new NodePair(root, null));

        StdDraw.setPenRadius(0.01);

        while (!queue.isEmpty()) {
            NodePair pair = queue.dequeue();
            final Node parent = pair.parent;
            final Node node = pair.node;
            points.enqueue(node.p);

            if (node.lb != null) {
                queue.enqueue(new NodePair(node.lb, node));
            }
            if (node.rt != null) {
                queue.enqueue(new NodePair(node.rt, node));
            }

            if (node.axis == VERTICAL) {
                StdDraw.setPenColor(Color.RED);
                if (pair.parent == null) {
                    StdDraw.line(node.p.x(), 0.0, node.p.x(), 1.0);
                }
                else {
                    if (node.p.y() > parent.p.y()) {
                        StdDraw.line(node.p.x(), parent.p.y(), node.p.x(), 1.0);
                    }
                    else {
                        StdDraw.line(node.p.x(), parent.p.y(), node.p.x(), 0.0);
                    }
                }
            }
            else {
                StdDraw.setPenColor(Color.BLUE);
                if (pair.parent == null) {
                    StdDraw.line(0.0, node.p.y(), 1.0, node.p.y());
                }
                else {
                    if (node.p.x() < parent.p.x()) {
                        StdDraw.line(0.0, node.p.y(), parent.p.x(), node.p.y());
                    }
                    else {
                        StdDraw.line(parent.p.x(), node.p.y(), 1.0, node.p.y());
                    }
                }
            }
        }

        while (!points.isEmpty()) {
            Point2D p = points.dequeue();
            StdDraw.setPenColor(Color.BLACK);
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        List<Point2D> points = new LinkedList<>();

        findPoints(root, rect, points);

        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        PointDistance pd = findNearestPoint(root, p, null, Double.POSITIVE_INFINITY);

        return pd.point2D;
    }

    private PointDistance findNearestPoint(Node node, Point2D p, Point2D nearest,
                                           double minDistance) {
        if (node == null) return new PointDistance(nearest, minDistance);

        double dist = p.distanceSquaredTo(node.p);
        if (dist < minDistance) {
            minDistance = dist;
            nearest = node.p;
        }

        PointDistance pd = new PointDistance(nearest, minDistance);
        if (node.axis == VERTICAL) {
            int cmp = compareVertical(p, node.p);
            if (cmp <= 0) {
                PointDistance left = findNearestPoint(node.lb, p, nearest, minDistance);
                if (left.distance >= pd.distance) {
                    PointDistance right = findNearestPoint(node.rt, p, nearest, minDistance);
                    if (right.distance < pd.distance) {
                        pd = right;
                    }
                }
                else {
                    pd = left;
                }
            }
            else {
                PointDistance right = findNearestPoint(node.rt, p, nearest, minDistance);
                if (right.distance >= pd.distance) {
                    PointDistance left = findNearestPoint(node.lb, p, nearest, minDistance);
                    if (left.distance < pd.distance) {
                        pd = left;
                    }
                }
                else {
                    pd = right;
                }
            }
        }
        else {
            int cmp = compareHorizontal(p, node.p);
            if (cmp <= 0) {
                PointDistance bottom = findNearestPoint(node.lb, p, nearest, minDistance);
                if (bottom.distance >= pd.distance) {
                    PointDistance top = findNearestPoint(node.rt, p, nearest, minDistance);
                    if (top.distance < pd.distance) {
                        pd = top;
                    }
                }
                else {
                    pd = bottom;
                }
            }
            else {
                PointDistance top = findNearestPoint(node.rt, p, nearest, minDistance);
                if (top.distance >= pd.distance) {
                    PointDistance bottom = findNearestPoint(node.lb, p, nearest, minDistance);
                    if (bottom.distance < pd.distance) {
                        pd = bottom;
                    }
                }
                else {
                    pd = top;
                }
            }
        }

        if (pd.distance < minDistance) {
            return pd;
        }

        return new PointDistance(nearest, minDistance);
    }

    private void findPoints(Node node, RectHV rect, List<Point2D> points) {
        if (node == null) return;

        if (rect.contains(node.p)) {
            points.add(node.p);
        }

        if (node.axis == VERTICAL) {
            if (rect.xmin() <= node.p.x()) {
                findPoints(node.lb, rect, points);
            }
            if (rect.xmax() >= node.p.x()) {
                findPoints(node.rt, rect, points);
            }
        }
        else {
            if (rect.ymin() <= node.p.y()) {
                findPoints(node.lb, rect, points);
            }
            if (rect.ymax() >= node.p.y()) {
                findPoints(node.rt, rect, points);
            }
        }
    }

    private int compareHorizontal(Point2D p, Point2D h) {
        if (p.y() < h.y()) return -1;
        else if (p.y() > h.y()) return 1;
        return 0;
    }

    private int compareVertical(Point2D p, Point2D h) {
        if (p.x() < h.x()) return -1;
        else if (p.x() > h.x()) return 1;
        return 0;
    }

    private Node insert(Node h, Node parent, Point2D p) {
        if (h == null) {
            boolean axis = invertAxis(parent);
            size++;
            return new Node(p, axis);
        }

        int cmp;
        if (h.axis == VERTICAL) {
            cmp = compareVertical(p, h.p);
        }
        else {
            cmp = compareHorizontal(p, h.p);
        }

        if (cmp <= 0) {
            h.lb = insert(h.lb, h, p);
        }
        else {
            h.rt = insert(h.rt, h, p);
        }

        return h;
    }

    private boolean contains(Node h, Point2D p) {
        if (h == null) return false;
        if (p.compareTo(h.p) == 0) return true;

        int cmp;
        if (h.axis == VERTICAL) {
            cmp = compareVertical(p, h.p);
        }
        else {
            cmp = compareHorizontal(p, h.p);
        }

        if (cmp < 0) {
            return contains(h.lb, p);
        }
        else if (cmp > 0) {
            return contains(h.rt, p);
        }
        else {
            return contains(h.lb, p) || contains(h.rt, p);
        }
    }

    private boolean invertAxis(Node parent) {
        if (parent == null) return VERTICAL;
        return !parent.axis;
    }

    private class PointDistance {
        private Point2D point2D;
        private double distance;

        public PointDistance(Point2D point2D, double distance) {
            this.point2D = point2D;
            this.distance = distance;
        }
    }

    private class Node {
        private final Point2D p;
        private final boolean axis;
        private Node lb;
        private Node rt;

        public Node(Point2D p, boolean axis) {
            this.p = p;
            this.axis = axis;
        }
    }

    private class NodePair {
        final Node node;
        final Node parent;

        public NodePair(Node node, Node parent) {
            this.node = node;
            this.parent = parent;
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kdTree = new KdTree();
        kdTree.insert(new Point2D(0.3, 0.9));
        kdTree.insert(new Point2D(0.3, 0.1));
        // boolean contains = kdTree.contains(new Point2D(0.6, 0.6));
    }
}
