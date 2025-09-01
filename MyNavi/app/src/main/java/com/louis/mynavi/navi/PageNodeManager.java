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

    @Deprecated
    public List<PageNode> topologicalSortOne() {
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

        //只检查条件满足但未处理的节点
        boolean hasCycle = false;
        for (PageNode node : pageNodeMap.values()) {
            //homeNode.addDependency(loginNode); loginNode.addDependency(homeNode); 不抛 IllegalStateException 很明显不合理，但又不能去掉 isSatisfied 判断，因为去掉后正常情况下满足条件又会抛出异常
            //经典的 条件感知拓扑排序 问题。需要 分离结构检查和条件检查，解决方案：前置 hasCycleByKahn() 判断，然后删除这里的判断
            if (!result.contains(node) && node.condition.isSatisfied() && inDegree.get(node) > 0) {
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

    public List<PageNode> topologicalSortTwo() {
        if (hasCycleByKahn()) {
            throw new IllegalStateException("检测到结构性循环依赖");
        }
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
        return result;
    }

    /**
     * 检测结构性循环依赖（忽略条件）
     * 几乎相同的Kahn算法实现
     */
    private boolean hasCycleByKahn() {
        Map<PageNode, Integer> inDegree = new HashMap<>();
        Queue<PageNode> queue = new LinkedList<>();

        // 初始化入度（忽略条件）
        for (PageNode node : pageNodeMap.values()) {
            inDegree.put(node, node.dependencies.size());
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        List<PageNode> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            PageNode current = queue.poll();
            result.add(current);

            for (PageNode node : pageNodeMap.values()) {
                if (node.dependencies.contains(current)) {
                    inDegree.put(node, inDegree.get(node) - 1);
                    if (inDegree.get(node) == 0) {
                        queue.offer(node);
                    }
                }
            }
        }
        boolean hasCycle = false;
        if (result.size() != pageNodeMap.size()) {
            hasCycle = true;
        }
        return hasCycle;
    }

    /**
     * 条件感知的拓扑排序（Kahn 算法） 仅处理入度为 0 且条件满足的节点
     * Kahn 算法实现拓扑排序（跳转顺序）
     *
     * @return 可跳转的节点列表（按顺序）
     * @throws IllegalStateException 存在循环依赖时抛出
     */
    public List<PageNode> topologicalSort() {
        //先检查结构性循环，然后进行条件感知的拓扑排序
        //独立检测结构性循环依赖，忽略条件判断
        //忽略条件判断改用DFS方案判断解决的问题：不需要维护两套几乎相同的Kahn算法实现，DFS方案简洁直接
        //DFS会直接检测到：homeNode → loginNode → homeNode，立即返回 true
        if (hasCycle()) {
            throw new IllegalStateException("检测到结构性循环依赖");
        }

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
        return result;
    }


    private boolean hasCycle() {
        // 节点访问状态：0=未访问，1=正在访问，2=已访问
        Map<PageNode, Integer> visited = new HashMap<>();
        for (PageNode node : pageNodeMap.values()) {
            if (visited.getOrDefault(node, 0) == 0 && dfsHasCycle(node, visited)) {
                // 只处理未访问的节点
                return true; // 发现环
            }
        }
        return false; // 无环
    }

    /**
     * DFS递归检测环
     *
     * @param node 当前节点
     * @return 从当前节点出发存在环则返回true
     */
    private boolean dfsHasCycle(PageNode node, Map<PageNode, Integer> visited) {
        visited.put(node, 1);// 标记为正在访问
        for (PageNode dep : node.dependencies) {
            int depState = visited.getOrDefault(dep, 0);
            if (depState == 0) {
                // 邻接节点未访问，递归检测
                if (dfsHasCycle(dep, visited)) {
                    return true; // 邻接节点存在环
                }
            } else if (depState == 1) {
                // 邻接节点正在访问中，说明存在环
                return true; // 当前路径中发现环
            }

        }
        visited.put(node, 2);// 标记为已访问（退出递归栈）
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