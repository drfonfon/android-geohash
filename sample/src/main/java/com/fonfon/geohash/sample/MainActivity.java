package com.fonfon.geohash.sample;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.fonfon.geohash.hash.GeoHash;
import com.fonfon.geohash.hash.LocationExt;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Location location = LocationExt.newLocation(53.2014645, 45.009858);

        GeoHash hash0 = GeoHash.fromLocation(location, 8);
        GeoHash hash1 = GeoHash.fromLocation(location, 7);
        GeoHash hash2 = GeoHash.fromLocation(location, 6);

        googleMap.addPolygon(new PolygonOptions()
                .add(fromLocation(hash0.getBoundingBox().getUpperLeft()))
                .add(fromLocation(hash0.getBoundingBox().getUpperRight()))
                .add(fromLocation(hash0.getBoundingBox().getLowerRight()))
                .add(fromLocation(hash0.getBoundingBox().getLowerLeft()))
                .fillColor(Color.argb(100, 125, 200, 200))
                .strokeColor(Color.BLUE)
        );

        googleMap.addPolygon(new PolygonOptions()
                .add(fromLocation(hash1.getBoundingBox().getUpperLeft()))
                .add(fromLocation(hash1.getBoundingBox().getUpperRight()))
                .add(fromLocation(hash1.getBoundingBox().getLowerRight()))
                .add(fromLocation(hash1.getBoundingBox().getLowerLeft()))
                .fillColor(Color.argb(100, 125, 200, 200))
                .strokeColor(Color.RED)
        );

        googleMap.addPolygon(new PolygonOptions()
                .add(fromLocation(hash2.getBoundingBox().getUpperLeft()))
                .add(fromLocation(hash2.getBoundingBox().getUpperRight()))
                .add(fromLocation(hash2.getBoundingBox().getLowerRight()))
                .add(fromLocation(hash2.getBoundingBox().getLowerLeft()))
                .fillColor(Color.argb(100, 125, 200, 200))
                .strokeColor(Color.MAGENTA)
        );

        googleMap.addMarker(new MarkerOptions()
                .position(fromLocation(hash0.getBoundingBox().getCenterPoint()))
        );

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fromLocation(hash0.getBoundingBox().getCenterPoint()), 18));

        Log.d("debug", hash0.toString());
    }

    private LatLng fromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
