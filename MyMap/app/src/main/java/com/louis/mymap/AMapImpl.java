package com.louis.mymap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;


public class AMapImpl implements IMap {
    private static final String TAG = "AMapImpl";
    private AMap aMap;
    private MapView mapView;

    @Override
    public void initMap(ViewGroup mapViewContainer, Bundle savedInstanceState) {
        Context context = mapViewContainer.getContext();
        //隐私合规
        MapsInitializer.updatePrivacyAgree(context, true);
        MapsInitializer.updatePrivacyShow(context, true, true);

        //创建地图视图
        mapView = new MapView(context);
        mapViewContainer.addView(mapView);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        //获取地图实例
        aMap = mapView.getMap();
        if (aMap == null) {
            Log.e(TAG, "aMap is null");
            return;
        }
        aMap.setTrafficEnabled(true);// 显示实时交通状况


        // 地图点击事件
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 处理地图点击，latLng为点击坐标
                Log.d(TAG, "地图点击: " + latLng.latitude + ", " + latLng.longitude);
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 处理标记物点击，返回true表示消费该事件
                Log.d(TAG, "Marker 点击: " + marker.getTitle());
                return false;
            }
        });

        //普通地图：AMap.MAP_TYPE_NORMAL
        //卫星地图：AMap.MAP_TYPE_SATELLITE
        //夜间模式：AMap.MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

    }

    @Override
    public void addMarker(MapMarker mapMarker) {
        if (aMap != null) {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(mapMarker.getLatitude(), mapMarker.getLongitude()))
                    .title(mapMarker.getTitle())
                    .snippet(mapMarker.getSnippet())
                    .draggable(true);

            if (mapMarker.getCustomView() != null) {
                options.icon(BitmapDescriptorFactory.fromView(mapMarker.getCustomView()));
            }
            aMap.addMarker(options);
//            markers.put(mapMarker.getId(), aMarker);

        }
    }

    @Override
    public void moveCamera(double latitude, double longitude, float zoom) {
        if (aMap != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
    }

    @Override
    public void setMapViewType(int mapViewType) {
        int mapType = AMap.MAP_TYPE_NORMAL;
        if (aMap != null) {
            switch (mapViewType) {
                case MapViewType.NORMAL:
                    mapType = AMap.MAP_TYPE_NORMAL;
                    break;
                case MapViewType.SATELLITE:
                    mapType = AMap.MAP_TYPE_SATELLITE;
                    break;
                case MapViewType.NIGHT:
                    mapType = AMap.MAP_TYPE_NIGHT;
                    break;
                case MapViewType.NAVI:
                    mapType = AMap.MAP_TYPE_NAVI;
                    break;
                case MapViewType.NAVI_NIGHT:
                    mapType = AMap.MAP_TYPE_NAVI_NIGHT;
                    break;
                default:
                    break;
            }
            aMap.setMapType(mapType);
        }
    }
}
