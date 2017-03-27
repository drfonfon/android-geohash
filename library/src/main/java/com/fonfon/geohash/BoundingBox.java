package com.fonfon.geohash;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class BoundingBox implements Parcelable {

    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    /**
     * Generate {@link BoundingBox} from
     *
     * @param p1 first {@link Location}
     * @param p2 second {@link Location}
     */
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

    /**
     * @return top left box location
     */
    public Location getTopLeft() {
        return LocationExt.newLocation(maxLatitude, minLongitude);
    }

    /**
     * @return top right box location
     */
    public Location getTopRight() {
        return LocationExt.newLocation(maxLatitude, maxLongitude);
    }

    /**
     * @return bottom left box location
     */
    public Location getBottomLeft() {
        return LocationExt.newLocation(minLatitude, minLongitude);
    }

    /**
     * @return bottom right box location
     */
    public Location getBottomRight() {
        return LocationExt.newLocation(minLatitude, maxLongitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
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

    /**
     * @param point {@link Location} object
     * @return location contains in box
     */
    public boolean contains(Location point) {
        return point.getLatitude() >= minLatitude
                && point.getLongitude() >= minLongitude
                && point.getLatitude() <= maxLatitude
                && point.getLongitude() <= maxLongitude;
    }

    /**
     * @param other BoundingBox
     * @return intersects of two boxes
     */
    public boolean intersects(BoundingBox other) {
        return !(other.minLongitude > maxLongitude
                || other.maxLongitude < minLongitude
                || other.minLatitude > maxLatitude
                || other.maxLatitude < minLatitude);
    }

    @Override
    public String toString() {
        return getTopLeft().toString() + " -> " + getBottomRight().toString();
    }

    /**
     * @return center box point
     */
    public Location getCenterPoint() {
        return LocationExt.newLocation((minLatitude + maxLatitude) / 2, (minLongitude + maxLongitude) / 2);
    }

    /**
     * @return maximum box latitude
     */
    public double getMaxLatitude() {
        return maxLatitude;
    }

    /**
     * @return maximum box longitude
     */
    public double getMaxLongitude() {
        return maxLongitude;
    }

    /**
     * @return minimum box latitude
     */
    public double getMinLatitude() {
        return minLatitude;
    }

    /**
     * @return minimum box longitude
     */
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
