package com.louis.mynavi.dag;


import androidx.fragment.app.Fragment;

/**
 * 可导航的Fragment基类，提供下一页点击和页面更新回调
 */
public abstract class NavigableFragment extends Fragment {
    private Runnable onNextClickListener;
    private PageNavigator.OnPageUpdatedListener onPageUpdatedListener;

    // 设置下一页点击监听器
    public void setOnNextClickListener(Runnable listener) {
        this.onNextClickListener = listener;
    }

    // 设置页面更新监听器
    public void setOnPageUpdatedListener(PageNavigator.OnPageUpdatedListener listener) {
        this.onPageUpdatedListener = listener;
    }

    // 触发下一页点击事件
    protected void onNextClick() {
        if (onNextClickListener != null) {
            onNextClickListener.run();
        }
    }

    // 通知页面已更新，可能需要重新导航
    protected void notifyPageUpdated() {
        if (onPageUpdatedListener != null) {
            onPageUpdatedListener.onPageUpdated();
        }
    }

    // 在子类中调用此方法可以动态更新下一页
    protected void updateNextPage(String nextPageId) {
        PageNavigator.getInstance().updateCurrentPageNext(nextPageId);
        notifyPageUpdated(); // 通知页面已更新
    }
}
