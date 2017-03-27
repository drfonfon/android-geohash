package com.fonfon.geohash;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GeoHashTest {

    private GeoHash testhash;
    private Location location;
    private static final double EPS = 0.00000001;

    @Before
    public void setUp() throws Exception {

        location = new Location("geohash");
        location.setLatitude(53.2030476);
        location.setLongitude(45.0324948);

        testhash = GeoHash.fromLocation(location, 9);
    }

    @Test
    public void fromCoordinatesTest() throws Exception {
        assertEquals(GeoHash.fromCoordinates(53.2030476, 45.0324948).toString(), "v12n8trdjnvu");
        assertEquals(GeoHash.fromCoordinates(53.2030476, 45.0324948, 9).toString(), "v12n8trdj");
    }

    @Test
    public void fromLocationTest() throws Exception {
        assertEquals(GeoHash.fromLocation(location, 9).toString(), "v12n8trdj");
        assertEquals(GeoHash.fromLocation(location, 8).toString(), "v12n8trd");
        assertEquals(GeoHash.fromLocation(location).toString(), "v12n8trdjnvu");
    }

    @Test
    public void fromStringTest() throws Exception {
        GeoHash hash = GeoHash.fromString("v12n8");
        assertEquals(Math.abs(hash.getCenter().getLatitude() - 53.19580078) < EPS, true);
        assertEquals(Math.abs(hash.getCenter().getLongitude() - 45.02197266) < EPS, true);
    }

    @Test
    public void getCenterTest() throws Exception {
        assertEquals(Math.abs(testhash.getCenter().getLatitude() - 53.20303202) < EPS, true);
        assertEquals(Math.abs(testhash.getCenter().getLongitude() - 45.03250837) < EPS, true);
    }

    @Test
    public void nextTest() throws Exception {
        assertEquals(testhash.next().toString(), "v12n8trdk");
    }

    @Test
    public void next1Test() throws Exception {
        assertEquals(testhash.next(2).toString(), "v12n8trdm");
    }

    @Test
    public void prevTest() throws Exception {
        assertEquals(testhash.prev().toString(), "v12n8trdh");
    }

    @Test
    public void getAdjacentTest() throws Exception {
        String[] hashs = new String[]{
                "v12n8trdm", "v12n8trdq", "v12n8trdn",
                "v12n8tr9y", "v12n8tr9v", "v12n8tr9u",
                "v12n8trdh", "v12n8trdk"
        };
        GeoHash[] geoHashs = testhash.getAdjacent();
        for (int i = 0; i < geoHashs.length; i++) {
            assertEquals(geoHashs[i].toString(), hashs[i]);
        }
    }

    @Test
    public void getAdjacentBoxTest() throws Exception {
        String[] hashs = new String[]{
                "v12n8trdk", "v12n8trdm", "v12n8trdq",
                "v12n8trdh", "v12n8trdj", "v12n8trdn",
                "v12n8tr9u", "v12n8tr9v", "v12n8tr9y"
        };
        GeoHash[] geoHashs = testhash.getAdjacentBox();
        for (int i = 0; i < geoHashs.length; i++) {
            assertEquals(geoHashs[i].toString(), hashs[i]);
        }
    }

    @Test
    public void getChildHashTests() throws Exception {
        String[] hashs = new String[]{
                "v12n8trdj0","v12n8trdj1", "v12n8trdj2", "v12n8trdj3", "v12n8trdj4", "v12n8trdj5", "v12n8trdj6",
                "v12n8trdj7", "v12n8trdj8", "v12n8trdj9", "v12n8trdjb","v12n8trdjc","v12n8trdjd","v12n8trdje",
                "v12n8trdjf","v12n8trdjg","v12n8trdjh", "v12n8trdjj","v12n8trdjk","v12n8trdjm","v12n8trdjn",
                "v12n8trdjp","v12n8trdjq","v12n8trdjr","v12n8trdjs", "v12n8trdjt","v12n8trdju","v12n8trdjv",
                "v12n8trdjw","v12n8trdjx", "v12n8trdjy","v12n8trdjz"
        };
        GeoHash[] geoHashs = testhash.getChildHashes();
        assertNotNull(geoHashs);
        for (int i = 0; i < geoHashs.length; i++) {
            assertEquals(geoHashs[i].toString(), hashs[i]);
        }

        GeoHash hash = GeoHash.fromLocation(location);
        assertNull(hash.getChildHashes());
    }

    @Test
    public void getParentHashTests() throws Exception {
        assertNotNull(testhash.getParentHash());
        assertEquals(testhash.getParentHash().toString(), "v12n8trd");

        GeoHash hash = GeoHash.fromLocation(location, 1);
        assertNull(hash.getParentHash());
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
        assertEquals(testhash.getEasternNeighbour().toString(), "v12n8trdn");
    }

    @Test
    public void getWesternNeighbourTest() throws Exception {
        assertEquals(testhash.getWesternNeighbour().toString(), "v12n8trdh");
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