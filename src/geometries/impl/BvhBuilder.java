package geometries.impl;

import geometries.api.Intersectable;
import primitives.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for creating a Bounding Volume Hierarchy (BVH) tree.
 * It uses the Surface Area Heuristic (SAH) algorithm and supports N-ary trees
 * (e.g., Binary, Quaternary).
 *
 * @author David &amp; Yehuda
 */
public class BvhBuilder {

    /**
     * The number of bins to use for the SAH algorithm.
     */
    private static int BINS_COUNT = 16;

    /**
     * The maximum number of branches (children) a single node can have.
     * 2 = Binary Tree, 4 = Quad Tree (QBVH), 8 = Octree-like BVH.
     */
    private static int MAX_BRANCHES = 2;

    /**
     * Prevents instantiation of this static utility class.
     */
    private BvhBuilder() {
    }

    /**
     * Sets the number of bins to use for the SAH algorithm.
     *
     * @param count the number of bins
     */
    public static void setBINS(int count) {
        BINS_COUNT = count;
    }

    /**
     * Sets the maximum number of branches per tree node.
     *
     * @param count the number of branches (minimum 2)
     */
    public static void setMaxBranches(int count) {
        if (count >= 2) {
            MAX_BRANCHES = count;
        }
    }

    /**
     * A simple helper class to represent a "Bin" for the SAH algorithm.
     */
    private static class Bin {
        /**
         * Accumulated bounds for all boxes in this bin.
         */
        AABB bounds = null;
        /**
         * Number of boxes assigned to this bin.
         */
        int count = 0;

        /**
         * Creates an empty bin.
         */
        Bin() {
        }

        /**
         * Adds a box to the bin and expands accumulated bounds.
         *
         * @param box bounding box to add
         */
        void add(AABB box) {
            count++;
            if (bounds == null) {
                bounds = box;
            } else {
                bounds = bounds.union(box);
            }
        }
    }

    /**
     * Helper record/class to hold the result of a single binary split.
     */
    private static class SplitResult {
        /**
         * Geometries assigned to the left side of the split.
         */
        List<Intersectable> left;
        /**
         * Geometries assigned to the right side of the split.
         */
        List<Intersectable> right;

        /**
         * Creates a split result with left and right partitions.
         *
         * @param left geometries in the left partition
         * @param right geometries in the right partition
         */
        SplitResult(List<Intersectable> left, List<Intersectable> right) {
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Builds the main BVH tree from a list of geometries.
     *
     * @param allGeometries the full list of geometries in the scene
     * @return a single Geometries object containing the optimized tree
     */
    protected static Geometries buildSahTree(List<Intersectable> allGeometries) {
        List<Intersectable> infiniteGeometries = new ArrayList<>();
        List<Intersectable> finiteGeometries = new ArrayList<>();

        for (Intersectable geo : allGeometries) {
            if (geo.getBoundingBox() == null) {
                infiniteGeometries.add(geo);
            } else {
                finiteGeometries.add(geo);
            }
        }

        Geometries root = new Geometries();

        for (Intersectable geo : infiniteGeometries) {
            root.add(geo);
        }

        if (!finiteGeometries.isEmpty()) {
            root.add(buildNode(finiteGeometries));
        }

        return root;
    }

    /**
     * Recursively builds a BVH node, splitting the list up to MAX_BRANCHES chunks.
     *
     * @param geometries the list of finite geometries to organize
     * @return a Geometries object representing a branch in the tree
     */
    private static Geometries buildNode(List<Intersectable> geometries) {
        // Base case: If there are 20 or fewer geometries, stop splitting and make a leaf.
        if (geometries.size() <= 20) {
            return createLeaf(geometries);
        }

        // List to hold the geometric chunks for the current node level
        List<List<Intersectable>> chunks = new ArrayList<>();
        chunks.add(geometries);

        // Iteratively split the largest chunk until we reach the desired number of branches
        while (chunks.size() < MAX_BRANCHES) {
            int largestIdx = -1;
            int maxSize = 20; // Only split chunks that are larger than the leaf threshold

            // Find the chunk with the most geometries to split next
            for (int i = 0; i < chunks.size(); i++) {
                if (chunks.get(i).size() > maxSize) {
                    maxSize = chunks.get(i).size();
                    largestIdx = i;
                }
            }

            // If no chunk is large enough to be split, break early
            if (largestIdx == -1) {
                break;
            }

            // Remove the chunk and split it using SAH
            List<Intersectable> toSplit = chunks.remove(largestIdx);
            SplitResult splitResult = performSahSplit(toSplit);

            // Add the two new sub-chunks back into our working list
            chunks.add(splitResult.left);
            chunks.add(splitResult.right);
        }

        // Now we have up to MAX_BRANCHES chunks. Recursively build them as children.
        Geometries parentNode = new Geometries();
        for (List<Intersectable> chunk : chunks) {
            parentNode.add(buildNode(chunk));
        }

        return parentNode;
    }

    /**
     * Performs a single optimal binary split using the SAH algorithm.
     *
     * @param geometries the list to split
     * @return a SplitResult containing the two sub-lists
     */
    private static SplitResult performSahSplit(List<Intersectable> geometries) {
        AABB totalBox = geometries.getFirst().getBoundingBox();
        for (int i = 1; i < geometries.size(); i++) {
            totalBox = totalBox.union(geometries.get(i).getBoundingBox());
        }

        double xLength = totalBox.getMax().getX() - totalBox.getMin().getX();
        double yLength = totalBox.getMax().getY() - totalBox.getMin().getY();
        double zLength = totalBox.getMax().getZ() - totalBox.getMin().getZ();

        int axis = 0;
        if (yLength > xLength && yLength > zLength) axis = 1;
        else if (zLength > xLength && zLength > yLength) axis = 2;

        double minCenter = Double.MAX_VALUE;
        double maxCenter = -Double.MAX_VALUE;

        for (Intersectable geo : geometries) {
            double center = getCenter(geo.getBoundingBox(), axis);
            if (center < minCenter) minCenter = center;
            if (center > maxCenter) maxCenter = center;
        }

        if (minCenter == maxCenter) {
            return splitInHalf(geometries);
        }

        Bin[] bins = new Bin[BINS_COUNT];
        for (int i = 0; i < BINS_COUNT; i++) {
            bins[i] = new Bin();
        }

        for (Intersectable geo : geometries) {
            double center = getCenter(geo.getBoundingBox(), axis);
            int binIndex = (int) (((center - minCenter) / (maxCenter - minCenter)) * BINS_COUNT);
            if (binIndex == BINS_COUNT) binIndex = BINS_COUNT - 1;
            bins[binIndex].add(geo.getBoundingBox());
        }

        double minCost = Double.MAX_VALUE;
        int bestSplit = -1;

        for (int i = 0; i < BINS_COUNT - 1; i++) {
            AABB leftBox = null;
            int leftCount = 0;
            for (int j = 0; j <= i; j++) {
                if (bins[j].count > 0) {
                    leftCount += bins[j].count;
                    leftBox = (leftBox == null) ? bins[j].bounds : leftBox.union(bins[j].bounds);
                }
            }

            AABB rightBox = null;
            int rightCount = 0;
            for (int j = i + 1; j < BINS_COUNT; j++) {
                if (bins[j].count > 0) {
                    rightCount += bins[j].count;
                    rightBox = (rightBox == null) ? bins[j].bounds : rightBox.union(bins[j].bounds);
                }
            }

            if (leftCount > 0 && rightCount > 0) {
                double leftArea = calculateSurfaceArea(leftBox);
                double rightArea = calculateSurfaceArea(rightBox);
                double cost = (leftArea * leftCount) + (rightArea * rightCount);

                if (cost < minCost) {
                    minCost = cost;
                    bestSplit = i;
                }
            }
        }

        if (bestSplit == -1) {
            return splitInHalf(geometries);
        }

        List<Intersectable> leftList = new ArrayList<>();
        List<Intersectable> rightList = new ArrayList<>();

        for (Intersectable geo : geometries) {
            double center = getCenter(geo.getBoundingBox(), axis);
            int binIndex = (int) (((center - minCenter) / (maxCenter - minCenter)) * BINS_COUNT);
            if (binIndex == BINS_COUNT) binIndex = BINS_COUNT - 1;

            if (binIndex <= bestSplit) {
                leftList.add(geo);
            } else {
                rightList.add(geo);
            }
        }

        if (leftList.isEmpty() || rightList.isEmpty()) {
            return splitInHalf(geometries);
        }

        return new SplitResult(leftList, rightList);
    }

    /**
     * Calculates the surface area of a bounding box.
     *
     * @param box bounding box to evaluate
     * @return surface area of the box, or 0 when the box is null
     */
    private static double calculateSurfaceArea(AABB box) {
        if (box == null) return 0;
        double x = box.getMax().getX() - box.getMin().getX();
        double y = box.getMax().getY() - box.getMin().getY();
        double z = box.getMax().getZ() - box.getMin().getZ();
        return 2.0 * (x * y + y * z + z * x);
    }

    /**
     * Returns the center coordinate of a box on a selected axis.
     *
     * @param box bounding box to evaluate
     * @param axis axis index (0 for X, 1 for Y, 2 for Z)
     * @return center coordinate on the selected axis
     */
    private static double getCenter(AABB box, int axis) {
        switch (axis) {
            case 0: return (box.getMin().getX() + box.getMax().getX()) / 2.0;
            case 1: return (box.getMin().getY() + box.getMax().getY()) / 2.0;
            case 2: return (box.getMin().getZ() + box.getMax().getZ()) / 2.0;
            default: return 0;
        }
    }

    /**
     * Creates a leaf node containing the provided geometries.
     *
     * @param geometries geometries to place in the leaf
     * @return leaf node with all input geometries
     */
    private static Geometries createLeaf(List<Intersectable> geometries) {
        Geometries leaf = new Geometries();
        for (Intersectable geo : geometries) {
            leaf.add(geo);
        }
        return leaf;
    }

    /**
     * Splits a geometry list into two halves by index.
     *
     * @param geometries geometries to split
     * @return split result with left and right halves
     */
    private static SplitResult splitInHalf(List<Intersectable> geometries) {
        int mid = geometries.size() / 2;
        return new SplitResult(
                new ArrayList<>(geometries.subList(0, mid)),
                new ArrayList<>(geometries.subList(mid, geometries.size()))
        );
    }
}