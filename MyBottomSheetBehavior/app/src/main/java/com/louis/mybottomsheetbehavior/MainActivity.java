package com.louis.mybottomsheetbehavior;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {

    private LinearLayout titleBar;
    private ImageView btnClose;
    private ImageButton btnBack;
    private Button btnShowPoint;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    // 是否支持中间停留
    private boolean isSupportMiddleState = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBottomSheet();
        initListeners();
    }

    private void initViews() {
        titleBar = findViewById(R.id.title_bar);
        btnClose = findViewById(R.id.btn_close);
        btnBack = findViewById(R.id.btn_back);
        btnShowPoint = findViewById(R.id.btn_show_point);
    }

    private void initBottomSheet() {
        // 从布局中获取 BottomSheetBehavior
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //折叠高度（底部小面板高度）
//        bottomSheetBehavior.setPeekHeight(120);
        // 设置折叠状态的高度（peek height）
        int peekHeight = (int) (120 * getResources().getDisplayMetrics().density);
        bottomSheetBehavior.setPeekHeight(peekHeight);

        //允许隐藏
        bottomSheetBehavior.setHideable(true);

        if (isSupportMiddleState) {
            //中间停留比例 中间停靠高度（如50%位置）
            // 设置中间停留比例（屏幕高度的50%）
            bottomSheetBehavior.setFitToContents(false); // 需要禁用才支持half_expanded
            bottomSheetBehavior.setHalfExpandedRatio(0.5f);
        }

        // 设置展开偏移量（用于状态栏适配）
        bottomSheetBehavior.setExpandedOffset(0);

        // 初始状态为隐藏
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // 设置状态为折叠状态
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // 监听状态变化
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // 状态变化回调
                handleStateChange(newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // slideOffset: -1.0~0.0 (隐藏->折叠), 0.0~1.0 (折叠->展开)
                // 滑动过程中的回调，可用于动画
                handleSlide(slideOffset);
            }
        });
    }

    private void initListeners() {
        // 显示点位信息
        btnShowPoint.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        // 关闭按钮
        btnClose.setOnClickListener(v -> hideBottomSheet());
        // 返回按钮
        btnBack.setOnClickListener(v -> hideBottomSheet());

        // 点击地图空白区域（可选）
        findViewById(R.id.map_container).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                hideBottomSheet();
            }
        });
    }

    private void handleStateChange(int newState) {
        // STATE_COLLAPSED (收起/折叠)
        // STATE_EXPANDED (展开/全屏)
        // STATE_HALF_EXPANDED (中间)
        // STATE_HIDDEN (隐藏)
        // STATE_DRAGGING (拖动中)
        // STATE_SETTLING (动画中)
        switch (newState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                // 底部折叠状态：显示关闭按钮，隐藏标题栏
                btnClose.setVisibility(View.VISIBLE);
                titleBar.setVisibility(View.GONE);
                break;

            case BottomSheetBehavior.STATE_EXPANDED:
                // 顶部展开状态：隐藏关闭按钮，显示标题栏
                btnClose.setVisibility(View.GONE);
                titleBar.setVisibility(View.VISIBLE);
                break;
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                // 中间停留状态
//                isExpanded = false;
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                // 隐藏状态
                btnClose.setVisibility(View.VISIBLE);
                titleBar.setVisibility(View.GONE);
                break;

            case BottomSheetBehavior.STATE_DRAGGING:
                // 拖动中
                break;

            case BottomSheetBehavior.STATE_SETTLING:
                //  settling中
                // 释放后自动滑动的过程
                break;
        }
    }

    /**
     * 处理滑动过程
     *
     * slideOffset 范围：
     * -1.0 到 0.0：隐藏 -> 折叠
     * 0.0 到 1.0：折叠 -> 全屏展开
     */
    private void handleSlide(float slideOffset) {
        // 限制 slideOffset 范围
        slideOffset = Math.max(-1.0f, Math.min(1.0f, slideOffset));

        // slideOffset: -1.0~0.0 (隐藏->折叠), 0.0~1.0 (折叠->展开)
        // 可根据滑动偏移量做动画
        // slideOffset: -1.0 (隐藏) to 0.0 (折叠) to 1.0 (展开)

        // 示例：平滑过渡标题栏透明度
//        if (slideOffset > 0) {
//            titleBar.setAlpha(slideOffset);
//        }

        // 处理标题栏切换动画
        // 当滑动偏移量 > 0.3 时开始显示展开状态的标题栏
        float toolbarAlpha = 0f;
        if (slideOffset > 0.3f) {
            toolbarAlpha = (slideOffset - 0.3f) / 0.7f;
            titleBar.setAlpha(toolbarAlpha);
        }

        // 设置展开状态标题栏的透明度
//        if (sheetToolbarExpanded != null) {
//            sheetToolbarExpanded.setAlpha(toolbarAlpha);
//            sheetToolbarExpanded.setVisibility(toolbarAlpha > 0 ? View.VISIBLE : View.INVISIBLE);
//        }

        // 处理折叠状态头部区域的透明度
        float headerAlpha = 1.0f;
        if (slideOffset > 0.3f) {
            headerAlpha = 1.0f - ((slideOffset - 0.3f) / 0.7f);
        }

//        if (sheetHeaderCollapsed != null) {
//            sheetHeaderCollapsed.setAlpha(headerAlpha);
//        }

        // 模拟标记点缩放效果
//        if (mapMarker != null) {
//            float markerScale = 1.0f;
//            if (slideOffset > 0) {
//                // 向上拖动时标记点逐渐变小
//                markerScale = 1.0f - (slideOffset * 0.3f);
//            }
//            mapMarker.setScaleX(markerScale);
//            mapMarker.setScaleY(markerScale);
//        }
    }

    private BottomSheetBehavior<FrameLayout> behavior;
    /**
     * 展开面板（全屏）
     */
    private void expandedBottomSheet() {
        if (bottomSheetBehavior == null) {
            return;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * 隐藏面板
     */
    private void hideBottomSheet() {
        if (bottomSheetBehavior == null) {
            return;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * 收起面板（折叠）
     */
    private void collapseBottomSheet() {
        if (bottomSheetBehavior == null) {
            return;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    // 设置是否支持半展开状态
    private void setHalfExpandedEnabled(boolean enabled) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bottomSheetBehavior.setHalfExpandedRatio(0.5f); // 50%高度作为半展开状态
        }
    }


    // 监听外部点击事件
    private void setupOutsideTouch() {
//        bottomSheetContainer.setOnClickListener(v -> {
//            // 点击内容区域不关闭，只有拖拽或点击关闭按钮才关闭
//        });
    }

    @Override
    public void onBackPressed() {
        int currentState = bottomSheetBehavior.getState();
        if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
            // 全屏展开时，折叠面板
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (currentState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            // 中间停留时，折叠面板
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (currentState == BottomSheetBehavior.STATE_COLLAPSED) {
            // 折叠状态时，隐藏面板
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
}