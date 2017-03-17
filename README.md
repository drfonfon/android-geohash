# Android-Geohash

[![](https://jitpack.io/v/drfonfon/android-geohash.svg)](https://jitpack.io/#drfonfon/android-geohash)

An implementation of Geohashes in Android. The produced hashes, when using character precision (multiples of 5 bits) are compatible to the reference implementation geohash.org.

## Example
```java
  Location location = new Location("geohash");
  location.setLatitude(53.2030476);
  location.setLongitude(45.0324948);
  
  GeoHash hash = GeoHash.fromLocation(location, 9);
  hash.toString(); //"v12n8trdj"
```

