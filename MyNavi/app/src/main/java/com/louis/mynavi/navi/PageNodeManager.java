package com.louis.mynavi.navi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class PageNodeManager {

    //(fragmentTag,PageNode)
    private Map<String, PageNode> pageNodeMap = new HashMap<>();


    //(from，)
    //导航图（DAG 的“边”）
    //    private Map<String, PageNode> navGraph = new HashMap<>();
    private Map<PageNode, Set<PageNode>> navGraphMap = new HashMap<>();
    //添加节点

    public void addPageNode(PageNode pageNode) {
        pageNodeMap.put(pageNode.fragmentTag, pageNode);
    }

    public void addPageNodes(PageNode... pageNodes) {
        for (PageNode pageNode : pageNodes) {
            addPageNode(pageNode);
        }
    }


    //添加边（跳转关系）
    public void addEdge(String from, String to) {
//        if (!pageNodeMap.containsKey(from) || !pageNodeMap.containsKey(to)) {
//            throw new IllegalArgumentException("Fragment not in graph");
//        }
//        pageNodeMap.get(from).dependencies.add(to);
    }

    public PageNode getPageNode(String fragmentTag) {
        return pageNodeMap.get(fragmentTag);
    }


    /**
     * 条件感知的拓扑排序（Kahn 算法） 仅处理入度为 0 且条件满足的节点
     * Kahn 算法实现拓扑排序（跳转顺序）
     *
     * @return 可跳转的节点列表（按顺序）
     * @throws IllegalStateException 存在循环依赖时抛出
     */
    public List<PageNode> topologicalSort() {
        Map<PageNode, Integer> inDegree = new HashMap<>();
        Queue<PageNode> queue = new LinkedList<>();
        // 计算每个节点的入度
        // 队列用于存储入度为0且满足条件的节点          // 队列用于存储入度为0且满足条件的节点
        //初始队列（入度为 0 且条件满足）
        for (PageNode node : pageNodeMap.values()) {
            inDegree.put(node, node.dependencies.size());
            if (inDegree.get(node) == 0 && node.condition.isSatisfied()) {
                queue.offer(node); //入队
            }
        }
        //拓扑排序  遍历队列，生成拓扑序列
        List<PageNode> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            PageNode current = queue.poll();
            result.add(current);
            // 处理当前节点的所有邻居    //减少依赖当前节点的其他节点的入度               // 更新依赖此节点的其他节点的入度       // 遍历当前节点的所有依赖节点
            for (PageNode node : pageNodeMap.values()) {
                if (node.dependencies.contains(current)) {
                    inDegree.put(node, inDegree.get(node) - 1);
                    // 如果入度变为0且满足条件，则加入队列
                    if (inDegree.get(node) == 0 && node.condition.isSatisfied()) {
                        queue.offer(node);
                    }
                }
            }
        }

        printDAG();

        // 只检查条件满足但未处理的节点
        boolean hasCycle = false;
        for (PageNode node : pageNodeMap.values()) {
//            if (!result.contains(node) && node.condition.isSatisfied() && inDegree.get(node) > 0) {
            if (!result.contains(node) && inDegree.get(node) > 0) {
//                判断剩余未处理节点且满足条件的，是否还有入度大于0
                hasCycle = true;
                break;
            }
        }

        if (hasCycle) {
            throw new IllegalStateException("检测到循环依赖");
        }

        return result;
    }



    public boolean hasCycle() {
//        return fragmentDAG.hasCycle();
//        List<String> order = topologicalSort();
        return false;
    }

    //    public String getStartNode() {
//        List<String> sortedNodes = topologicalSort();
//        for (String node : sortedNodes) {
//            if (!completedNodes.contains(node) && node.isAvailable()) {
//                return node;
//            }
//        }
//        return null; // 无可用节点（所有节点已完成或不可用）
//    }
    public void printDAG() {
        for (PageNode node : pageNodeMap.values()) {
            System.out.println(node.fragmentTag + " -> " + Arrays.toString(node.dependencies.toArray()));
        }
    }
}