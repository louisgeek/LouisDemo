package com.louis.mynavi.dag;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment页面节点，封装页面信息、依赖关系和访问条件
 */
public class PageNode implements Parcelable {
    private String pageId; // 页面唯一标识
    private Class<? extends Fragment> fragmentClass; // 对应的Fragment类
    private List<String> dependencies; // 依赖的页面ID列表
    private PageCondition condition; // 访问条件
    private String nextPageId; // 下一页ID

    public PageNode(String pageId, Class<? extends Fragment> fragmentClass) {
        this.pageId = pageId;
        this.fragmentClass = fragmentClass;
        this.dependencies = new ArrayList<>();
    }

    // 检查页面是否可访问（满足条件且依赖已满足）
    public boolean isAccessible(PageNodeManager manager) {
        // 检查条件是否满足
        if (condition != null && !condition.isSatisfied()) {
            return false;
        }

        // 检查所有依赖是否已满足
        for (String dependency : dependencies) {
            if (!manager.isCompleted(dependency)) {
                return false;
            }
        }
        return true;
    }

    // 添加依赖页面
    public void addDependency(String pageId) {
        if (!dependencies.contains(pageId)) {
            dependencies.add(pageId);
        }
    }

    // Parcelable实现
    protected PageNode(Parcel in) {
        pageId = in.readString();
        try {
            fragmentClass = (Class<? extends Fragment>) Class.forName(in.readString());
        } catch (ClassNotFoundException e) {
            fragmentClass = null;
        }
        dependencies = in.createStringArrayList();
        nextPageId = in.readString();
    }

    public static final Creator<PageNode> CREATOR = new Creator<PageNode>() {
        @Override
        public PageNode createFromParcel(Parcel in) {
            return new PageNode(in);
        }

        @Override
        public PageNode[] newArray(int size) {
            return new PageNode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pageId);
        dest.writeString(fragmentClass.getName());
        dest.writeStringList(dependencies);
        dest.writeString(nextPageId);
    }

    // Getters and Setters
    public String getPageId() {
        return pageId;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setCondition(PageCondition condition) {
        this.condition = condition;
    }

    public String getNextPageId() {
        return nextPageId;
    }

    public void setNextPageId(String nextPageId) {
        this.nextPageId = nextPageId;
    }
}

// 页面访问条件接口
interface PageCondition {
    boolean isSatisfied();
}
