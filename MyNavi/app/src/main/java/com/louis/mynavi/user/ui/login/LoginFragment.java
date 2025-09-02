package com.louis.mynavi.user.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.louis.mynavi.databinding.FragmentLoginBinding;
import com.louis.mynavi.navi.FlowManager;
import com.louis.mynavi.navi.PageNavigator;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> {
            PageNavigator.getInstance().navigateBack();
        });
        binding.btnNext.setOnClickListener(v -> {
            // 模拟登录成功
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit().putBoolean("isLogin", true).apply();
//            PageNavigator.getInstance().isLogined = true;
//            PageNavigator.getInstance().navigateToNext(true);
            FlowManager.getInstance(requireContext()).navToNext(getParentFragmentManager());
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}