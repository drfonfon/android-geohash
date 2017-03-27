package com.fonfon.geohash;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TwoGeoHashBoundingBoxTest {

    private TwoGeoHashBoundingBox tHBox;
    private GeoHash geoHash0;
    private GeoHash geoHash1;
    private static final double EPS = 0.00000001;

    @Before
    public void setUp() throws Exception {
        geoHash0 = GeoHash.fromString("v12n8jq");
        geoHash1 = GeoHash.fromString("v12n8kb");
        tHBox = new TwoGeoHashBoundingBox(geoHash0, geoHash1);
    }

    @Test
    public void fromBoundingBoxTest() throws Exception {
        TwoGeoHashBoundingBox box = TwoGeoHashBoundingBox.fromBoundingBox(
                new BoundingBox(
                        LocationExt.newLocation(53.203643, 45.008654),
                        LocationExt.newLocation(53.200764, 45.011658)), 7);

        assertEquals(box.toString(), "v12n8jqv12n8kb");
    }

    @Test
    public void fromStringTest() throws Exception {
        TwoGeoHashBoundingBox box = TwoGeoHashBoundingBox.fromString("v12n8jp");
        assertEquals(box.toString(), "v12n8jppv12n8jpb");
        TwoGeoHashBoundingBox box1 = TwoGeoHashBoundingBox.fromString("v12n8jpp");
        assertEquals(box1.toString(), "v12n8jppbv12n8jppp");
    }

    @Test
    public void fromTwoGeoHashBoundingBoxString() throws Exception {
        TwoGeoHashBoundingBox box = TwoGeoHashBoundingBox.fromThoGeoHashBoundingBoxString("v12n8jppv12n8jpn");
        assertEquals(box.getBottomRightHash().toString(), "v12n8jpn");
    }

    @Test
    public void fromGeohashTest() throws Exception {
        TwoGeoHashBoundingBox box = TwoGeoHashBoundingBox.fromGeoHash(GeoHash.fromString("v12n8jpp"));
        assertEquals(box.toString(), "v12n8jppbv12n8jppp");
    }

    @Test
    public void getBoundingBoxTest() throws Exception {
        assertEquals(tHBox.getBoundingBox().getMinLatitude(), geoHash1.getBoundingBox().getMinLatitude(), EPS);
        assertEquals(tHBox.getBoundingBox().getMinLongitude(), geoHash0.getBoundingBox().getMinLongitude(), EPS);
        assertEquals(tHBox.getBoundingBox().getMaxLatitude(), geoHash0.getBoundingBox().getMaxLatitude(), EPS);
        assertEquals(tHBox.getBoundingBox().getMaxLongitude(), geoHash1.getBoundingBox().getMaxLongitude(), EPS);
    }

    @Test
    public void getTopLeftTest() throws Exception {
        assertEquals(tHBox.getTopLeftHash().toString(), "v12n8jq");
    }

    @Test
    public void getBottomRightTest() throws Exception {
        assertEquals(tHBox.getBottomRightHash().toString(), "v12n8kb");
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals(tHBox.toString(), "v12n8jqv12n8kb");
    }

}