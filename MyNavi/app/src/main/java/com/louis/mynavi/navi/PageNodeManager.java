package com.louis.mynavi.navi;

import java.util.*;

public class PageNodeManager {
    private Map<String, PageNode> pageNodeMap = new HashMap<>();
    private Set<PageNode> nodes = new HashSet<>();

    //添加节点
    public void addNodes(PageNode... pageNodes) {
        for (PageNode node : pageNodes) {
            pageNodeMap.put(node.fragmentTag, node);
        }
    }


    //添加边（跳转关系）
    public void addEdge(String from, String to) {
        if (!pageNodeMap.containsKey(from) || !pageNodeMap.containsKey(to)) {
            throw new IllegalArgumentException("Fragment not in graph");
        }
        pageNodeMap.get(from).add(to);
    }


    //Kahn 算法实现拓扑排序（跳转顺序）
    private List<String> topologicalSort() {
        Map<String, Integer> inDegree = new HashMap<>();
        //初始化入度
        for (PageNode node : pageNodeMap.values()) {
            inDegree.put(node.fragmentTag, 0);

        }
        //计算入度
        for (PageNode node : pageNodeMap.values()) {
            for (String dep : node.dependencies) {
                inDegree.put(dep, inDegree.get(dep) + 1);
            }
        }
        //初始队列（入度为 0 且条件满足）
        Queue<String> queue = new LinkedList<>();
        for (PageNode node : pageNodeMap.values()) {
            if (inDegree.get(node.fragmentTag) == 0 && node.condition.isSatisfied()) {
                queue.offer(node.fragmentTag);
            }
        }
        //拓扑排序
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
            for (PageNode node : pageNodeMap.values()) {
                if (node.dependencies.contains(current)) {
                    int newDegree = inDegree.get(node.fragmentTag) - 1;
                    inDegree.put(node.fragmentTag, newDegree);
                    if (newDegree == 0 && node.condition.isSatisfied()) {
                        queue.add(node.fragmentTag);
                    }
                }
            }
        }
        return result;
    }

}