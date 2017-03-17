package com.fonfon.geohash;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GeoHashTest {

    private GeoHash testhash;
    private Location location;

    @Before
    public void setUp() throws Exception {

        location = new Location("geohash");
        location.setLatitude(53.2030476);
        location.setLongitude(45.0324948);

        testhash = GeoHash.fromLocation(location, 9);
    }

    @Test
    public void fromLocationTest() throws Exception {
        assertEquals(GeoHash.fromLocation(location, 9).toString(), "v12n8trdj");
        assertEquals(GeoHash.fromLocation(location, 8).toString(), "v12n8trd");
    }

    @Test
    public void fromStringTest() throws Exception {
        GeoHash hash = GeoHash.fromString("v12n8");
        assertEquals(hash.getCenter().getLatitude(), 53.19580078);
        assertEquals(hash.getCenter().getLongitude(), 45.02197266);
    }

    @Test
    public void getCenterTest() throws Exception {
        assertEquals(testhash.getCenter().getLatitude(), 53.20303202);
        assertEquals(testhash.getCenter().getLongitude(), 45.03250837);
    }

    @Test
    public void nextTest() throws Exception {
        assertEquals(testhash.next().toString(), "v12n8trdk");
    }

    @Test
    public void next1Test() throws Exception {
        assertEquals(testhash.next(2).toString(), "v12n8trdl");
    }

    @Test
    public void prevTest() throws Exception {
        assertEquals(testhash.prev().toString(), "v12n8trdi");
    }

    @Test
    public void getAdjacentTest() throws Exception {
        String[] hashs = new String[]{
                "v12n8trdm", "v12n8trdq", "v12n8trdn",
                "v12n8tr9y", "v12n8tr9v", "v12n8tr9u",
                "v12n8trdh", "v12n8trk"
        };
        GeoHash[] geoHashs = testhash.getAdjacent();
        for (int i = 0; i < geoHashs.length; i++) {
            assertEquals(geoHashs[i].toString(), hashs[i]);
        }
    }

    @Test
    public void getNorthernNeighbourTest() throws Exception {
        assertEquals(testhash.getNorthernNeighbour().toString(), "v12n8trdm");
    }

    @Test
    public void getSouthernNeighbourTest() throws Exception {
        assertEquals(testhash.getSouthernNeighbour().toString(), "v12n8tr9v");
    }

    @Test
    public void getEasternNeighbourTest() throws Exception {
        assertEquals(testhash.getEasternNeighbour().toString(), "v12n8trdh");
    }

    @Test
    public void getWesternNeighbourTest() throws Exception {
        assertEquals(testhash.getWesternNeighbour().toString(), "v12n8trdn");
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals(testhash.toString(), "v12n8trdj");
    }

    @Test
    public void equalsTest() throws Exception {
        GeoHash hash = GeoHash.fromLocation(location, 9);
        GeoHash hash1 = GeoHash.fromString("v12n8trdj");

        assertEquals(testhash.equals(null), false);
        assertEquals(testhash.equals(testhash), true);
        assertEquals(testhash.equals(hash), true);
        assertEquals(testhash.equals(hash1), true);
    }

}