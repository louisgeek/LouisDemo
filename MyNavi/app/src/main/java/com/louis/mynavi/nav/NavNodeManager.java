package com.louis.mynavi.nav;

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
public class NavNodeManager {
    private final Map<String, NavNode> pageNodeMap = new HashMap<>();

    // 添加节点
    public void addNode(NavNode node) {
        // 检查是否会形成循环依赖
        for (NavNode existingNode : pageNodeMap.values()) {
            if (node.isDependentOn(existingNode) && existingNode.isDependentOn(node)) {
                throw new IllegalArgumentException("添加节点 " + node.getFragmentTag() + " 会导致循环依赖");
            }
        }
        pageNodeMap.put(node.getFragmentTag(), node);
    }

    // 获取节点
    public NavNode getNode(String id) {
        return pageNodeMap.get(id);
    }

    // 获取所有节点
    public List<NavNode> getAllNodes() {
        return new ArrayList<>(pageNodeMap.values());
    }

    // 获取指定节点的所有依赖节点
    public List<NavNode> getDependencies(String nodeId) {
        NavNode node = pageNodeMap.get(nodeId);
        return node != null ? new ArrayList<>(node.getDependencies()) : new ArrayList<>();
    }

    // 获取依赖于指定节点的所有节点
    public List<NavNode> getDependents(NavNode node) {
        if (node == null) {
            return new ArrayList<>();
        }

        return pageNodeMap.values().stream()
                .filter(n -> n.getDependencies().contains(node))
                .collect(Collectors.toList());
    }

    /**
     * 获取拓扑排序的节点列表（基于入度的Kahn算法）
     */
    private List<NavNode> topologicalSort() {
        Map<NavNode, Integer> inDegree = new HashMap<>();
        Queue<NavNode> queue = new ArrayDeque<>();


        for (NavNode node : pageNodeMap.values()) {
            inDegree.put(node, node.getDependencies().size());
        }

        for (Map.Entry<NavNode, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                NavNode node = entry.getKey();
                if (node != null) {
                    queue.offer(node);
                }
            }
        }

        List<NavNode> result = new ArrayList<>();

        // Process nodes in topological order
        while (!queue.isEmpty()) {
            NavNode current = queue.poll();
            result.add(current);

            for (NavNode node : getDependents(current)) {
                int currentDegree = inDegree.getOrDefault(node, 0) - 1;
                inDegree.put(node, currentDegree);
                if (currentDegree == 0) {
                    queue.offer(node);
                }
            }
        }

        // Check for circular dependencies
        if (result.size() != pageNodeMap.size()) {
            List<String> cycleNodeIds = new ArrayList<>();
            for (NavNode node : pageNodeMap.values()) {
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
    public NavNode getStartNode() {
        List<NavNode> sortedNodes = topologicalSort();

        for (NavNode node : sortedNodes) {
            // 检查节点自身条件
            if (!node.isCompleted()) {
                return node;
            }

            // 检查所有依赖节点的条件
            for (NavNode dependency : node.getDependencies()) {
                if (!dependency.isCompleted()) {
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
        List<NavNode> terminalNodes = pageNodeMap.values().stream()
                .filter(node -> pageNodeMap.values().stream().noneMatch(n -> n.getDependencies().contains(node)))
                .collect(Collectors.toList());

        // 从终端节点开始打印
        for (int i = 0; i < terminalNodes.size(); i++) {
            boolean isLast = i == terminalNodes.size() - 1;
            builder.append(terminalNodes.get(i).printDependencyTree("", isLast, new HashSet<>()));
        }

        return builder.toString();
    }
}