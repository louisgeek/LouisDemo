package com.louis.mynavi.nav;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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


    private NavNodeManager navNodeManager01;
    private NavNodeManager navNodeManager02;

    public void init(Context context) {
        navNodeManager01 = new NavNodeManager();

        NavNode splashNode = new NavNode(SplashFragment.class, () -> splashCompleted);//无条件

        NavNode privacyNode = new NavNode(AgreementFragment.class, () -> PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isAgreed", false) || agreementCompleted); //条件：未同意隐私协议
        privacyNode.addDependency(splashNode); //

        NavNode loginNode = new NavNode(LoginFragment.class, () -> PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isLogin", false)); //条件：同意隐私协议且未登录
        loginNode.addDependency(privacyNode); //登录页 依赖 隐私协议页

        NavNode homeNode = new NavNode(HomeFragment.class); //条件：同意隐私协议且已登录
        homeNode.addDependency(loginNode); //主页页 依赖 登录页

        navNodeManager01.addNode(splashNode);
        navNodeManager01.addNode(privacyNode);
        navNodeManager01.addNode(loginNode);
        navNodeManager01.addNode(homeNode);

        Log.d("dagManager", navNodeManager01.printDag());
    }

    public NavNodeManager getNavNodeManager01() {
        return navNodeManager01;
    }

    // 检查是否可以跳转到指定步骤
    public boolean canNavigateTo(String step, NavNodeManager navNodeManager) {
        NavNode targetNode = navNodeManager.getAllNodes().stream()
                .filter(node -> step.equals(node.getFragmentClass()))
                .findFirst()
                .orElse(null);

        if (targetNode == null) {
            return false;
        }

        // 检查所有依赖是否满足条件
        for (NavNode dependency : targetNode.getDependencies()) {
            if (!dependency.isSatisfied()) {
                return false;
            }
        }

        return true;
    }

    // 封装导航到下一步的逻辑
    public boolean navToNext(FragmentManager fragmentManager) {
        Log.d("dagManager", navNodeManager01.printDag());
        //
        NavNode node = navNodeManager01.getStartNode();
//        navController.navigate(route);
        if (node != null) {
            navigateToFragment(fragmentManager, node.getFragmentClass(), null);
        } else {
            navigateToFragment(fragmentManager, HomeFragment.class, null);
        }
        return true;
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
}