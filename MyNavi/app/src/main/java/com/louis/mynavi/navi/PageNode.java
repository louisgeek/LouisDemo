package com.louis.mynavi.navi;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PageNode {
    public String fragmentTag;
    public Class<? extends Fragment> fragmentClass;
    public List<String> dependencies = new ArrayList<>(); // 依赖的前置节点
    public Bundle args;
    public PageCondition condition; //跳转条件
}