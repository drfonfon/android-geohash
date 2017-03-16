package com.fonfon.geohash;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class BoundingBox implements Parcelable {

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

    protected BoundingBox(Parcel in) {
        minLatitude = in.readDouble();
        maxLatitude = in.readDouble();
        minLongitude = in.readDouble();
        maxLongitude = in.readDouble();
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

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if (this == obj) return true;
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

    public static final Creator<BoundingBox> CREATOR = new Creator<BoundingBox>() {
        @Override
        public BoundingBox createFromParcel(Parcel in) {
            return new BoundingBox(in);
        }

        @Override
        public BoundingBox[] newArray(int size) {
            return new BoundingBox[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(minLatitude);
        dest.writeDouble(maxLatitude);
        dest.writeDouble(minLongitude);
        dest.writeDouble(maxLongitude);
    }
}
