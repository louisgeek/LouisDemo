//package com.louis.mynavi.navi;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Queue;
//import java.util.Set;
//import java.util.Stack;
//
//public class NaviManagerOld {
//    private FragmentManager fragmentManager;
//    private int containerId;
//    private Context ctx;
//    private Map<String, List<String>> naviGraph = new HashMap<>();
//    private Map<String, Integer> degree = new HashMap<>();
//    private Stack<String> navStack = new Stack<>();
//
//
//    public NaviManagerOld(FragmentManager fragmentManager, int containerId, Context ctx) {
//        this.fragmentManager = fragmentManager;
//        this.containerId = containerId;
//        this.ctx = ctx;
//    }
//
//    public void addEdge(String from, String to) {
//        naviGraph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
//        degree.put(to, degree.getOrDefault(to, 0) + 1);
//        degree.putIfAbsent(from, 0);
//    }
//
//    public List<String> kahn() {
//        Queue<String> queue = new LinkedList<>();
//        Map<String, Integer> temp = new HashMap<>(degree);
//        temp.entrySet().stream().filter(e -> e.getValue() == 0).forEach(e -> queue.offer(e.getKey()));
//
//        List<String> result = new ArrayList<>();
//        while (!queue.isEmpty()) {
//            String cur = queue.poll();
//            result.add(cur);
//            if (naviGraph.containsKey(cur)) {
//                naviGraph.get(cur).forEach(next -> {
//                    temp.put(next, temp.get(next) - 1);
//                    if (temp.get(next) == 0) queue.offer(next);
//                });
//            }
//        }
//        return result;
//    }
//
//
//    public String getCurrentTag() {
//        return navStack.isEmpty() ? null : navStack.peek();
//    }
//
//    public String findNearestCommon(String tag1, String tag2) {
//        List<String> order = kahn();
//        Set<String> path1 = getPathTo(tag1);
//        Set<String> path2 = getPathTo(tag2);
//
//        for (String node : order) {
//            if (path1.contains(node) && path2.contains(node)) {
//                return node;
//            }
//        }
//        return null;
//    }
//
//    private Set<String> getPathTo(String target) {
//        Set<String> ancestors = new HashSet<>();
//        Queue<String> queue = new LinkedList<>();
//        queue.offer(target);
//        ancestors.add(target);
//
//        while (!queue.isEmpty()) {
//            String current = queue.poll();
//            for (Map.Entry<String, List<String>> entry : naviGraph.entrySet()) {
//                if (entry.getValue().contains(current) && !ancestors.contains(entry.getKey())) {
//                    ancestors.add(entry.getKey());
//                    queue.offer(entry.getKey());
//                }
//            }
//        }
//
//        return ancestors;
//    }
//
//    public List<String> getValidPath(String from, String to) {
//        List<String> order = kahn();
//        int fromIndex = order.indexOf(from);
//        int toIndex = order.indexOf(to);
//
//        if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
//            return order.subList(fromIndex, toIndex + 1);
//        }
//        return Collections.emptyList();
//    }
//
//    public boolean isValidNavigation(String from, String to) {
//        return !getValidPath(from, to).isEmpty();
//    }
//
//
//}