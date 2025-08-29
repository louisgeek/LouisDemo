package com.louis.mynavi.navi;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.*;

public class NaviManager {
    private FragmentManager fragmentManager;
    private int containerId;
    private Context ctx;
    private Map<String, List<String>> dag = new HashMap<>();
    private Map<String, Integer> degree = new HashMap<>();
    private Stack<String> navStack = new Stack<>();

    
    public NaviManager(FragmentManager fragmentManager, int containerId, Context ctx) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.ctx = ctx;
    }
    
    public void addEdge(String from, String to) {
        dag.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        degree.put(to, degree.getOrDefault(to, 0) + 1);
        degree.putIfAbsent(from, 0);
    }
    
    public List<String> kahn() {
        Queue<String> q = new LinkedList<>();
        Map<String, Integer> temp = new HashMap<>(degree);
        temp.entrySet().stream().filter(e -> e.getValue() == 0).forEach(e -> q.offer(e.getKey()));
        
        List<String> result = new ArrayList<>();
        while (!q.isEmpty()) {
            String cur = q.poll();
            result.add(cur);
            if (dag.containsKey(cur)) {
                dag.get(cur).forEach(next -> {
                    temp.put(next, temp.get(next) - 1);
                    if (temp.get(next) == 0) q.offer(next);
                });
            }
        }
        return result;
    }
    
    public void to(String tag, Fragment f) {
        fragmentManager.beginTransaction().replace(containerId, f, tag).addToBackStack(tag).commit();
        navStack.push(tag);
    }
    
    public void toActivity(Class<?> activityClass) {
        ctx.startActivity(new Intent(ctx, activityClass));
    }
    
    public void back() {
        fragmentManager.popBackStack();
        if (!navStack.isEmpty()) navStack.pop();
    }
    
    public void clear() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        dag.clear();
        degree.clear();
        navStack.clear();
    }
    
    public void backTo(String tag) {
        fragmentManager.popBackStack(tag, 0);
        while (!navStack.isEmpty() && !navStack.peek().equals(tag)) {
            navStack.pop();
        }
    }
    
    public String getCurrentTag() {
        return navStack.isEmpty() ? null : navStack.peek();
    }
    
    public String findNearestCommon(String tag1, String tag2) {
        List<String> order = kahn();
        Set<String> path1 = getPathTo(tag1);
        Set<String> path2 = getPathTo(tag2);
        
        for (String node : order) {
            if (path1.contains(node) && path2.contains(node)) {
                return node;
            }
        }
        return null;
    }
    
    private Set<String> getPathTo(String target) {
        Set<String> ancestors = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(target);
        ancestors.add(target);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (Map.Entry<String, List<String>> entry : dag.entrySet()) {
                if (entry.getValue().contains(current) && !ancestors.contains(entry.getKey())) {
                    ancestors.add(entry.getKey());
                    queue.offer(entry.getKey());
                }
            }
        }
        
        return ancestors;
    }
    
    public List<String> getValidPath(String from, String to) {
        List<String> order = kahn();
        int fromIndex = order.indexOf(from);
        int toIndex = order.indexOf(to);
        
        if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
            return order.subList(fromIndex, toIndex + 1);
        }
        return Collections.emptyList();
    }
    
    public boolean isValidNavigation(String from, String to) {
        return !getValidPath(from, to).isEmpty();
    }


    public void setResult(String requestKey, Bundle result) {
        fragmentManager.setFragmentResult(requestKey, result);
    }

    public void backWithResult(String requestKey, Bundle result) {
        fragmentManager.setFragmentResult(requestKey, result);
        back();
    }
    
    public void printDAG() {
        System.out.println("DAG Structure:");
        for (Map.Entry<String, List<String>> entry : dag.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("Degrees: " + degree);
        System.out.println("Topological Order: " + kahn());
    }
}