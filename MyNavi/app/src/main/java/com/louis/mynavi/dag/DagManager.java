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
     * 优化的拓扑排序（Kahn 算法），仅抛出真实循环依赖异常
     *
     * @return 可处理的节点列表（按顺序）
     * @throws IllegalStateException 仅当存在真实循环依赖时抛出
     */
    public List<DagNode> topologicalSort() {
        Map<DagNode, Integer> inDegree = new HashMap<>(); // 仅统计未完成的依赖导致的入度
        Queue<DagNode> queue = new LinkedList<>();
        List<DagNode> sortedNodes = new ArrayList<>();

        // 初始化入度：所有节点初始入度为 0
        for (DagNode node : nodes.values()) {
            inDegree.put(node, 0);
        }

        // 计算初始入度（仅统计未完成的依赖）
        for (DagNode node : nodes.values()) {
            for (DagNode dep : node.getDependencies()) {
                if (!dep.isCompleted()) {
                    inDegree.put(node, inDegree.get(node) + 1);
                }
            }
        }

        // 初始队列：入度为 0 的节点（无论条件是否满足）
        for (DagNode node : nodes.values()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        // 处理队列
        while (!queue.isEmpty()) {
            DagNode current = queue.poll();

            // 仅当节点可用（条件满足）时，才加入结果列表并更新下游
            if (current.isAvailable()) {
                sortedNodes.add(current);

                // 更新下游节点的入度
                for (DagNode node : nodes.values()) {
                    if (node.getDependencies().contains(current)) {
                        inDegree.put(node, inDegree.get(node) - 1);
                        if (inDegree.get(node) == 0) {
                            queue.offer(node);
                        }
                    }
                }
            } else {
                // 条件不满足的节点：保留在队列外，不处理但允许后续重新检查
                queue.offer(current); // 重新入队，等待条件满足
            }
        }

//        // 检测循环依赖（拓扑序列长度 ≠ 总节点数）
        if (sortedNodes.size() != nodes.size()) {
            throw new IllegalStateException("DAG 存在循环依赖！");
        }

        //检测真实循环依赖：存在未被处理且入度 > 0 的节点
//        for (DagNode node : nodes.values()) {
//            if (!sortedNodes.contains(node) && inDegree.get(node) > 0) {
//                throw new IllegalStateException("DAG 存在循环依赖！");
//            }
//        }

        // 检查是否有环：对于满足条件的节点，如果存在入度不为0的，说明有环
//        for (T node : satisfiedNodes) {
//            if (inDegree.get(node) > 0) {
//                throw new IllegalArgumentException("图中存在环，无法进行拓扑排序");
//            }
//        }


        return sortedNodes;
    }

    public DagNode getCurrentAvailableNode() {
        List<DagNode> sortedNodes = topologicalSort();
        for (DagNode node : sortedNodes) {
            if (!completedNodes.contains(node) && node.isAvailable()) {
                return node;
            }
        }
        return null;
    }

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

    public void markNodeCompleted(String nodeId) {
        DagNode node = nodes.get(nodeId);
        if (node != null) {
            node.markCompleted();
            completedNodes.add(node);
        }
    }

    public boolean hasCycle() {
        try {
            topologicalSort();
            return false;
        } catch (IllegalStateException e) {
            return e.getMessage().contains("循环依赖");
        }
    }
}