package com.fonfon.geohash;

public class TwoGeoHashBoundingBox {

    private BoundingBox boundingBox;
    private GeoHash topLeftHash;
    private GeoHash bottomRightHash;

    /**
     * Generate {@link TwoGeoHashBoundingBox} from
     *
     * @param box {@link BoundingBox}
     * @param numberOfCharacters max characters count - 12
     * @return new {@link TwoGeoHashBoundingBox}
     */
    public static TwoGeoHashBoundingBox fromBoundingBox(BoundingBox box, int numberOfCharacters) {
        GeoHash topLeft = GeoHash.fromLocation(box.getTopLeft(), numberOfCharacters);
        GeoHash bottomRight = GeoHash.fromLocation(box.getBottomRight(), numberOfCharacters);
        return new TwoGeoHashBoundingBox(topLeft, bottomRight);
    }

    /**
     * Generate {@link TwoGeoHashBoundingBox} from
     * @param base32 geoash string text
     * @return new {@link TwoGeoHashBoundingBox}
     */
    public static TwoGeoHashBoundingBox fromString(String base32) {
        if (base32.length() > GeoHash.MAX_CHARACTER_PRECISION) {
            throw new IllegalArgumentException(
                    "A geohash can only be " + GeoHash.MAX_CHARACTER_PRECISION + " character long.");
        }
        boolean isEven = (base32.length() % 2) == 1;
        GeoHash topLeft = GeoHash.fromString(base32 + (isEven ? "p" : "b"));
        GeoHash bottomRight = GeoHash.fromString(base32 + (isEven ? "b" : "p"));
        return new TwoGeoHashBoundingBox(topLeft, bottomRight);
    }

    /**
     * Generate {@link TwoGeoHashBoundingBox} from
     * @param geoHash parent {@link GeoHash}
     * @return new {@link TwoGeoHashBoundingBox}
     */
    public static TwoGeoHashBoundingBox fromGeoHash(GeoHash geoHash) {
        String hash =geoHash.toString();
        boolean isEven = (hash.length() % 2) == 1;
        GeoHash topLeft = GeoHash.fromString(hash + (isEven ? "p" : "b"));
        GeoHash bottomRight = GeoHash.fromString(hash + (isEven ? "b" : "p"));
        return new TwoGeoHashBoundingBox(topLeft, bottomRight);
    }

    /**
     * Generate {@link TwoGeoHashBoundingBox} from
     * @param base32 TwoGeoHashBoundingBox string
     * @return new {@link TwoGeoHashBoundingBox}
     */
    public static TwoGeoHashBoundingBox fromThoGeoHashBoundingBoxString(String base32) {
        String bottomLeft = base32.substring(0, base32.length() / 2);
        String topRight = base32.substring(base32.length() / 2);
        return new TwoGeoHashBoundingBox(GeoHash.fromString(bottomLeft), GeoHash.fromString(topRight));
    }

    /**
     * Generate {@link TwoGeoHashBoundingBox} from
     * @param topLeftHash top left bounding box {@link GeoHash}
     * @param bottomRightHash bottom right bounding box {@link GeoHash}
     */
    public TwoGeoHashBoundingBox(GeoHash topLeftHash, GeoHash bottomRightHash) {
        if (topLeftHash.getSignificantBits() != bottomRightHash.getSignificantBits()) {
            throw new IllegalArgumentException("Hashes have different precisions!");
        }
        this.topLeftHash = topLeftHash;
        this.bottomRightHash = bottomRightHash;

        this.boundingBox = new BoundingBox(
                topLeftHash.getBoundingBox().getTopLeft(),
                bottomRightHash.getBoundingBox().getBottomRight());
    }

    /**
     * @return bounding box
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return top left bounding box {@link GeoHash}
     */
    public GeoHash getTopLeftHash() {
        return topLeftHash;
    }

    /**
     * @return bottom right bounding box {@link GeoHash}
     */
    public GeoHash getBottomRightHash() {
        return bottomRightHash;
    }

    @Override
    public String toString() {
        return topLeftHash.toString() + bottomRightHash.toString();
    }

}
