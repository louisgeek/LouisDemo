package com.louis.mynavi.navi;

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
public class FlowManager {

    private static volatile FlowManager instance;

    private FlowManager(Context context) {
        initializeDag(context);
    }

    // 获取单例实例的方法
    public static FlowManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FlowManager.class) {
                if (instance == null) {
                    instance = new FlowManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public boolean splashCompleted = false;

    public boolean agreementCompleted = false;

    private final DagManager dagManager = new DagManager();
    private static final String TAG = "FlowManager";

    private void initializeDag(Context context) {

        DagNode splashNode = new DagNode(SplashFragment.class, () -> splashCompleted);//无条件

        DagNode privacyNode = new DagNode(AgreementFragment.class, () -> PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isAgreed", false) || agreementCompleted); //条件：未同意隐私协议
        privacyNode.addDependency(splashNode); //

        DagNode loginNode = new DagNode(LoginFragment.class, () -> PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("isLogin", false)); //条件：同意隐私协议且未登录
        loginNode.addDependency(privacyNode); //登录页 依赖 隐私协议页

        DagNode homeNode = new DagNode(HomeFragment.class); //条件：同意隐私协议且已登录
        homeNode.addDependency(loginNode); //主页页 依赖 登录页

        dagManager.addNode(splashNode);
        dagManager.addNode(privacyNode);
        dagManager.addNode(loginNode);
        dagManager.addNode(homeNode);

        Log.d("dagManager", dagManager.printDag());
    }

    // 获取当前应该显示的流程步骤
    public DagNode getCurrentStep() {
        return dagManager.getStartNode();
    }

    // 检查是否可以跳转到指定步骤
    public boolean canNavigateTo(String step) {
        DagNode targetNode = dagManager.getAllNodes().stream()
                .filter(node -> step.equals(node.getFragmentClass()))
                .findFirst()
                .orElse(null);

        if (targetNode == null) {
            return false;
        }

        // 检查所有依赖是否满足条件
        for (DagNode dependency : targetNode.getDependencies()) {
            if (!dependency.isSatisfied()) {
                return false;
            }
        }

        return true;
    }

    // 封装导航到下一步的逻辑
    public boolean navToNext(FragmentManager fragmentManager) {
        Log.d("dagManager", dagManager.printDag());
        //
        DagNode node = dagManager.getStartNode();
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