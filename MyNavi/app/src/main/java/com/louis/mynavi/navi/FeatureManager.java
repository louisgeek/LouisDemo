package com.louis.mynavi.navi;

import java.util.*;

public class FeatureManager {
    private NaviManager nav;
    private Map<String, String[]> features = new HashMap<>();
    
    public FeatureManager(NaviManager nav) {
        this.nav = nav;
    }

    
    public void defineFeature(String name, String... tags) {
        features.put(name, tags);
        for (int i = 0; i < tags.length - 1; i++) {
            nav.addEdge(tags[i], tags[i + 1]);
        }
    }
    
    public void start(String feature, androidx.fragment.app.Fragment fragment) {
        String[] tags = features.get(feature);
        if (tags != null && tags.length > 0) {
            nav.to(tags[0], fragment);
        }
    }
    
    public void backToInFeature(String feature, String tag) {
        nav.backTo(tag);
    }
}