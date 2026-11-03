package com.louis.javadesignpatterns.behavioral.state;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Demo {

    private Map<String, Set<String>> graph = new HashMap<>();
    private Map<String, Integer> inDegree = new HashMap<>();
    public void addEdge(String from, String to) {
        graph.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        graph.putIfAbsent(to, new HashSet<>());
    }
    
    public List<String> topologicalSort() {

        // 初始化所有节点的入度为0
        for (String node : graph.keySet()) {
            inDegree.put(node, 0);
        }
        
        // 计算每个节点的入度
        for (Set<String> neighbors : graph.values()) {
            for (String neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }


        Queue<String> queue = new ArrayDeque<>();
        
        // 将入度为0的节点加入队列
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        List<String> result = new ArrayList<>();
        // Kahn算法
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            for (String neighbor : graph.getOrDefault(current, new HashSet<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return result.size() == inDegree.size() ? result : null;
    }

}