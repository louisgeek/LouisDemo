package com.louis.mynavi.navi;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.louis.mynavi.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageNavigator {
    private static volatile PageNavigator instance;

    private PageNavigator() {
    }

    // 获取单例实例的方法
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

    private NavManager mNavManager;
    private PageNodeManager mPageNodeManager;

//    private PageNode mCurrentNode; // 记录当前节点

    public void init(NavManager navManager) {
        this.mNavManager = navManager;
        mPageNodeManager = new PageNodeManager();

    }

    public void addPageNodes(PageNode... pageNodes) {
        mPageNodeManager.addPageNodes(pageNodes);
    }

    // 添加带条件的跳转边
    public void addTransition(String from, String to) {
//        mPageNodeManager.addEdge(from, to);
    }

    private static final String TAG = "PageNavigator";

    public boolean isLogined = false;

    public void navigateBack() {
        mNavManager.goBack();
    }

    public void navigateToNext(boolean markNodeCompleted) {
        Log.d("dagManager", mPageNodeManager.printDag());
        // 1. 获取当前节点（首次使用起始节点）
//        if (mCurrentNode == null) {
        PageNode node = mPageNodeManager.getStartNode();
//        }


        Fragment targetFragment = null;
        try {
            targetFragment = node.fragmentClass.newInstance();
            Log.e(TAG, "navigateToNext: Target fragment=" + targetFragment);

            // 执行跳转
            mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);

            // 更新当前节点
//                    mCurrentNode = targetNode;
//            if (markNodeCompleted) {
//                markNodeCompleted(node.fragmentClass);
//            }

            // 5. 递归检查是否需要继续跳转下一页
//                    navigateToNext(); // 自动继续跳转

        } catch (Exception e) {
            Log.e(TAG, "Fragment instantiation failed", e);
        }

    }

    public void navigateToNext2() {
//        List<PageNode> order = mPageNodeManager.topologicalSortOne();
//        List<PageNode> order = mPageNodeManager.topologicalSortTwo();
        PageNode pageNode = mPageNodeManager.getStartNode();
        Log.e(TAG, "navigateToNext: pageNode=" + pageNode);
        if (pageNode != null) {
            Fragment targetFragment = null;
            try {
                targetFragment = pageNode.fragmentClass.newInstance(); //类反射
                Log.e(TAG, "navigateToNext: targetFragment=" + targetFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
        }
//        List<PageNode> order = mPageNodeManager.topologicalSort();
//        List<String> order = mPageNodeManager.topologicalSort();
//        for (String fragmentTag : order) {
//            if (fragmentTag.equals(targetFragmentTag)) {
//                Fragment fragment = mNavManager.findFragmentByTag(fragmentTag);
//                if (fragment == null) {
//                    PageNode node = mPageNodeManager.getPageNode(fragmentTag);
//                    Fragment targetFragment = null;
//                    try {
//                        targetFragment = node.fragmentClass.newInstance(); //类反射
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
//                }
//                break;
//            }
//        }

//        for (FragmentEdge edge : current.outgoingEdges) {
//            if (edge.condition.test(context)) {
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.container, edge.to.fragmentClass.newInstance());
//                ft.addToBackStack(edge.to.id);
//                ft.commit();
//                return;
//            }
//        }


//        List<String> order = mPageNodeManager.topologicalSort();
//
//        mNavManager.navigateTo(R.id.fragment_container, fragmentManager.findFragmentByTag(order.get(0)), null, true);
//
//        for (String fragmentTag : order) {
//            if (fragmentTag.equals(targetFragmentTag)) {
//                Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
//                if (fragment == null) {
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.fragment_container, Fragment.instantiate(context, fragmentTag), fragmentTag)
//                            .addToBackStack(fragmentTag)
//                            .commit();
//                }
//                break;
//            }
//        }
    }

    public void navigateTo(Class<? extends Fragment> fragmentClass) {
        try {
            Fragment targetFragment = fragmentClass.newInstance();
            Log.e(TAG, "navigateTo: " + fragmentClass.getSimpleName());
            mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
        } catch (Exception e) {
            Log.e(TAG, "navigateTo error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 检查是否可以跳转到指定步骤
    public boolean canNavigateTo(String fragmentTag) {
        PageNode targetNode = mPageNodeManager.getAllNodes().stream()
                .filter(node -> fragmentTag.equals(node.fragmentTag))
                .findFirst()
                .orElse(null);

        Log.e(TAG, "canNavigateTo targetNode: " + targetNode);
        if (targetNode == null) {
            return false;
        }

        // 检查所有依赖是否满足条件
        for (PageNode dependency : targetNode.dependencies) {
            if (!dependency.isCompleted()) {
                Log.e(TAG, "canNavigateTo dependency isCompleted=false dependency=" + dependency);
                return false;
            }
        }

        return true;
    }

    public void autoNavigate(@NonNull String targetKey) {
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
            PageNode targetNode = mPageNodeManager.getPageNode(targetKey);
            if (targetNode != null) {
                for (PageNode depKey : targetNode.dependencies) {
                    if (!canNavigateTo(depKey.fragmentTag)) {
                        autoNavigate(depKey.fragmentTag);
                        return;
                    }
                }
            }
            throw new IllegalStateException("Cannot navigate to: " + targetKey + ". Dependencies not met.");
        }
        Class<? extends Fragment> fragmentClass = null;
        try {
            fragmentClass = (Class<? extends Fragment>) Class.forName(targetKey);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Fragment targetFragment = null;
        try {
            targetFragment = fragmentClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        Log.e(TAG, "navigateToNext: Target fragment=" + targetFragment);

        // 执行跳转
        mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
    }

    public void autoNavigate2222() {
        try {
            List<PageNode> order = mPageNodeManager.topologicalSort();
            Log.e(TAG, "autoNavigate: available nodes=" + order.size());

            if (!order.isEmpty()) {
                PageNode nextNode = order.get(0);
                Fragment targetFragment = nextNode.fragmentClass.newInstance();
                Log.e(TAG, "autoNavigate: " + nextNode.fragmentClass.getSimpleName());
                mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
            } else {
                Log.e(TAG, "autoNavigate: no available nodes");
            }
        } catch (Exception e) {
            Log.e(TAG, "autoNavigate error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void startNavigation() {
        PageNode startNode = mPageNodeManager.getStartNode();
        Log.e(TAG, "startNavigation startNode: " + startNode);
        try {
            Fragment targetFragment = startNode.fragmentClass.newInstance();
            mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
        } catch (Exception e) {
            Log.e(TAG, "startNavigation: Failed", e);
        }
    }

    public String printDag() {
        return mPageNodeManager.printDag();
    }

    private Set<String> completedNodes = new HashSet<>();

    // 标记页面为已访问
    public void markNodeCompleted(Class<? extends Fragment> fragmentClass) {
//        if (pageNodeMap.containsKey(fragmentTag)) {
        String fragmentTag = fragmentClass.getSimpleName();
        completedNodes.add(fragmentTag);
//        }
    }

    public boolean isCompleted(Class<? extends Fragment> fragmentClass) {
        String fragmentTag = fragmentClass.getSimpleName();
        return completedNodes.contains(fragmentTag);
    }
}
