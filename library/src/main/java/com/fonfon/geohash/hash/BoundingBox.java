package com.fonfon.geohash.hash;

import android.location.Location;

public class BoundingBox {

    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public BoundingBox(Location p1, Location p2) {
        this(p1.getLatitude(), p2.getLatitude(), p1.getLongitude(), p2.getLongitude());
    }

    private BoundingBox(double y1, double y2, double x1, double x2) {
        minLongitude = Math.min(x1, x2);
        maxLongitude = Math.max(x1, x2);
        minLatitude = Math.min(y1, y2);
        maxLatitude = Math.max(y1, y2);
    }

    public Location getUpperLeft() {
        return LocationExt.newLocation(maxLatitude, minLongitude);
    }

    public Location getUpperRight() {
        return LocationExt.newLocation(maxLatitude, maxLongitude);
    }

    public Location getLowerLeft() {
        return LocationExt.newLocation(minLatitude, minLongitude);
    }

    public Location getLowerRight() {
        return LocationExt.newLocation(minLatitude, maxLongitude);
    }

    public double getLatitudeSize() {
        return maxLatitude - minLatitude;
    }

    public double getLongitudeSize() {
        return maxLongitude - minLongitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BoundingBox) {
            BoundingBox that = (BoundingBox) obj;
            return minLatitude == that.minLatitude
                    && minLongitude == that.minLongitude
                    && maxLatitude == that.maxLatitude
                    && maxLongitude == that.maxLongitude;
        }
        return false;
    }

    public boolean contains(Location point) {
        return point.getLatitude() >= minLatitude
                && point.getLongitude() >= minLongitude
                && point.getLatitude() <= maxLatitude
                && point.getLongitude() <= maxLongitude;
    }

    public boolean intersects(BoundingBox other) {
        return !(other.minLongitude > maxLongitude
                || other.maxLongitude < minLongitude
                || other.minLatitude > maxLatitude
                || other.maxLatitude < minLatitude);
    }

    @Override
    public String toString() {
        return getUpperLeft().toString() + " -> " + getLowerRight().toString();
    }

    public Location getCenterPoint() {
        return LocationExt.newLocation((minLatitude + maxLatitude) / 2, (minLongitude + maxLongitude) / 2);
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }
}
