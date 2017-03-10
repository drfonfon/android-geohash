package com.fonfon.geohash.hash;

import android.location.Location;

public class LocationExt {

    private static final String PROVIDER = "geohash";

    public static Location newLocation(double latitude, double longitude) {
        Location location = new Location(PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
