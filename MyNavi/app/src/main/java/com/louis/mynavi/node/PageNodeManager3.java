package com.louis.mynavi.node;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.louis.mynavi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * PageNodeManager类管理页面节点，包含pageNodeMap等字段，
 * 提供addPageNode、topologicalSort、getStartNode、markNodeCompleted、isCompleted等方法
 */
public class PageNodeManager3 {

    private static PageNodeManager3 instance;

    private PageNodeManager3() {
    }

    public static PageNodeManager3 getInstance() {
        if (instance == null) {
            instance = new PageNodeManager3();
        }
        return instance;
    }

    private Map<String, PageNode3> pageNodeMap = new HashMap<>();
    private Set<String> completedNodes = new HashSet<>();

    /**
     * 添加页面节点
     *
     * @param pageNode 要添加的页面节点
     */
    public void addPageNode(PageNode3 pageNode) {
        pageNodeMap.put(pageNode.getFragmentTag(), pageNode);
    }

    /**
     * 获取页面节点
     *
     * @param fragmentTag 节点标识
     * @return 对应的页面节点
     */
    public PageNode3 getPageNode(String fragmentTag) {
        return pageNodeMap.get(fragmentTag);
    }

    /**
     * 对页面节点进行拓扑排序
     *
     * @return 按依赖关系排序的页面节点列表
     */
    public List<PageNode3> topologicalSort() {
        List<PageNode3> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        // 使用Kahn算法进行拓扑排序
        // 1. 计算每个节点的入度（有多少依赖项）
        Map<String, Integer> inDegree = new HashMap<>();
        for (PageNode3 node : pageNodeMap.values()) {
            inDegree.put(node.getFragmentTag(), node.getDependencies().size());
        }

        // 2. 将入度为0的节点加入队列（没有依赖项的节点）
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        // 3. 处理队列中的节点
        while (!queue.isEmpty()) {
            String currentTag = queue.poll();
            PageNode3 currentNode = pageNodeMap.get(currentTag);
            result.add(currentNode);

            // 检查哪些节点依赖于当前节点，减少它们的入度
            for (PageNode3 node : pageNodeMap.values()) {
                if (node.getDependencies().contains(currentTag)) {
                    inDegree.put(node.getFragmentTag(), inDegree.get(node.getFragmentTag()) - 1);
                    if (inDegree.get(node.getFragmentTag()) == 0) {
                        queue.offer(node.getFragmentTag());
                    }
                }
            }
        }

        // 检查是否存在循环依赖
//        if (result.size() != pageNodeMap.size()) {
//            throw new RuntimeException("存在循环依赖");
//        }

        return result;
    }

    /**
     * 获取可以开始执行的节点（没有未完成依赖的节点）
     *
     * @return 可以开始执行的节点列表
     */
    public List<PageNode3> readyNodes() {
        List<PageNode3> readyNodes = new ArrayList<>();

        for (PageNode3 node : pageNodeMap.values()) {
            boolean allDependenciesCompleted = true;

            for (String dependency : node.getDependencies()) {
                if (!completedNodes.contains(dependency)) {
                    allDependenciesCompleted = false;
                    break;
                }
            }

            if (allDependenciesCompleted && !completedNodes.contains(node.getFragmentTag())) {
                readyNodes.add(node);
            }
        }

        return readyNodes;
    }

    public PageNode3 getStartNode() {
        List<PageNode3> sorted = topologicalSort();
        for (PageNode3 node : sorted) {
            Log.i("TAG", "getStartNode--------- node=" + node);
//            if (!completedNodes.contains(node.getFragmentTag())) {
//                return node;
//            }
        }
        Log.i("TAG", "getStartNode sss=" + sorted.size());
        return sorted.isEmpty() ? null : sorted.get(0);
    }

    /**
     * 标记节点为已完成
     *
     * @param fragmentTag 节点标识
     */
    public void markNodeCompleted(String fragmentTag) {
        completedNodes.add(fragmentTag);
    }

    /**
     * 检查节点是否已完成
     *
     * @param fragmentTag 节点标识
     * @return 节点是否已完成
     */
    public boolean isCompleted(String fragmentTag) {
        return completedNodes.contains(fragmentTag);
    }

    /**
     * 检查所有节点是否都已完成
     *
     * @return 所有节点是否都已完成
     */
    public boolean isCompleted() {
        return completedNodes.size() == pageNodeMap.size();
    }

    /**
     * 获取所有节点数量
     *
     * @return 节点总数
     */
    public int getNodeCount() {
        return pageNodeMap.size();
    }

    /**
     * 获取已完成节点数量
     *
     * @return 已完成节点数
     */
    public int getCompletedNodeCount() {
        return completedNodes.size();
    }

    public void navigateToNext(FragmentManager fragmentManager) {

        PageNode3 startNode = getStartNode();
        System.out.println("加载页面: " + startNode.getFragmentTag() + " (类: " + startNode.getFragmentClass() + ")");

        // 跳转到页面
        Class clazz = null;
        try {
            clazz = Class.forName(startNode.getFragmentClass());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        navigateToFragment(fragmentManager, clazz, null);

        // 模拟页面加载完成
        markNodeCompleted(startNode.getFragmentTag());
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