package com.louis.mynavi.mime;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.louis.mynavi.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}