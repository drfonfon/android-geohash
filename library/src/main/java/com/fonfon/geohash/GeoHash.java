package com.fonfon.geohash;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public final class GeoHash {

    private static final double LATITUDE_MAX_ABS = 90.0;
    private static final double LONGITUDE_MAX_ABS = 180.0;

    private static final int MAX_BIT_PRECISION = Long.bitCount(Long.MAX_VALUE) + 1;// max - 64;

    private static final int MAX_CHARACTER_PRECISION = 12;

    private static final int[] BITS = {16, 8, 4, 2, 1};

    private static final int BASE32_BITS = 5;
    private static final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    private static final int MAX_GEO_HASH_BITS_COUNT = BASE32_BITS * MAX_CHARACTER_PRECISION;

    private final static Map<Character, Integer> decodeMap = new HashMap<>();

    static {
        for (int i = 0; i < base32.length(); i++) {
            decodeMap.put(base32.charAt(i), i);
        }
    }

    private long bits = 0;
    private byte significantBits = 0;

    private BoundingBox boundingBox;

    private GeoHash() {
    }

    public static GeoHash fromLocation(Location location, int numberOfCharacters) {
        if (numberOfCharacters > MAX_CHARACTER_PRECISION) {
            throw new IllegalArgumentException("A geohash can only be " + MAX_CHARACTER_PRECISION + " character long.");
        }
        int desiredPrecision = (numberOfCharacters * BASE32_BITS <= MAX_GEO_HASH_BITS_COUNT) ?
                numberOfCharacters * BASE32_BITS : MAX_GEO_HASH_BITS_COUNT;
        return new GeoHash(location.getLatitude(), location.getLongitude(), desiredPrecision);
    }

    public static GeoHash fromString(String geohash) {
        double[] latitudeRange = {-LATITUDE_MAX_ABS, LATITUDE_MAX_ABS};
        double[] longitudeRange = {-LONGITUDE_MAX_ABS, LONGITUDE_MAX_ABS};

        boolean isEvenBit = true;
        GeoHash hash = new GeoHash();

        for (int i = 0; i < geohash.length(); i++) {
            int cd = decodeMap.get(geohash.charAt(i));
            for (int j = 0; j < BASE32_BITS; j++) {
                int mask = BITS[j];
                if (isEvenBit) {
                    divideRangeDecode(hash, longitudeRange, (cd & mask) != 0);
                } else {
                    divideRangeDecode(hash, latitudeRange, (cd & mask) != 0);
                }
                isEvenBit = !isEvenBit;
            }
        }

        setBoundingBox(hash, latitudeRange, longitudeRange);
        hash.bits <<= (MAX_BIT_PRECISION - hash.significantBits);
        return hash;
    }

    private GeoHash(double latitude, double longitude, int desiredPrecision) {

        desiredPrecision = Math.min(desiredPrecision, MAX_BIT_PRECISION);

        boolean isEvenBit = true;
        double[] latitudeRange = {-LATITUDE_MAX_ABS, LATITUDE_MAX_ABS};
        double[] longitudeRange = {-LONGITUDE_MAX_ABS, LONGITUDE_MAX_ABS};

        while (significantBits < desiredPrecision) {
            if (isEvenBit) {
                divideRangeEncode(longitude, longitudeRange);
            } else {
                divideRangeEncode(latitude, latitudeRange);
            }
            isEvenBit = !isEvenBit;
        }

        setBoundingBox(this, latitudeRange, longitudeRange);
        bits <<= (MAX_BIT_PRECISION - desiredPrecision);
    }

    private static void setBoundingBox(GeoHash hash, double[] latitudeRange, double[] longitudeRange) {
        hash.boundingBox = new BoundingBox(
                LocationExt.newLocation(latitudeRange[0], longitudeRange[0]),
                LocationExt.newLocation(latitudeRange[1], longitudeRange[1])
        );
    }

    private void divideRangeEncode(double value, double[] range) {
        double mid = (range[0] + range[1]) / 2;
        if (value >= mid) {
            addOnBitToEnd();
            range[0] = mid;
        } else {
            addOffBitToEnd();
            range[1] = mid;
        }
    }

    private static void divideRangeDecode(GeoHash hash, double[] range, boolean b) {
        double mid = (range[0] + range[1]) / 2;
        if (b) {
            hash.addOnBitToEnd();
            range[0] = mid;
        } else {
            hash.addOffBitToEnd();
            range[1] = mid;
        }
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    private void addOnBitToEnd() {
        significantBits++;
        bits <<= 1;
        bits = bits | 0x1;
    }

    private void addOffBitToEnd() {
        significantBits++;
        bits <<= 1;
    }

    @Override
    public String toString() {
        if (significantBits % BASE32_BITS != 0) {
            throw new IllegalStateException("Cannot convert a geoHash to base32");
        }
        StringBuilder buf = new StringBuilder();

        long firstFiveBitsMask = 0xf800000000000000L;
        long bitsCopy = bits;
        int partialChunks = (int) Math.ceil(((double) significantBits / 5));

        for (int i = 0; i < partialChunks; i++) {
            int pointer = (int) ((bitsCopy & firstFiveBitsMask) >>> 59);
            buf.append(base32.charAt(pointer));
            bitsCopy <<= BASE32_BITS;
        }
        return buf.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof GeoHash) {
            GeoHash other = (GeoHash) obj;
            return other.significantBits == significantBits && other.bits == bits;
        }
        return false;
    }
}
