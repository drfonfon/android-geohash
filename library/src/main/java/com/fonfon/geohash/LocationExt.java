package com.fonfon.geohash;

import android.location.Location;

final class LocationExt {

    private static final String PROVIDER = "geohash";

    static Location newLocation(double latitude, double longitude) {
        Location location = new Location(PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}
