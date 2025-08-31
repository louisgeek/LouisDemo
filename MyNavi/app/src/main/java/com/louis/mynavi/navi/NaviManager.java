package com.louis.mynavi.navi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NaviManager {
    private FragmentManager fragmentManager;

    public NaviManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public String getBackStackTag(String fragmentTag) {
        //约定
        String backStackTag = "bs_" + fragmentTag.replace("Fragment", "").toLowerCase();
        return backStackTag;
    }

    public void navigateTo(int containerId, Fragment fragment, Bundle args, boolean addToBackStack) {
        if (args != null) {
            Bundle arguments = fragment.getArguments();
            if (arguments == null) {
                fragment.setArguments(args);
            } else {
                arguments.putAll(args);
            }
        }
        String fragmentTag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerId, fragment, fragmentTag);
        if (addToBackStack) {
            String backStackTag = getBackStackTag(fragmentTag);
            transaction.addToBackStack(backStackTag);
        }
        ///如果在主线程外调用，可以改用 commitAllowingStateLoss
        transaction.commit();
    }


    public void goBack() {
        //异步
        fragmentManager.popBackStack();
    }

    public void goBackTo(String backStackTag) {
        //0 保留目标页面
        fragmentManager.popBackStack(backStackTag, 0);
    }

    public void goBackTo(Class<? extends Fragment> fragmentClass) {
        String fragmentTag = fragmentClass.getSimpleName();
        String backStackTag = getBackStackTag(fragmentTag);
        fragmentManager.popBackStack(backStackTag, 0);
    }

    public boolean canGoBack() {
        return fragmentManager.getBackStackEntryCount() > 0;
    }

    public void clearBackStack() {
        //POP_BACK_STACK_INCLUSIVE 连同目标页面一起清除
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void goBackImmediate() {
        //同步
        boolean success = fragmentManager.popBackStackImmediate();
    }


    public void setFragmentResult(String requestKey, Bundle result) {
        fragmentManager.setFragmentResult(requestKey, result);
    }

    public void backWithFragmentResult(String requestKey, Bundle result) {
        fragmentManager.setFragmentResult(requestKey, result);
        goBack();
    }

}