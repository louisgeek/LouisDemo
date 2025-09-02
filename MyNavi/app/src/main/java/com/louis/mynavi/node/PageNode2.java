package com.louis.mynavi.node;

import android.os.Bundle;

import com.louis.mynavi.dag.Condition;

import java.util.HashSet;
import java.util.Set;

public class PageNode2 {
    private String fragmentTag;
    private Class<?> fragmentClass;
    private Set<PageNode2> dependencies = new HashSet<>();
    private Condition condition;
    private Bundle args;
    private boolean completed = false;

    public PageNode2(String fragmentTag, Class<?> fragmentClass, Condition condition) {
        this.fragmentTag = fragmentTag;
        this.fragmentClass = fragmentClass;
        this.condition = condition;
    }

    public void addDependency(PageNode2 dependency) {
        dependencies.add(dependency);
    }

    public String getFragmentTag() {
        return fragmentTag;
    }

    public Class<?> getFragmentClass() {
        return fragmentClass;
    }

    public Set<PageNode2> getDependencies() {
        return dependencies;
    }

    public Condition getCondition() {
        return condition;
    }

    public Bundle getArgs() {
        return args;
    }

    public void setArgs(Bundle args) {
        this.args = args;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}