package com.louis.mynavi.navi;

import java.util.*;

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

    public boolean hasCycle() {
//        return fragmentDAG.hasCycle();
        List<String> order = topologicalSort();
        return false;
    }

    //Kahn 算法实现拓扑排序（跳转顺序）
    public List<String> topologicalSort() {
        //创建图
        Map<String, Integer> inDegree = new HashMap<>();
        //初始化入度
        for (String fragmentTag : pageNodeMap.keySet()) {
            inDegree.put(fragmentTag, 0);
        }
        //计算入度（基于 dependencies）
        for (PageNode node : pageNodeMap.values()) {
            for (PageNode dep : node.dependencies) {
                if (inDegree.containsKey(dep.fragmentTag)) {
                    inDegree.put(dep.fragmentTag, inDegree.get(dep.fragmentTag) + 1);
                }
            }
        }
//        for (String node : graph.keySet()) {
//            for (String neighbor : graph.get(node)) {
//                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
//            }
//        }
        //初始队列（入度为 0 且条件满足）
        Queue<String> queue = new LinkedList<>();
        for (PageNode node : pageNodeMap.values()) {
            if (inDegree.get(node.fragmentTag) == 0 && node.condition.isSatisfied()) {
                queue.offer(node.fragmentTag); //入队
            }
        }
        //拓扑排序  遍历队列，生成拓扑序列
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            //减少依赖当前节点的其他节点的入度               // 更新依赖此节点的其他节点的入度
            for (PageNode node : pageNodeMap.values()) {
                if (node.dependencies.contains(current)) {
                    //减少入度
                    int newDegree = inDegree.get(node.fragmentTag) - 1;
                    inDegree.put(node.fragmentTag, newDegree);
                    //入度为 0 则入队
                    if (newDegree == 0 && node.condition.isSatisfied()) {
                        queue.offer(node.fragmentTag);
                    }
                }
            }
        }

        printDAG();

        // 检查是否存在环（拓扑序列长度 < 总节点数）
        if (result.size() != pageNodeMap.size()) {
            throw new IllegalStateException("DAG 存在循环依赖！");
        }

        return result;
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
        for (Map.Entry<String, PageNode> entry : pageNodeMap.entrySet()) {
            String fragmentTag = entry.getKey();
            PageNode pageNode = entry.getValue();
            System.out.println(fragmentTag + " -> " + pageNode.fragmentTag);
        }
    }
}