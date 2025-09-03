package com.louis.mynavi.nav;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.louis.mynavi.R;
import com.louis.mynavi.home.HomeFragment;
import com.louis.mynavi.ui.AgreementFragment;
import com.louis.mynavi.ui.SplashFragment;
import com.louis.mynavi.user.ui.login.LoginFragment;

/**
 * 应用流程管理器，使用DAG管理应用的导航流程
 */
public class NavManager {


    private static volatile NavManager instance;

    private NavManager() {

    }

    // 获取单例实例的方法
    public static NavManager getInstance() {
        if (instance == null) {
            synchronized (NavManager.class) {
                if (instance == null) {
                    instance = new NavManager();
                }
            }
        }
        return instance;
    }


    public boolean splashCompleted = false;

    public boolean agreementCompleted = false;


    private NavNodeManager navNodeManager;
    private NavNodeManager navNodeManager02;

    public void init(Context context) {
        navNodeManager = new NavNodeManager();

        NavNode splashNode = new NavNode(SplashFragment.class, () -> splashCompleted);//未完成

        NavNode privacyNode = new NavNode(AgreementFragment.class, () -> PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isAgreed", false) || agreementCompleted); //未同意 或 未完成
        privacyNode.addDependency(splashNode); //

        NavNode loginNode = new NavNode(LoginFragment.class, () -> PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isLogin", false)); //未登录
        loginNode.addDependency(privacyNode); //登录页 依赖 隐私协议页

        NavNode homeNode = new NavNode(HomeFragment.class, () -> false); //固定未完成 一直要显示
        homeNode.addDependency(loginNode); //主页页 依赖 登录页

        navNodeManager.addNode(splashNode);
        navNodeManager.addNode(privacyNode);
        navNodeManager.addNode(loginNode);
        navNodeManager.addNode(homeNode);

        Log.d("dagManager", navNodeManager.printDag());
    }

    public NavNodeManager getNavNodeManager() {
        return navNodeManager;
    }

    // 检查是否可以跳转到指定步骤
    public boolean canNavigateTo(String step) {
        NavNode targetNode = navNodeManager.getAllNodes().stream()
                .filter(node -> step.equals(node.getFragmentClass()))
                .findFirst()
                .orElse(null);

        if (targetNode == null) {
            return false;
        }

        // 检查所有依赖是否满足条件
        for (NavNode dependency : targetNode.getDependencies()) {
            if (!dependency.isCompleted()) {
                return false;
            }
        }

        return true;
    }

    // 封装导航到下一步的逻辑
    public boolean navToNext(FragmentManager fragmentManager) {
        Log.d("dagManager", navNodeManager.printDag());
        //
        NavNode node = navNodeManager.getStartNode();
//        navController.navigate(route);
        if (node != null) {
            navigateToFragment(fragmentManager, node.getFragmentClass(), null);
        } else {
            navigateToFragment(fragmentManager, HomeFragment.class, null);
        }
        return true;
    }

    public void autoNavigate(FragmentManager fragmentManager, @NonNull String targetKey) {
//        if (fragmentManager == null || pageNodeManager == null) {
//            throw new IllegalStateException("Navigator not initialized");
//        }

        // 检查循环依赖
//        Set<String> visited = new HashSet<>();
//        if (!pageNodeManager.checkDependencies(targetKey, visited)) {
//            throw new IllegalStateException("Circular dependency detected for: " + targetKey);
//        }

        // 检查导航条件
        if (!canNavigateTo(targetKey)) {
            // 尝试导航到依赖节点
            NavNode targetNode = navNodeManager.getNode(targetKey);
            if (targetNode != null) {
                for (NavNode depKey : targetNode.getDependencies()) {
                    if (!canNavigateTo(depKey.getFragmentTag())) {
                        autoNavigate(fragmentManager, depKey.getFragmentTag());
                        return;
                    }
                }
            }
            throw new IllegalStateException("Cannot navigate to: " + targetKey + ". Dependencies not met.");
        }
        Class<?> fragmentClass = null;
        try {
            fragmentClass = Class.forName(targetKey);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        navigateToFragment(fragmentManager, fragmentClass, null);
    }

    public void navigateToFragment(FragmentManager fragmentManager, Class<?> fragmentClass, Bundle args) {
        // Fragment跳转逻辑
        Fragment targetFragment = null;
        try {
            targetFragment = (Fragment) fragmentClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        if (args != null) {
            targetFragment.setArguments(args);
        }
        navigateTo(fragmentManager, R.id.containerId, targetFragment, null, true);
    }

    private void navigateTo(FragmentManager fragmentManager, int containerId, Fragment fragment, Bundle args, boolean addToBackStack) {
        if (fragment == null) {
            return;
        }
        if (args != null) {
            Bundle arguments = fragment.getArguments();
            if (arguments == null) {
                fragment.setArguments(args);
            } else {
                arguments.putAll(args);
            }
        }

//        Fragment targetFragment = "".getFragmentClazz().newInstance(); //类反射

        String fragmentTag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerId, fragment, fragmentTag);
        if (addToBackStack) {
//            String backStackTag = getBackStackTag(fragmentTag);
            transaction.addToBackStack(fragmentTag);
        }
        ///如果在主线程外调用，可以改用 commitAllowingStateLoss
        transaction.commit();
    }

    private Fragment createFragment(Class<? extends Fragment> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Fragment createFragment(Class<? extends Fragment> fragmentClass, Bundle args) {
        try {
            return fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}