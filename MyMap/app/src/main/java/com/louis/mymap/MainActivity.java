package com.louis.mymap;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.louis.mymap.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private IMap map;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        String mapType = "amap";
        String mapType = "google";
        map = MapFactory.createMap(mapType);

        map.initMap(binding.flContainerMapVew, savedInstanceState);

        binding.fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.moveCamera(39.909, 116.397, 16);
            }
        });

        binding.mapTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int mapViewType = IMap.MapViewType.NORMAL;
                if (checkedId == R.id.map_type_normal) {
                    mapViewType = IMap.MapViewType.NORMAL; // 普通地图
                } else if (checkedId == R.id.map_type_satellite) {
                    mapViewType = IMap.MapViewType.SATELLITE; // 卫星地图
                }
                map.setMapViewType(mapViewType);
            }
        });
        binding.fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.layout_test, null);
//                view.setBackgroundColor(Color.parseColor("#FF4081"));
//                view.setPadding(20, 10, 20, 10);
//
//                map.addMarker(
//                        new MapMarker("2", 31.14, 121.29, "上海")
//                                .customView(view)
//                );
            }
        });
    }
}