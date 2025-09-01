package com.louis.mynavi.dag;

import java.util.HashSet;
import java.util.Set;

public class DagNode {
    private final String id;
    private final String route;
    private final Set<DagNode> dependencies = new HashSet<>();
    private final Condition condition;
    private boolean isCompleted;

    public DagNode(String id, String route) {
        this(id, route, null);
    }

    public DagNode(String id, String route, Condition condition) {
        this.id = id;
        this.route = route;
        this.condition = condition;
    }

    public void addDependency(DagNode node) {
        dependencies.add(node);
    }

    public void removeDependency(DagNode node) {
        dependencies.remove(node);
    }

    public Set<DagNode> getDependencies() {
        return new HashSet<>(dependencies);
    }

    public Condition getCondition() {
        return condition;
    }

    /**
     * 节点可用条件：依赖全部完成 **且** 条件满足（若有条件）
     */
    public boolean isAvailable() {
        // 依赖未全部完成 → 不可用
        for (DagNode dep : dependencies) {
            if (!dep.isCompleted()) {
                return false;
            }
        }
        // 条件不满足 → 不可用（若有条件）
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