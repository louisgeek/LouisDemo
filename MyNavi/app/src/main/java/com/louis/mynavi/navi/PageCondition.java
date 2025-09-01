package com.louis.mynavi.navi;

public interface PageCondition {
    /**
     * 校验条件是否满足
     *
     * @return true：条件满足，允许跳转；false：条件不满足，拦截跳转
     */
    boolean isSatisfied();
}