package com.louis.mynavi.dag;// DagManager.java

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DagManager {
    private final Map<String, DagNode> nodes = new HashMap<>();      // 节点 ID → 节点
    private final Set<DagNode> completedNodes = new HashSet<>();     // 已完成的节点

    /**
     * 添加节点到 DAG
     *
     * @param nodesToAdd 要添加的节点数组
     */
    public void addNodes(DagNode... nodesToAdd) {
        for (DagNode node : nodesToAdd) {
            nodes.put(node.getId(), node);
        }
    }

    /**
     * 移除节点（级联移除依赖关系）
     *
     * @param nodeId 要移除的节点 ID
     */
    public void removeNode(String nodeId) {
        DagNode node = nodes.get(nodeId);
        if (node == null) return;

        // 从所有依赖此节点的节点中移除依赖
        for (DagNode n : nodes.values()) {
            n.getDependencies().removeIf(dep -> dep.getId().equals(nodeId));
        }
        nodes.remove(nodeId);
        completedNodes.remove(node);
    }

    /**
     * 条件感知的拓扑排序（Kahn 算法）
     * 仅处理入度为 0 且条件满足的节点
     *
     * @return 可跳转的节点列表（按顺序）
     * @throws IllegalStateException 存在循环依赖时抛出
     */
    public List<DagNode> topologicalSort() {
        Map<DagNode, Integer> inDegree = new HashMap<>(); // 节点入度（未被完成的前置节点数）
        Queue<DagNode> queue = new LinkedList<>();         // 待处理队列（入度 0 + 条件满足）
        List<DagNode> sortedNodes = new ArrayList<>();

        // 初始化所有节点入度为 0
        for (DagNode node : nodes.values()) {
            inDegree.put(node, 0);
        }

        // 计算实际入度（仅统计未完成的依赖节点）
        for (DagNode node : nodes.values()) {
            for (DagNode dep : node.getDependencies()) {
                if (!dep.isCompleted()) {
                    inDegree.put(node, inDegree.get(node) + 1);
                }
            }
        }

        // 初始队列：入度为 0 且条件满足的节点
        for (DagNode node : nodes.values()) {
            if (inDegree.get(node) == 0 && node.isAvailable()) {
                queue.offer(node);
            }
        }

        // 处理队列生成拓扑序列
        while (!queue.isEmpty()) {
            DagNode current = queue.poll();
            sortedNodes.add(current);

            // 更新下游节点的入度
            for (DagNode node : nodes.values()) {
                if (node.getDependencies().contains(current)) {
                    inDegree.put(node, inDegree.get(node) - 1);
                    // 若入度变为 0 且条件满足，加入队列
                    if (inDegree.get(node) == 0 && node.isAvailable()) {
                        queue.offer(node);
                    }
                }
            }
        }

        // 检测循环依赖（拓扑序列长度 ≠ 总节点数）
        if (sortedNodes.size() != nodes.size()) {
            throw new IllegalStateException("DAG 存在循环依赖！");
        }

        return sortedNodes;
    }

    /**
     * 获取当前可立即跳转的节点（拓扑排序中第一个未完成且可用的节点）
     *
     * @return 可跳转的节点；无可用节点返回 null
     */
    public DagNode getCurrentAvailableNode() {
        List<DagNode> sortedNodes = topologicalSort();
        for (DagNode node : sortedNodes) {
            if (!completedNodes.contains(node) && node.isAvailable()) {
                return node;
            }
        }
        return null; // 所有节点已完成或不可用
    }

    /**
     * 跳转到指定节点对应的 Fragment
     *
     * @param fragmentManager FragmentManager 实例
     * @param node            目标节点
     */
    public void navigateTo(FragmentManager fragmentManager, DagNode node) {
//        if (node == null) return;
//
//        try {
//            // 通过反射实例化 Fragment（需确保 route 是完整类名）
//            Class<?> fragmentClass = Class.forName(node.getRoute());
//            Fragment fragment = (Fragment) fragmentClass.getDeclaredConstructor().newInstance();
//
//            // 执行 Fragment 事务（替换容器并添加回退栈）
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fl_fragment_container, fragment)
//                    .addToBackStack(node.getId()) // 回退时回到上一个节点
//                    .commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Fragment 实例化失败：" + node.getRoute());
//        }
    }

    /**
     * 标记节点为已完成（用户完成当前页面操作后调用）
     *
     * @param nodeId 节点 ID
     */
    public void markNodeCompleted(String nodeId) {
        DagNode node = nodes.get(nodeId);
        if (node != null) {
            node.markCompleted();
            completedNodes.add(node);
        }
    }

    /**
     * 检查是否存在循环依赖（调试用）
     *
     * @return true：存在循环；false：无循环
     */
    public boolean hasCycle() {
        try {
            topologicalSort();
            return false;
        } catch (IllegalStateException e) {
            return e.getMessage().contains("循环依赖");
        }
    }
}