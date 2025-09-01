package com.louis.mynavi.dag;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Fragment节点管理器，负责管理所有页面节点及其状态
 */
public class PageNodeManager {
    private static final String TAG = "PageNodeManager";
    private Map<String, PageNode> pageNodes = new HashMap<>();
    private Set<String> completedNodes = new HashSet<>();
    private String startPageId;

    // 添加页面节点
    public void addPageNode(PageNode node) {
        if (node != null && !pageNodes.containsKey(node.getPageId())) {
            pageNodes.put(node.getPageId(), node);
        }
    }

    // 获取页面节点
    public PageNode getPageNode(String pageId) {
        return pageNodes.get(pageId);
    }

    // 设置起始页面ID
    public void setStartPageId(String pageId) {
        if (pageNodes.containsKey(pageId)) {
            this.startPageId = pageId;
        } else {
            Log.e(TAG, "起始页面不存在: " + pageId);
        }
    }

    // 获取起始页面节点
    public PageNode getStartPageNode() {
        return startPageId != null ? pageNodes.get(startPageId) : null;
    }

    // 标记页面为已访问
    public void markNodeCompleted(String pageId) {
        if (pageNodes.containsKey(pageId)) {
            completedNodes.add(pageId);
        }
    }

    // 检查页面是否已访问
    public boolean isCompleted(String pageId) {
        return completedNodes.contains(pageId);
    }

    // 更新节点的下一页
    public void updateNextPage(String currentPageId, String nextPageId) {
        PageNode currentNode = getPageNode(currentPageId);
        if (currentNode != null && pageNodes.containsKey(nextPageId)) {
            currentNode.setNextPageId(nextPageId);
            Log.d(TAG, "已更新页面 " + currentPageId + " 的下一页为 " + nextPageId);
        }
    }

    // 获取下一个可访问的页面
    public PageNode getNextAccessiblePage(String currentPageId) {
        PageNode currentNode = getPageNode(currentPageId);
        if (currentNode == null) {
            return null;
        }

        String nextPageId = currentNode.getNextPageId();
        if (nextPageId != null) {
            PageNode nextNode = getPageNode(nextPageId);
            if (nextNode != null && nextNode.isAccessible(this)) {
                return nextNode;
            }
        }
        return null;
    }

    // 清除导航状态
    public void clearNavigationState() {
        completedNodes.clear();
    }
}
