package com.louis.mynavi.dag;// DagNode.java

import java.util.HashSet;
import java.util.Set;

public class DagNode {
    private final String id;          // 节点唯一标识（如 "nodeA"）
    private final String route;       // 目标 Fragment 的类名（如 "com.example.NodeAFragment"）
    private final Set<DagNode> dependencies = new HashSet<>(); // 依赖的前置节点
    private final Condition condition;                          // 跳转条件（可为 null）
    private boolean isCompleted;                                // 是否已完成（跳转后标记）

    // 构造函数（必选：id、route）
    public DagNode(String id, String route) {
        this(id, route, null);
    }

    // 构造函数（可选：带条件）
    public DagNode(String id, String route, Condition condition) {
        this.id = id;
        this.route = route;
        this.condition = condition;
    }

    // 添加依赖节点（需确保依赖已注册到 DAG 管理器）
    public void addDependency(DagManager dagManager, DagNode dependency) {
        if (dependency == null) {
            throw new IllegalArgumentException("依赖节点不能为 null");
        }
        if (!dagManager.nodes.containsKey(dependency.getId())) {
            throw new IllegalArgumentException("依赖节点未注册: " + dependency.getId());
        }
        dependencies.add(dependency);
    }

    // 移除依赖节点
    public void removeDependency(DagNode dependency) {
        dependencies.remove(dependency);
    }

    // 获取所有依赖节点（返回副本防外部修改）
    public Set<DagNode> getDependencies() {
        return new HashSet<>(dependencies);
    }

    // 获取条件检查器（可能为 null）
    public Condition getCondition() {
        return condition;
    }

    /**
     * 判断节点是否可用（依赖全部完成 **且** 条件满足）
     * @return true：可用；false：不可用
     */
    public boolean isAvailable() {
        // 1. 检查所有依赖是否已完成
        for (DagNode dep : dependencies) {
            if (!dep.isCompleted()) {
                return false;
            }
        }
        // 2. 检查条件是否满足（若有条件）
        return condition == null || condition.isSatisfied();
    }

    // 标记节点为已完成（跳转后调用）
    public void markCompleted() {
        isCompleted = true;
    }

    // 是否已完成
    public boolean isCompleted() {
        return isCompleted;
    }

    // 获取节点 ID
    public String getId() {
        return id;
    }

    // 获取 Fragment 路由（类名）
    public String getRoute() {
        return route;
    }
}