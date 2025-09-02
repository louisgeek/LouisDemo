package com.louis.mynavi.node;

import java.util.*;

/**
 * PageNode类表示页面节点，包含fragmentTag、fragmentClass、dependencies、condition、args等字段
 */
public class PageNode3 {
    private String fragmentTag;
    private String fragmentClass;
    private Map<String, Object> args;
    private List<String> dependencies;
    private String condition;

    public PageNode3(String fragmentTag, String fragmentClass) {
        this.fragmentTag = fragmentTag;
        this.fragmentClass = fragmentClass;
        this.dependencies = new ArrayList<>();
        this.args = new HashMap<>();
    }

    // Getters
    public String getFragmentTag() {
        return fragmentTag;
    }

    public String getFragmentClass() {
        return fragmentClass;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public String getCondition() {
        return condition;
    }

    // Setters
    public void setFragmentTag(String fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    public void setFragmentClass(String fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * 添加依赖项
     *
     * @param dependency 依赖的页面节点标识
     */
    public void addDependency(String dependency) {
        if (!dependencies.contains(dependency)) {
            dependencies.add(dependency);
        }
    }

    /**
     * 添加参数
     *
     * @param key   参数键
     * @param value 参数值
     */
    public void addArg(String key, Object value) {
        args.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageNode3 pageNode = (PageNode3) o;
        return Objects.equals(fragmentTag, pageNode.fragmentTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fragmentTag);
    }

    @Override
    public String toString() {
        return "PageNode{" +
                "fragmentTag='" + fragmentTag + '\'' +
                ", fragmentClass='" + fragmentClass + '\'' +
                ", dependencies=" + dependencies +
                ", condition='" + condition + '\'' +
                '}';
    }
}