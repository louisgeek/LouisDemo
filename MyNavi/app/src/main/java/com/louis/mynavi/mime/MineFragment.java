package com.louis.mynavi.mime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.louis.mynavi.databinding.FragmentMineBinding;

public class MineFragment extends Fragment {
    private FragmentMineBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 使用ViewBinding
        binding = FragmentMineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置返回按钮点击事件
//        binding.btnBack.setOnClickListener(v -> {
//            naviManager.goBack();
//        });
//
//        // 设置跳转到设置按钮点击事件
//        binding.btnSettings.setOnClickListener(v -> {
//            Map<String, Object> params = new HashMap<>();
////            naviManager.navigateTo("settings", params, CONTAINER_ID);
//            naviManager.navigateTo(R.id.containerId, SettingsFragment.newInstance(), null, true);
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}