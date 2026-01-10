package com.louis.mymap;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IMap {

    void initMap(ViewGroup mapViewContainer, Bundle savedInstanceState);

    void addMarker(MapMarker mapMarker);

    void moveCamera(double latitude, double longitude, float zoom);

    void setMapViewType(@MapViewType int mapViewType);

//     void onCreate(Context context);
//     void onSaveInstanceState(Context context);

    @IntDef({MapViewType.NORMAL, MapViewType.SATELLITE, MapViewType.NIGHT, MapViewType.NAVI,  MapViewType.NAVI_NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface MapViewType {
        int NORMAL = 0;     // 普通地图
        int SATELLITE = 1;  // 卫星地图
        int NIGHT = 2;      // 夜间模式
        int NAVI = 3;       // 导航模式
        int NAVI_NIGHT = 4;       // 夜间导航模式
    }
}
