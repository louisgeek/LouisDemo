package com.louis.mynavi.node;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.louis.mynavi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class PageNodeManager2 {
    private static PageNodeManager2 instance;
    private Map<String, PageNode2> pageNodeMap = new HashMap<>();

    private PageNodeManager2() {
    }

    public static PageNodeManager2 getInstance() {
        if (instance == null) {
            instance = new PageNodeManager2();
        }
        return instance;
    }

    public void addPageNode(PageNode2 node) {
        pageNodeMap.put(node.getFragmentTag(), node);
    }

    public List<PageNode2> topologicalSort() {
        Map<PageNode2, Integer> inDegree = new HashMap<>();
        Queue<PageNode2> queue = new LinkedList<>();
        List<PageNode2> result = new ArrayList<>();

        for (PageNode2 node : pageNodeMap.values()) {
            inDegree.put(node, node.getDependencies().size());
            if (inDegree.get(node) == 0 && node.getCondition().isSatisfied()) {
                queue.offer(node);
            }
        }

        while (!queue.isEmpty()) {
            PageNode2 current = queue.poll();
            result.add(current);

            for (PageNode2 node : pageNodeMap.values()) {
                if (node.getDependencies().contains(current)) {
                    inDegree.put(node, inDegree.get(node) - 1);
                    if (inDegree.get(node) == 0 && node.getCondition().isSatisfied()) {
                        queue.offer(node);
                    }
                }
            }
        }
        return result;
    }

    public PageNode2 getStartNode() {
        List<PageNode2> sorted = topologicalSort();
        for (PageNode2 node : sorted) {
            if (!node.isCompleted()) {
                return node;
            }
        }
        Log.i("TAG", "getStartNode sss=" + sorted.size());
        return sorted.isEmpty() ? null : sorted.get(0);
    }

    public void markNodeCompleted(String fragmentTag) {
        PageNode2 node = pageNodeMap.get(fragmentTag);
        if (node != null) {
            node.setCompleted(true);
        }
    }

    public boolean isCompleted(String fragmentTag) {
        PageNode2 node = pageNodeMap.get(fragmentTag);
        return node != null && node.isCompleted();
    }


    public void navigateToNext(FragmentManager fragmentManager) {
        PageNodeManager2 manager = PageNodeManager2.getInstance();

        // 获取下一个要显示的页面
        PageNode2 startNode = manager.getStartNode();
        if (startNode != null) {
            // 跳转到页面
            navigateToFragment(fragmentManager, startNode.getFragmentClass(), startNode.getArgs());

            // 标记当前页面完成
            manager.markNodeCompleted(startNode.getFragmentTag());
        }
    }

    private void navigateToFragment(FragmentManager fragmentManager, Class<?> fragmentClass, Bundle args) {
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
