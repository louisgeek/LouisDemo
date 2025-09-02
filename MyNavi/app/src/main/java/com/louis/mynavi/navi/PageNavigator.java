package com.louis.mynavi.navi;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.louis.mynavi.R;

import java.util.List;

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

    private PageNode mCurrentNode; // 记录当前节点

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
        // 1. 获取当前节点（首次使用起始节点）
//        if (mCurrentNode == null) {
        mCurrentNode = mPageNodeManager.getStartNode();
//        }

        Log.e(TAG, "navigateToNext: Current node=" + mCurrentNode);

        if (mCurrentNode != null) {
            // 2. 获取所有可能的下一个节点
//            List<PageNode> nextNodes = mPageNodeManager.getNextNodes(mCurrentNode);
//
//            // 3. 查找第一个满足条件的节点
//            PageNode targetNode = null;
//            for (PageNode node : nextNodes) {
//                if (node.condition.isSatisfied()) {
//                    targetNode = node;
//                    break;
//                }
//            }

            // 4. 如果找到满足条件的节点则跳转
            if (mCurrentNode != null) {
                Fragment targetFragment = null;
                try {
                    targetFragment = mCurrentNode.fragmentClass.newInstance();
                    Log.e(TAG, "navigateToNext: Target fragment=" + targetFragment);

                    // 执行跳转
                    mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);

                    // 更新当前节点
//                    mCurrentNode = targetNode;
                    if (markNodeCompleted) {
                        mPageNodeManager.markNodeCompleted(mCurrentNode.fragmentTag);
                    }

                    // 5. 递归检查是否需要继续跳转下一页
//                    navigateToNext(); // 自动继续跳转

                } catch (Exception e) {
                    Log.e(TAG, "Fragment instantiation failed", e);
                }
            } else {
                Log.e(TAG, "No next node with satisfied condition");
            }
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

    public void autoNavigate() {
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
        mCurrentNode = mPageNodeManager.getStartNode();

        if (mCurrentNode != null) {
            try {
                Fragment targetFragment = mCurrentNode.fragmentClass.newInstance();
                mNavManager.navigateTo(R.id.containerId, targetFragment, null, true);
                Log.e(TAG, "startNavigation: Started with " + mCurrentNode.fragmentTag);
            } catch (Exception e) {
                Log.e(TAG, "startNavigation: Failed", e);
            }
        }
    }
}
