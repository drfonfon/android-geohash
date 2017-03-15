package com.fonfon.geohash.sample;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.fonfon.geohash.GeoHash;

public class ConverterFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private AppCompatEditText editGeohash;
    private AppCompatEditText editLat;
    private AppCompatEditText editLon;
    private AppCompatTextView textPrecision;
    private AppCompatSeekBar seekPrecision;

    private GeoHash geoHash;
    private Location location;

    private boolean isHashPaste = false;
    private boolean isLocationPaste = false;
    private boolean isPrecisionpaste = false;

    private TextWatcher hashTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isHashPaste) {
                geoHash = GeoHash.fromString(editGeohash.getText().toString());
                location = geoHash.getCenter();
                isLocationPaste = true;
                editLat.setText(String.valueOf(location.getLatitude()));
                editLon.setText(String.valueOf(location.getLongitude()));
                isPrecisionpaste = true;
                textPrecision.setText("Precision: " + (geoHash.toString().length() - 1));
                seekPrecision.setProgress(geoHash.toString().length() - 1);
                isLocationPaste = false;
                isPrecisionpaste = false;
            }
        }
    };

    private TextWatcher locationTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isLocationPaste) {
                if (editLat.length() > 0 && editLon.length() > 0) {
                    location.setLatitude(Double.parseDouble(editLat.getText().toString()));
                    location.setLongitude(Double.parseDouble(editLon.getText().toString()));
                    geoHash = GeoHash.fromLocation(location, seekPrecision.getProgress() + 1);
                    isHashPaste = true;
                    editGeohash.setText(geoHash.toString());
                    isHashPaste = false;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_converter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editGeohash = (AppCompatEditText) view.findViewById(R.id.edit_hash);
        editLat = (AppCompatEditText) view.findViewById(R.id.edit_lat);
        editLon = (AppCompatEditText) view.findViewById(R.id.edit_lon);
        textPrecision = (AppCompatTextView) view.findViewById(R.id.text_precision);
        seekPrecision = (AppCompatSeekBar) view.findViewById(R.id.seek_precision);

        editGeohash.addTextChangedListener(hashTextWatcher);
        editLat.addTextChangedListener(locationTextWatcher);
        editLon.addTextChangedListener(locationTextWatcher);
        seekPrecision.setOnSeekBarChangeListener(this);

        location = new Location("geohash_example");
        textPrecision.setText("Precision: " + 1);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!isPrecisionpaste) {
            textPrecision.setText("Precision: " + (progress + 1));
            geoHash = GeoHash.fromLocation(location, progress + 1);
            isHashPaste = true;
            editGeohash.setText(geoHash.toString());
            isHashPaste = false;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
