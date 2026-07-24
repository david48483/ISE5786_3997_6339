package geometries.impl; // Or geometries.impl, depending on your project structure

import geometries.api.Intersectable;
import primitives.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for creating a Bounding Volume Hierarchy (BVH) tree.
 * It uses the Surface Area Heuristic (SAH) algorithm with bins to optimize ray tracing.
 */
public class BvhBuilder {

    /**
     * The number of bins to use for the SAH algorithm.
     * 16 is a good balance between building speed and rendering performance.
     */
    private static int BINS_COUNT = 16;

    public void setBINS(int count) {
        BINS_COUNT = count;
    }

    /**
     * A simple helper class to represent a "Bin" (a bucket) for the SAH algorithm.
     */
    private static class Bin {
        AABB bounds = null;
        int count = 0;

        /**
         * Adds a geometry's bounding box to this bin.
         *
         * @param box the bounding box of the geometry
         */
        void add(AABB box) {
            count++;
            if (bounds == null) {
                bounds = box; // Copy the box if it is the first one
            } else {
                bounds = bounds.union(box); // Merge with the existing box
            }
        }
    }

    /**
     * Builds the main BVH tree from a list of geometries.
     * It separates infinite geometries (like planes) from finite ones.
     *
     * @param allGeometries the full list of geometries in the scene
     * @return a single Geometries object containing the optimized tree
     */
    protected static Geometries buildSahTree(List<Intersectable> allGeometries) {
        List<Intersectable> infiniteGeometries = new ArrayList<>();
        List<Intersectable> finiteGeometries = new ArrayList<>();

        // Step 1: Separate geometries.
        // Planes do not have a bounding box (it is null), so they go to infiniteGeometries.
        for (Intersectable geo : allGeometries) {
            if (geo.getBoundingBox() == null) {
                infiniteGeometries.add(geo);
            } else {
                finiteGeometries.add(geo);
            }
        }

        Geometries root = new Geometries();

        // Step 2: Add all infinite geometries (Planes) directly to the root level.
        for (Intersectable geo : infiniteGeometries) {
            root.add(geo);
        }

        // Step 3: Build the SAH tree for the finite geometries and add it to the root.
        if (!finiteGeometries.isEmpty()) {
            root.add(buildNode(finiteGeometries));
        }

        return root;
    }

    /**
     * Recursively builds a BVH node using the Binned SAH algorithm.
     *
     * @param geometries the list of finite geometries to organize
     * @return a Geometries object representing a branch in the tree
     */
    private static Geometries buildNode(List<Intersectable> geometries) {
        int size = geometries.size();

        // Base case: If there are 2 or fewer geometries, stop splitting and make a leaf.
        if (size <= 20) {
            return createLeaf(geometries);
        }

        // Step 1: Find the overall bounding box of all objects to know their spread.
        AABB totalBox = geometries.getFirst().getBoundingBox();
        for (int i = 1; i < size; i++) {
            totalBox = totalBox.union(geometries.get(i).getBoundingBox());
        }

        // Step 2: Find the longest axis to split (0 = X, 1 = Y, 2 = Z).
        double xLength = totalBox.getMax().getX() - totalBox.getMin().getX();
        double yLength = totalBox.getMax().getY() - totalBox.getMin().getY();
        double zLength = totalBox.getMax().getZ() - totalBox.getMin().getZ();

        int axis = 0;
        if (yLength > xLength && yLength > zLength) axis = 1;
        else if (zLength > xLength && zLength > yLength) axis = 2;

        // Find the minimum and maximum center points along the chosen axis.
        double minCenter = Double.MAX_VALUE;
        double maxCenter = -Double.MAX_VALUE;

        for (Intersectable geo : geometries) {
            double center = getCenter(geo.getBoundingBox(), axis);
            if (center < minCenter) minCenter = center;
            if (center > maxCenter) maxCenter = center;
        }

        // If all objects are at the exact same place, we cannot split them by space.
        // Just split the list in half.
        if (minCenter == maxCenter) {
            return splitInHalf(geometries);
        }

        // Step 3: Create the bins and put geometries into them.
        Bin[] bins = new Bin[BINS_COUNT];
        for (int i = 0; i < BINS_COUNT; i++) {
            bins[i] = new Bin();
        }

        for (Intersectable geo : geometries) {
            double center = getCenter(geo.getBoundingBox(), axis);
            // Calculate which bin the geometry belongs to (0 to BINS_COUNT - 1).
            int binIndex = (int) (((center - minCenter) / (maxCenter - minCenter)) * BINS_COUNT);
            if (binIndex == BINS_COUNT) binIndex = BINS_COUNT - 1; // Fix edge case

            bins[binIndex].add(geo.getBoundingBox());
        }

        // Step 4: Calculate SAH cost for each possible split between the bins.
        double minCost = Double.MAX_VALUE;
        int bestSplit = -1;

        // There are BINS_COUNT - 1 possible ways to split the bins.
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

            // Calculate cost: (Left Surface Area * Left Count) + (Right Surface Area * Right Count)
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

        // Step 5: Split the geometries into two lists based on the best split found.
        if (bestSplit == -1) {
            // Fallback if we couldn't find a good split.
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

        // Edge case: if one side is empty, just split in half to avoid infinite recursion.
        if (leftList.isEmpty() || rightList.isEmpty()) {
            return splitInHalf(geometries);
        }

        // Step 6: Recursively build the left and right branches.
        Geometries parentNode = new Geometries();
        parentNode.add(buildNode(leftList));
        parentNode.add(buildNode(rightList));

        return parentNode;
    }

    /**
     * Helper method to calculate the surface area of a bounding box.
     *
     * @param box the bounding box
     * @return the surface area, or 0 if the box is null
     */
    private static double calculateSurfaceArea(AABB box) {
        if (box == null) return 0;
        double x = box.getMax().getX() - box.getMin().getX();
        double y = box.getMax().getY() - box.getMin().getY();
        double z = box.getMax().getZ() - box.getMin().getZ();
        return 2.0 * (x * y + y * z + z * x);
    }

    /**
     * Helper method to get the center point of a bounding box along a specific axis.
     *
     * @param box  the bounding box
     * @param axis the axis (0 for X, 1 for Y, 2 for Z)
     * @return the center coordinate
     */
    private static double getCenter(AABB box, int axis) {
        switch (axis) {
            case 0:
                return (box.getMin().getX() + box.getMax().getX()) / 2.0;
            case 1:
                return (box.getMin().getY() + box.getMax().getY()) / 2.0;
            case 2:
                return (box.getMin().getZ() + box.getMax().getZ()) / 2.0;
            default:
                return 0;
        }
    }

    /**
     * Helper method to create a leaf node containing a few geometries.
     *
     * @param geometries the list of geometries
     * @return a Geometries object containing these geometries
     */
    private static Geometries createLeaf(List<Intersectable> geometries) {
        Geometries leaf = new Geometries();
        for (Intersectable geo : geometries) {
            leaf.add(geo);
        }
        return leaf;
    }

    /**
     * Fallback method to simply split the list in half if SAH cannot find a good split.
     *
     * @param geometries the list to split
     * @return a Geometries object with two equal branches
     */
    private static Geometries splitInHalf(List<Intersectable> geometries) {
        int mid = geometries.size() / 2;
        List<Intersectable> leftList = geometries.subList(0, mid);
        List<Intersectable> rightList = geometries.subList(mid, geometries.size());

        Geometries parentNode = new Geometries();
        parentNode.add(buildNode(leftList));
        parentNode.add(buildNode(rightList));
        return parentNode;
    }
}