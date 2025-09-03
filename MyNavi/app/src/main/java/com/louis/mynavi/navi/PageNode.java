package com.louis.mynavi.navi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 每个页面对应一个节点
 */
public class PageNode {
    public String fragmentTag;
    public Class<? extends Fragment> fragmentClass;
    public Set<PageNode> dependencies = new HashSet<>(); // 依赖的前置节点  依赖节点集合​​
    public Bundle args;
    public PageCondition condition; //跳转条件  ​​条件检查函数

    public PageNode(Class<? extends Fragment> fragmentClass) {
        this(fragmentClass, () -> false);
    }

    public PageNode(Class<? extends Fragment> fragmentClass, PageCondition condition) {
        this.fragmentTag = fragmentClass.getSimpleName();
        this.fragmentClass = fragmentClass;
        this.condition = condition;
    }

    // 添加依赖节点
    public void addDependency(PageNode node) {
        dependencies.add(node);
    }

    public void addDependency(PageNode... nodes) {
        for (PageNode node : nodes) {
            dependencies.add(node);
        }
    }

    // 移除依赖节点
    public void removeDependency(PageNode node) {
        dependencies.remove(node);
    }

    public boolean isCompleted() {
        if (condition != null && !condition.isCompleted()) {
            return false;
        }
        for (PageNode dependency : dependencies) {
            if (!dependency.isCompleted()) {
                return false;
            }
        }
        return true;
    }
    public boolean isDependentOn(PageNode node) {
        if (dependencies.contains(node)) {
            return true;
        }
        //递归检查间接依赖
        for (PageNode dependency : dependencies) {
            if (dependency.isDependentOn(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PageNode{" +
                "fragmentTag='" + fragmentTag + '\'' +
//                ", fragmentClass=" + fragmentClass +
                ", dependencies=" + dependencies +
//                ", args=" + args +
//                ", condition=" + condition +
                ", condition isSatisfied=" + condition.isCompleted() +
                '}';
    }


    public String printDependencyTree(String indent, boolean isLast, Set<String> visited) {
        if (visited.contains(fragmentTag)) {
            return indent + (isLast ? "└── " : "├── ") + fragmentTag + " (循环依赖)\n";
        }

        visited.add(fragmentTag);
        String currentIndent = indent + (isLast ? "└── " : "├── ");
        String childIndent = indent + (isLast ? "    " : "│   ");

        StringBuilder builder = new StringBuilder();
        builder.append(currentIndent).append(fragmentTag).append(" (").append(fragmentClass).append(")");

        // 添加条件状态
        String conditionStatus = (condition != null && !condition.isCompleted()) ? " 未完成要显示" : " 已完成跳过";
        builder.append(conditionStatus).append("\n");

        // 递归打印所有依赖
        List<PageNode> dependencyList = new ArrayList<>(dependencies);
        for (int i = 0; i < dependencyList.size(); i++) {
            boolean isLastDependency = i == dependencyList.size() - 1;
            builder.append(dependencyList.get(i).printDependencyTree(childIndent, isLastDependency, new HashSet<>(visited)));
        }

        return builder.toString();
    }
}