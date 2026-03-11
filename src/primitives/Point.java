package primitives;

public class Point {
    final Double3 _xyz;

    public static final Point ZERO = new Point(Double3.ZERO);

    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    public double distanceSquared(Point other) {
        Double3 diff = _xyz.subtract(other._xyz);
        return diff._d1() * diff._d1() + diff._d2() * diff._d2() + diff._d3() * diff._d3();
    }

    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && _xyz.equals(((Point) obj)._xyz);
    }

    @Override
    public int hashCode() {
        return _xyz.hashCode();
    }

    @Override
    public String toString() {
        return "Point" + _xyz;
    }
}
