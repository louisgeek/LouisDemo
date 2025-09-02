package com.louis.mynavi.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.louis.mynavi.databinding.FragmentHomeBinding;
import com.louis.mynavi.navi.PageNavigator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> {
            PageNavigator.getInstance().navigateBack();
        });
        binding.btnLogout.setOnClickListener(v -> {
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit().putBoolean("isLogin", false).apply();
            NavManager.getInstance().navToNext(getParentFragmentManager());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}