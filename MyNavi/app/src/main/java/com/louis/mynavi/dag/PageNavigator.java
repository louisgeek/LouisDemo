package com.louis.mynavi.dag;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Fragment节点导航器，单例模式，负责页面跳转和导航管理
 */
public class PageNavigator {
    private static final String TAG = "PageNavigator";
    private static volatile PageNavigator instance;

    private FragmentManager fragmentManager;
    private int containerId;
    private PageNodeManager pageNodeManager;
    private String currentPageId;
    private OnPageUpdatedListener pageUpdatedListener;

    // 私有构造函数，确保单例
    private PageNavigator() {
    }

    /**
     * 获取单例实例
     */
    public static PageNavigator getInstance() {
        if (instance == null) {
            synchronized (PageNavigator.class) {
                if (instance == null) {
                    instance = new PageNavigator();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化导航器
     */
    public void init(FragmentActivity activity, int containerId, PageNodeManager pageNodeManager) {
        this.fragmentManager = activity.getSupportFragmentManager();
        this.containerId = containerId;
        this.pageNodeManager = pageNodeManager;

        // 设置页面更新监听器
        this.pageUpdatedListener = () -> {
            Log.d(TAG, "页面状态已更新，尝试自动跳转");
            navigateToNext();
        };
    }

    /**
     * 启动导航，显示起始页面
     */
    public boolean start() {
        if (pageNodeManager == null) {
            Log.e(TAG, "导航器未初始化");
            return false;
        }

        PageNode startNode = pageNodeManager.getStartPageNode();
        if (startNode != null) {
            return navigateTo(startNode.getPageId());
        } else {
            Log.e(TAG, "未设置起始页面");
            return false;
        }
    }

    /**
     * 跳转到指定页面ID的页面
     */
    public boolean navigateTo(String pageId) {
        if (fragmentManager == null || pageNodeManager == null) {
            Log.e(TAG, "导航器未初始化");
            return false;
        }

        PageNode targetNode = pageNodeManager.getPageNode(pageId);
        if (targetNode == null) {
            Log.e(TAG, "页面不存在: " + pageId);
            return false;
        }

        // 检查页面是否可访问
        if (!targetNode.isAccessible(pageNodeManager)) {
            Log.e(TAG, "页面不可访问: " + pageId);
            return false;
        }

        try {
            // 创建Fragment实例
            Fragment fragment = targetNode.getFragmentClass().newInstance();

            // 传递页面节点信息和导航监听器
            android.os.Bundle args = new android.os.Bundle();
            args.putParcelable("page_node", targetNode);
            fragment.setArguments(args);

            // 如果是可导航的页面，设置导航回调
            if (fragment instanceof NavigableFragment) {
                ((NavigableFragment) fragment).setOnNextClickListener(() -> {
                    // 点击下一页时触发
                    navigateToNext();
                });

                ((NavigableFragment) fragment).setOnPageUpdatedListener(pageUpdatedListener);
            }

            // 执行Fragment跳转
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(containerId, fragment, pageId);

            // 添加到返回栈（除了起始页面）
            if (currentPageId != null) {
                transaction.addToBackStack(pageId);
            }

            transaction.commit();

            // 更新当前页面ID并标记为已访问
            currentPageId = pageId;
            pageNodeManager.markNodeCompleted(pageId);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "创建Fragment失败", e);
            return false;
        }
    }

    /**
     * 返回上一页
     */
    public boolean navigateBack() {
        if (fragmentManager == null) {
            return false;
        }

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();

            // 更新当前页面ID
            int backStackCount = fragmentManager.getBackStackEntryCount();
            if (backStackCount > 0) {
                currentPageId = fragmentManager.getBackStackEntryAt(backStackCount - 1).getName();
            } else {
                currentPageId = null;
            }
            return true;
        }
        return false;
    }

    /**
     * 自动跳转到下一个可访问的页面
     */
    public boolean navigateToNext() {
        if (currentPageId == null || pageNodeManager == null) {
            return false;
        }

        PageNode nextNode = pageNodeManager.getNextAccessiblePage(currentPageId);
        if (nextNode != null) {
            return navigateTo(nextNode.getPageId());
        }

        Log.d(TAG, "没有可访问的下一页了");
        return false;
    }

    /**
     * 更新当前页面的下一页节点
     */
    public void updateCurrentPageNext(String nextPageId) {
        if (currentPageId != null && pageNodeManager != null) {
            pageNodeManager.updateNextPage(currentPageId, nextPageId);
        }
    }

    /**
     * 获取当前页面ID
     */
    public String getCurrentPageId() {
        return currentPageId;
    }

    /**
     * 重置导航状态
     */
    public void reset() {
        if (fragmentManager != null) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        currentPageId = null;
        if (pageNodeManager != null) {
            pageNodeManager.clearNavigationState();
        }
    }

    // 页面更新监听器
    public interface OnPageUpdatedListener {
        void onPageUpdated();
    }
}
