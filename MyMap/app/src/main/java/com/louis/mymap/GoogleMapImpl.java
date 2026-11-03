package com.louis.mymap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapImpl implements IMap {
    private static final String TAG = "GoogleMapImpl";
    private GoogleMap googleMap;
    private MapView mapView;

    @Override
    public void initMap(ViewGroup mapViewContainer, Bundle savedInstanceState) {
        mapView = new MapView(mapViewContainer.getContext());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;
            }
        });
        mapViewContainer.addView(mapView);
    }
    @Override
    public void addMarker(MapMarker mapMarker) {
        if (googleMap != null) {
            LatLng position = new LatLng(mapMarker.getLatitude(), mapMarker.getLongitude());
            MarkerOptions options = new MarkerOptions()
                    .position(position)
                    .title(mapMarker.getTitle())
                    .snippet(mapMarker.getSnippet());

            if (mapMarker.getCustomView() != null) {
                Bitmap bitmap = createBitmapFromView(mapMarker.getCustomView());
                options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            }
            googleMap.addMarker(options);
        }

    }

    @Override
    public void moveCamera(double lat, double lng, float zoom) {
        if (googleMap != null) {
            LatLng position = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
        }
    }

    @Override
    public void setMapViewType(int mapViewType) {
        int mapType = GoogleMap.MAP_TYPE_NORMAL;
        if (googleMap != null) {
            switch (mapViewType) {
                case MapViewType.NORMAL:
                    mapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                case MapViewType.SATELLITE:
                    mapType = GoogleMap.MAP_TYPE_SATELLITE;
                    break;
                case MapViewType.NIGHT:
                    mapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                case MapViewType.NAVI:
                    mapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                case MapViewType.NAVI_NIGHT:
                    mapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                default:
                    break;
            }
            googleMap.setMapType(mapType);
        }
    }

    private Bitmap createBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
