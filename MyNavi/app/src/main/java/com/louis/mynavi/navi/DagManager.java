package com.louis.mynavi.navi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * DAG管理器，负责管理节点和执行流程
 */
public class DagManager {
    private final Map<String, DagNode> nodes = new HashMap<>();

    // 添加节点
    public void addNode(DagNode node) {
        // 检查是否会形成循环依赖
        for (DagNode existingNode : nodes.values()) {
            if (node.isDependentOn(existingNode) && existingNode.isDependentOn(node)) {
                throw new IllegalArgumentException("添加节点 " + node.getFragmentTag() + " 会导致循环依赖");
            }
        }
        nodes.put(node.getFragmentTag(), node);
    }

    // 获取节点
    public DagNode getNode(String id) {
        return nodes.get(id);
    }

    // 获取所有节点
    public List<DagNode> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

    // 获取指定节点的所有依赖节点
    public List<DagNode> getDependencies(String nodeId) {
        DagNode node = nodes.get(nodeId);
        return node != null ? new ArrayList<>(node.getDependencies()) : new ArrayList<>();
    }

    // 获取依赖于指定节点的所有节点
    public List<DagNode> getDependents(String nodeId) {
        DagNode node = nodes.get(nodeId);
        if (node == null) return new ArrayList<>();

        return nodes.values().stream()
                .filter(n -> n.getDependencies().contains(node))
                .collect(Collectors.toList());
    }

    /**
     * 获取拓扑排序的节点列表（基于入度的Kahn算法）
     */
    public List<DagNode> getStartNodes() {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, DagNode> nodeMap = new HashMap<>();

        for (DagNode node : nodes.values()) {
            nodeMap.put(node.getFragmentTag(), node);
        }

        // Calculate in-degrees
        for (DagNode node : nodes.values()) {
            inDegree.put(node.getFragmentTag(), node.getDependencies().size());
        }

        // Initialize queue with nodes having in-degree 0
        Queue<DagNode> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                DagNode node = nodeMap.get(entry.getKey());
                if (node != null) {
                    queue.add(node);
                }
            }
        }

        List<DagNode> result = new ArrayList<>();

        // Process nodes in topological order
        while (!queue.isEmpty()) {
            DagNode node = queue.poll();
            result.add(node);

            for (DagNode dependent : getDependents(node.getFragmentTag())) {
                int currentDegree = inDegree.getOrDefault(dependent.getFragmentTag(), 0) - 1;
                inDegree.put(dependent.getFragmentTag(), currentDegree);
                if (currentDegree == 0) {
                    queue.add(dependent);
                }
            }
        }

        // Check for circular dependencies
        if (result.size() != nodes.size()) {
            List<String> cycleNodeIds = new ArrayList<>();
            for (DagNode node : nodes.values()) {
                if (!result.contains(node)) {
                    cycleNodeIds.add(node.getFragmentTag());
                }
            }
            throw new IllegalStateException("检测到循环依赖，以下节点涉及循环: " + String.join(", ", cycleNodeIds));
        }

        return result;
    }

    /**
     * 获取第一个可执行的节点
     */
    public DagNode getStartNode() {
        List<DagNode> sortedNodes = getStartNodes();

        for (DagNode node : sortedNodes) {
            // 检查节点自身条件
            if (!node.isSatisfied()) {
                return node;
            }

            // 检查所有依赖节点的条件
            for (DagNode dependency : node.getDependencies()) {
                if (!dependency.isSatisfied()) {
                    return dependency;
                }
            }
        }

        // 如果所有节点条件都满足，返回最后一个节点（终点）
        return sortedNodes.isEmpty() ? null : sortedNodes.get(sortedNodes.size() - 1);
    }

    /**
     * 打印整个DAG的依赖关系
     */
    public String printDag() {
        StringBuilder builder = new StringBuilder();
        builder.append("DAG依赖关系图:\n");

        // 获取所有没有被依赖的节点（终端节点）
        List<DagNode> terminalNodes = nodes.values().stream()
                .filter(node -> nodes.values().stream().noneMatch(n -> n.getDependencies().contains(node)))
                .collect(Collectors.toList());

        // 从终端节点开始打印
        for (int i = 0; i < terminalNodes.size(); i++) {
            boolean isLast = i == terminalNodes.size() - 1;
            builder.append(terminalNodes.get(i).printDependencyTree("", isLast, new HashSet<>()));
        }

        return builder.toString();
    }
}