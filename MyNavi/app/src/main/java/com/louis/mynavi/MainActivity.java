package com.louis.mynavi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.louis.mynavi.navi.FeatureManager;
import com.louis.mynavi.navi.NaviManager;

public class MainActivity extends AppCompatActivity {
    private NaviManager naviManager;
    private FeatureManager featureManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        naviManager = new NaviManager(getSupportFragmentManager(), R.id.containerId, this);
        featureManager = new FeatureManager(naviManager);
        
        featureManager.defineFeature("user", "A", "B", "C");
        androidx.fragment.app.Fragment fragment = new androidx.fragment.app.Fragment();
        featureManager.start("login", fragment);
    }
    
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            naviManager.back();
        } else {
            super.onBackPressed();
        }
    }
}