package com.fonfon.geohash.sample;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.fonfon.geohash.GeoHash;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private MapView mapView;
    private GoogleMap map;

    private GeoHash geoHash;

    private AppCompatTextView hashText;
    private GridLayout ajGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(getActivity());

        hashText = (AppCompatTextView) view.findViewById(R.id.hash_string);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        view.findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (geoHash != null) {
                    geoHash = geoHash.next();
                    updateGeohash();
                }
            }
        });

        view.findViewById(R.id.button_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (geoHash != null) {
                    geoHash = geoHash.prev();
                    updateGeohash();
                }
            }
        });

        ajGrid = (GridLayout) view.findViewById(R.id.aj_grid);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        int zoom = (int) ((map.getCameraPosition().zoom / map.getMaxZoomLevel()) * GeoHash.MAX_CHARACTER_PRECISION) - 2;
        zoom = zoom > 0 ? zoom : 1;
        geoHash = GeoHash.fromLocation(latLngToLocation(latLng), zoom);
        updateGeohash();
    }

    private void updateGeohash() {
        hashText.setText(geoHash.toString());
        map.clear();

        GeoHash[] aj = geoHash.getAdjacent();

        for (int i = 0; i < ajGrid.getChildCount(); i++) {
            AppCompatTextView text = (AppCompatTextView) ajGrid.getChildAt(i);
            switch (i) {
                case 0:
                    text.setText(aj[7].toString());
                    break;
                case 1:
                    text.setText(aj[0].toString());
                    break;
                case 2:
                    text.setText(aj[1].toString());
                    break;
                case 3:
                    text.setText(aj[6].toString());
                    break;
                case 4:
                    text.setText(geoHash.toString());
                    break;
                case 5:
                    text.setText(aj[2].toString());
                    break;
                case 6:
                    text.setText(aj[5].toString());
                    break;
                case 7:
                    text.setText(aj[4].toString());
                    break;
                case 8:
                    text.setText(aj[3].toString());
                    break;
            }
        }

        Location[] locations = new Location[]{
                geoHash.getBoundingBox().getUpperLeft(),
                geoHash.getBoundingBox().getUpperRight(),
                geoHash.getBoundingBox().getLowerRight(),
                geoHash.getBoundingBox().getLowerLeft()
        };
        locationCorrect(locations);

        map.addPolygon(new PolygonOptions()
                .add(locationToLatLng(locations[0]))
                .add(locationToLatLng(locations[1]))
                .add(locationToLatLng(locations[2]))
                .add(locationToLatLng(locations[3]))
                .fillColor(Color.argb(100, 125, 200, 200))
                .strokeColor(Color.BLUE)
        );
    }

    //Can not display coordinate -90
    private void locationCorrect(Location[] locations) {
        for (Location location : locations) {
            if (Math.abs(location.getLatitude()) == 90.0) {
                if (location.getLatitude() > 0) {
                    location.setLatitude(89.99);
                } else {
                    location.setLatitude(-89.99);
                }
            }
            if (Math.abs(location.getLongitude()) == 180.0) {
                if (location.getLongitude() > 0) {
                    location.setLongitude(179.99);
                } else {
                    location.setLongitude(-179.99);
                }
            }
        }
    }

    private Location latLngToLocation(LatLng latLng) {
        Location location = new Location("map_geocoder_example");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    private LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
