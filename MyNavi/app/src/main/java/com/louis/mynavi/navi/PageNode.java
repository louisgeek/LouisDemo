package com.louis.mynavi.navi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

/**
 * 每个页面对应一个节点
 */
public class PageNode {
    public String fragmentTag;
    public Class<? extends Fragment> fragmentClass;
    public Set<PageNode> dependencies = new HashSet<>(); // 依赖的前置节点  依赖节点集合​​
    public Bundle args;
    public PageCondition condition = () -> true; //跳转条件  ​​条件检查函数

    public PageNode(Class<? extends Fragment> fragmentClass) {
        this(fragmentClass, () -> true);
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
                ", condition isSatisfied=" + condition.isSatisfied() +
                '}';
    }


}