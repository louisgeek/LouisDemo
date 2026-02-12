# MyBottomSheetBehavior

```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- 主内容区域 -->
    <FrameLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
 
    </FrameLayout>

    <!-- 可拖动底部面板  Bottom Sheet 使用 BottomSheetBehavior 实现拖动效果 -->
    <LinearLayout
        android:id="@+id/bottomSheet"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:elevation="8dp"
        app:layout_behavior="@string/bottom_sheet_behavior"
        app:behavior_hideable="true"
        app:behavior_peekHeight="120dp">

    </LinearLayout>

    </androidx.coordinatorlayout.widget.CoordinatorLayout>
```
