package com.louis.mynavi.dag;

// Condition.java
@FunctionalInterface
public interface Condition {
    /**
     * 检查当前条件是否满足（节点可跳转的前提）
     *
     * @return true：条件满足；false：条件不满足
     */
    boolean isSatisfied();
}