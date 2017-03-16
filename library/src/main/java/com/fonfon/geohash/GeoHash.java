package com.fonfon.geohash;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public final class GeoHash implements Parcelable {

    public static final int MAX_CHARACTER_PRECISION = 12;

    private static final long FIRST_BIT_FLAGGED = 0x8000000000000000L;
    private static final double LATITUDE_MAX_ABS = 90.0;
    private static final double LONGITUDE_MAX_ABS = 180.0;
    private static final int MAX_BIT_PRECISION = Long.bitCount(Long.MAX_VALUE) + 1;// max - 64;
    private static final int BASE32_BITS = 5;
    private static final int[] BITS = {16, 8, 4, 2, 1};
    private static final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    private static final int MAX_GEO_HASH_BITS_COUNT = BASE32_BITS * MAX_CHARACTER_PRECISION;

    private static final Map<Character, Integer> decodeMap = new HashMap<>();

    private long bits = 0;
    private byte significantBits = 0;
    private BoundingBox boundingBox;

    static {
        for (int i = 0; i < base32.length(); i++)
            decodeMap.put(base32.charAt(i), i);
    }

    private GeoHash() {
    }

    protected GeoHash(Parcel in) {
        bits = in.readLong();
        significantBits = in.readByte();
        boundingBox = in.readParcelable(BoundingBox.class.getClassLoader());
    }

    /**
     * Generate {@link GeoHash} from
     *
     * @param location           {@link Location} object
     * @param numberOfCharacters max characters count - 12
     * @return new {@link GeoHash}
     */
    public static GeoHash fromLocation(Location location, int numberOfCharacters) {
        if (numberOfCharacters > MAX_CHARACTER_PRECISION) {
            throw new IllegalArgumentException(
                    "A geohash can only be " + MAX_CHARACTER_PRECISION + " character long.");
        }
        int desiredPrecision = (numberOfCharacters * BASE32_BITS <= MAX_GEO_HASH_BITS_COUNT) ?
                numberOfCharacters * BASE32_BITS : MAX_GEO_HASH_BITS_COUNT;
        return new GeoHash(location.getLatitude(), location.getLongitude(), desiredPrecision);
    }

    /**
     * Generate {@link GeoHash} from
     *
     * @param geohash geoHash {@link String}
     * @return new {@link GeoHash}
     */
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

    /**
     * @return geohash {@link BoundingBox}
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return geohash {@link BoundingBox} center point
     */
    public Location getCenter() {
        return boundingBox.getCenterPoint();
    }

    /**
     * @param step next hash stepCount
     * @return {@link GeoHash}
     */
    public GeoHash next(int step) {
        return fromOrd(ord() + step, significantBits);
    }

    /**
     * Return next one step hash
     *
     * @return {@link GeoHash}
     */
    public GeoHash next() {
        return next(1);
    }

    /**
     * Return previous one step hash
     *
     * @return {@link GeoHash}
     */
    public GeoHash prev() {
        return next(-1);
    }

    /**
     * @return 8 adjacent {@link GeoHash} for this one. They are in the following order:
     * N, NE, E, SE, S, SW, W, NW
     */
    public GeoHash[] getAdjacent() {
        GeoHash northern = getNorthernNeighbour();
        GeoHash eastern = getEasternNeighbour();
        GeoHash southern = getSouthernNeighbour();
        GeoHash western = getWesternNeighbour();
        return new GeoHash[]{
                northern,
                northern.getEasternNeighbour(),
                eastern,
                southern.getEasternNeighbour(),
                southern,
                southern.getWesternNeighbour(),
                western,
                northern.getWesternNeighbour()
        };
    }

    /**
     * @return N adjacent hash
     */
    public GeoHash getNorthernNeighbour() {
        long[] latitudeBits = getRightAlignedLatitudeBits();
        long[] longitudeBits = getRightAlignedLongitudeBits();
        latitudeBits[0] += 1;
        latitudeBits[0] = maskLastNBits(latitudeBits[0], latitudeBits[1]);
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits);
    }

    /**
     * @return S adjacent hash
     */
    public GeoHash getSouthernNeighbour() {
        long[] latitudeBits = getRightAlignedLatitudeBits();
        long[] longitudeBits = getRightAlignedLongitudeBits();
        latitudeBits[0] -= 1;
        latitudeBits[0] = maskLastNBits(latitudeBits[0], latitudeBits[1]);
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits);
    }

    /**
     * @return E adjacent hash
     */
    public GeoHash getEasternNeighbour() {
        long[] latitudeBits = getRightAlignedLatitudeBits();
        long[] longitudeBits = getRightAlignedLongitudeBits();
        longitudeBits[0] += 1;
        longitudeBits[0] = maskLastNBits(longitudeBits[0], longitudeBits[1]);
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits);
    }

    /**
     * @return W adjacent hash
     */
    public GeoHash getWesternNeighbour() {
        long[] latitudeBits = getRightAlignedLatitudeBits();
        long[] longitudeBits = getRightAlignedLongitudeBits();
        longitudeBits[0] -= 1;
        longitudeBits[0] = maskLastNBits(longitudeBits[0], longitudeBits[1]);
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits);
    }

    @Override
    public String toString() {
        if (significantBits % BASE32_BITS != 0) {
            throw new IllegalStateException("Cannot convert a geoHash to base32");
        }
        StringBuilder buf = new StringBuilder();

        long firstFiveBitsMask = 0xf800000000000000L;
        long bitsCopy = bits;
        int partialChunks = (int) Math.ceil(((double) significantBits / BASE32_BITS));

        for (int i = 0; i < partialChunks; i++) {
            int pointer = (int) ((bitsCopy & firstFiveBitsMask) >>> 59);
            buf.append(base32.charAt(pointer));
            bitsCopy <<= BASE32_BITS;
        }
        return buf.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof GeoHash) {
            GeoHash other = (GeoHash) obj;
            return other.significantBits == significantBits && other.bits == bits;
        }
        return false;
    }

    /**
     * Private metods ----------------------------------------------------
     */

    private static GeoHash fromLongValue(long hashVal, int significantBits) {
        double[] latitudeRange = {-LATITUDE_MAX_ABS, LATITUDE_MAX_ABS};
        double[] longitudeRange = {-LONGITUDE_MAX_ABS, LONGITUDE_MAX_ABS};

        boolean isEvenBit = true;
        GeoHash hash = new GeoHash();

        String binaryString = Long.toBinaryString(hashVal);
        while (binaryString.length() < MAX_BIT_PRECISION) {
            binaryString = "0" + binaryString;
        }
        for (int j = 0; j < significantBits; j++) {
            if (isEvenBit) {
                divideRangeDecode(hash, longitudeRange, binaryString.charAt(j) != '0');
            } else {
                divideRangeDecode(hash, latitudeRange, binaryString.charAt(j) != '0');
            }
            isEvenBit = !isEvenBit;
        }

        setBoundingBox(hash, latitudeRange, longitudeRange);
        hash.bits <<= (MAX_BIT_PRECISION - hash.significantBits);
        return hash;
    }

    private static GeoHash fromOrd(long ord, int significantBits) {
        return fromLongValue(ord << MAX_BIT_PRECISION - significantBits, significantBits);
    }

    private static void setBoundingBox(GeoHash hash, double[] latitudeRange, double[] longitudeRange) {
        hash.boundingBox = new BoundingBox(
                LocationExt.newLocation(latitudeRange[0], longitudeRange[0]),
                LocationExt.newLocation(latitudeRange[1], longitudeRange[1])
        );
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

    private void addOnBitToEnd() {
        significantBits++;
        bits <<= 1;
        bits = bits | 0x1;
    }

    private void addOffBitToEnd() {
        significantBits++;
        bits <<= 1;
    }

    private long ord() {
        int insignificantBits = MAX_BIT_PRECISION - significantBits;
        return bits >>> insignificantBits;
    }

    private long[] getRightAlignedLatitudeBits() {
        long copyOfBits = bits << 1;
        long value = extractEverySecondBit(copyOfBits, getNumberOfLatLonBits()[0]);
        return new long[]{value, getNumberOfLatLonBits()[0]};
    }

    private long[] getRightAlignedLongitudeBits() {
        long copyOfBits = bits;
        long value = extractEverySecondBit(copyOfBits, getNumberOfLatLonBits()[1]);
        return new long[]{value, getNumberOfLatLonBits()[1]};
    }

    private long extractEverySecondBit(long copyOfBits, int numberOfBits) {
        long value = 0;
        for (int i = 0; i < numberOfBits; i++) {
            if ((copyOfBits & FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED) {
                value |= 0x1;
            }
            value <<= 1;
            copyOfBits <<= 2;
        }
        value >>>= 1;
        return value;
    }

    private int[] getNumberOfLatLonBits() {
        if (significantBits % 2 == 0) {
            return new int[]{significantBits / 2, significantBits / 2};
        } else {
            return new int[]{significantBits / 2, significantBits / 2 + 1};
        }
    }

    private long maskLastNBits(long value, long n) {
        long mask = 0xffffffffffffffffL;
        mask >>>= (MAX_BIT_PRECISION - n);
        return value & mask;
    }

    private GeoHash recombineLatLonBitsToHash(long[] latBits, long[] lonBits) {
        GeoHash hash = new GeoHash();
        boolean isEvenBit = false;
        latBits[0] <<= (MAX_BIT_PRECISION - latBits[1]);
        lonBits[0] <<= (MAX_BIT_PRECISION - lonBits[1]);
        double[] latitudeRange = {-LATITUDE_MAX_ABS, LATITUDE_MAX_ABS};
        double[] longitudeRange = {-LONGITUDE_MAX_ABS, LONGITUDE_MAX_ABS};

        for (int i = 0; i < latBits[1] + lonBits[1]; i++) {
            if (isEvenBit) {
                divideRangeDecode(hash, latitudeRange, (latBits[0] & FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED);
                latBits[0] <<= 1;
            } else {
                divideRangeDecode(hash, longitudeRange, (lonBits[0] & FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED);
                lonBits[0] <<= 1;
            }
            isEvenBit = !isEvenBit;
        }
        hash.bits <<= (MAX_BIT_PRECISION - hash.significantBits);
        setBoundingBox(hash, latitudeRange, longitudeRange);
        return hash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(bits);
        dest.writeByte(significantBits);
        dest.writeParcelable(boundingBox, flags);
    }

    public static final Creator<GeoHash> CREATOR = new Creator<GeoHash>() {
        @Override
        public GeoHash createFromParcel(Parcel in) {
            return new GeoHash(in);
        }

        @Override
        public GeoHash[] newArray(int size) {
            return new GeoHash[size];
        }
    };
}
