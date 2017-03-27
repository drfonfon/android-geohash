package com.fonfon.geohash;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BoundingBoxTest {

    private BoundingBox boxA;
    private BoundingBox boxB;

    private Location locationA;
    private Location locationB;
    private Location locationC;

    @Before
    public void setUp() throws Exception {
        locationA = new Location("geohashTest");
        locationA.setLatitude(10);
        locationA.setLongitude(11);

        locationB = new Location("geohashTest");
        locationB.setLatitude(20);
        locationB.setLongitude(21);

        locationC = new Location("geohashTest");
        locationC.setLatitude(15);
        locationC.setLongitude(16);

        //(20, 11) -- (20, 21)
        //(10, 11) -- (10, 21)
        boxA = new BoundingBox(locationA, locationB);
        boxB = new BoundingBox(locationB, locationC);
    }

    @Test
    public void getUpperLeftTest() throws Exception {
        assertEquals(boxA.getTopLeft().getLatitude(), locationB.getLatitude());
        assertEquals(boxA.getTopLeft().getLongitude(), locationA.getLongitude());
    }

    @Test
    public void getUpperRightTest() throws Exception {
        assertEquals(boxA.getTopRight().getLatitude(), locationB.getLatitude());
        assertEquals(boxA.getTopRight().getLongitude(), locationB.getLongitude());
    }

    @Test
    public void getLowerLeftTest() throws Exception {
        assertEquals(boxA.getBottomLeft().getLatitude(), locationA.getLatitude());
        assertEquals(boxA.getBottomLeft().getLongitude(), locationA.getLongitude());
    }

    @Test
    public void getLowerRightTest() throws Exception {
        assertEquals(boxA.getBottomRight().getLatitude(), locationA.getLatitude());
        assertEquals(boxA.getBottomRight().getLongitude(), locationB.getLongitude());
    }

    @Test
    public void equalsTest() throws Exception {
        BoundingBox boundingBox = new BoundingBox(locationA, locationB);
        assertEquals(boxA.equals(null), false);
        assertEquals(boxA.equals(boxA), true);
        assertEquals(boxA.equals(boundingBox), true);
    }

    @Test
    public void containsTest() throws Exception {
        assertEquals(boxA.contains(locationC), true);
    }

    @Test
    public void intersectsTest() throws Exception {

        Location location1 = new Location("geohash");
        location1.setLatitude(22);
        location1.setLongitude(33);

        Location location2 = new Location("geohash");
        location2.setLatitude(33);
        location2.setLongitude(44);

        BoundingBox box = new BoundingBox(location1, location2);

        assertEquals(boxA.intersects(boxB), true);
        assertEquals(boxA.intersects(box), false);
    }

    @Test
    public void getCenterPointTest() throws Exception {
        assertEquals(boxA.getCenterPoint().getLatitude(), locationC.getLatitude());
        assertEquals(boxA.getCenterPoint().getLongitude(), locationC.getLongitude());
    }

    @Test
    public void getMaxLatitudeTest() throws Exception {
        assertEquals(boxA.getMaxLatitude(), 20.0);
    }

    @Test
    public void getMaxLongitudeTest() throws Exception {
        assertEquals(boxA.getMaxLongitude(), 21.0);
    }

    @Test
    public void getMinLatitudeTest() throws Exception {
        assertEquals(boxA.getMinLatitude(), 10.0);
    }

    @Test
    public void getMinLongitudeTest() throws Exception {
        assertEquals(boxA.getMinLongitude(), 11.0);
    }

}