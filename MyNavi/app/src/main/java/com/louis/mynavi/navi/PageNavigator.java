package com.louis.mynavi.navi;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.louis.mynavi.R;

public class PageNavigator {
    private static PageNavigator instance;
    private NavManager mNavManager;
    private PageNodeManager mPageNodeManager;

    private PageNavigator() {
    }

    public static synchronized PageNavigator getInstance() {
        if (instance == null) {
            instance = new PageNavigator();
        }
        return instance;
    }

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

    public void navigateToNext() {
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
}
