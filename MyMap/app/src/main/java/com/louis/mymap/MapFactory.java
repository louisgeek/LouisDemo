package com.louis.mymap;

public class MapFactory {
    public static IMap createMap(String mapType) {
        switch (mapType.toLowerCase()) {
            case "amap":
                return new AMapImpl();
            case "google":
                return new GoogleMapImpl();
            default:
                throw new IllegalArgumentException("Unsupported map mapType: " + mapType);
        }
    }
}
