package com.louis.mynavi.nav;


import androidx.fragment.app.Fragment;

import com.louis.mynavi.navi.PageCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * DAG节点，代表流程中的一个步骤或任务
 */
public class NavNode {
    private final String fragmentTag;
    public Class<? extends Fragment> fragmentClass;

    private final Set<NavNode> dependencies = new HashSet<>();
    private PageCondition condition;


    public NavNode(Class<? extends Fragment> fragmentClass) {
        this(fragmentClass, () -> false);
    }

    public NavNode(Class<? extends Fragment> fragmentClass, PageCondition condition) {
        this.fragmentTag = fragmentClass.getSimpleName();
        this.fragmentClass = fragmentClass;
        this.condition = condition;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public Set<NavNode> getDependencies() {
        return dependencies;
    }

    // 添加依赖节点
    public void addDependency(NavNode node) {
        dependencies.add(node);
    }

    // 添加多个依赖节点
    public void addDependency(NavNode... nodes) {
        Collections.addAll(dependencies, nodes);
    }


    // 检查节点条件是否满足
    public boolean isSatisfied() {
        if (condition != null && !condition.isSatisfied()) {
            return false;
        }
        for (NavNode dependency : dependencies) {
            if (!dependency.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    // 检查是否依赖于指定节点
    public boolean isDependentOn(NavNode node) {
        if (dependencies.contains(node)) {
            return true;
        }

        // 递归检查间接依赖
        for (NavNode dependency : dependencies) {
            if (dependency.isDependentOn(node)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public int hashCode() {
        return Objects.hash(fragmentTag);
    }

    // 打印依赖关系树
    public String printDependencyTree() {
        return printDependencyTree("", true, new HashSet<>());
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
        String conditionStatus = (condition != null && !condition.isSatisfied()) ? " 未完成要显示" : " 已完成跳过";
        builder.append(conditionStatus).append("\n");

        // 递归打印所有依赖
        List<NavNode> dependencyList = new ArrayList<>(dependencies);
        for (int i = 0; i < dependencyList.size(); i++) {
            boolean isLastDependency = i == dependencyList.size() - 1;
            builder.append(dependencyList.get(i).printDependencyTree(childIndent, isLastDependency, new HashSet<>(visited)));
        }

        return builder.toString();
    }
}