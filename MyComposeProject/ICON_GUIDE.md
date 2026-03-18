# 应用图标生成指南

## 在线工具

### 1. Android Asset Studio
https://romannurik.github.io/AndroidAssetStudio/

### 2. App Icon Generator
https://appicon.co/

### 3. Icon Kitchen
https://icon.kitchen/

## 使用 Android Studio

1. 右键点击 `res` 文件夹
2. 选择 New > Image Asset
3. 选择图标类型：
   - Launcher Icons (Legacy)
   - Launcher Icons (Adaptive and Legacy)
4. 上传图片或选择 Clip Art
5. 配置前景和背景
6. 点击 Next > Finish

## 图标尺寸要求

### Launcher Icon
- mdpi: 48x48 px
- hdpi: 72x72 px
- xhdpi: 96x96 px
- xxhdpi: 144x144 px
- xxxhdpi: 192x192 px

### Adaptive Icon (Android 8.0+)
- 前景层：108x108 dp
- 背景层：108x108 dp
- 安全区域：72x72 dp (中心)

## 设计建议

1. **简洁明了** - 避免过多细节
2. **识别度高** - 在小尺寸下清晰可见
3. **品牌一致** - 与应用主题匹配
4. **避免文字** - 图标应该是图形化的
5. **测试多种背景** - 确保在不同背景下都清晰

## 文件格式

- PNG (推荐)
- WebP (Android 4.0+)
- 避免使用 JPG

## 命名规范

```
ic_launcher.png          # 标准图标
ic_launcher_round.png    # 圆形图标
ic_launcher_foreground.png  # 前景层
ic_launcher_background.png  # 背景层
```

## 自适应图标 XML

```xml
<!-- res/mipmap-anydpi-v26/ic_launcher.xml -->
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
```

## 测试图标

1. 在不同设备上测试
2. 测试不同启动器
3. 测试深色/浅色主题
4. 检查圆角效果
5. 验证通知图标

## Play Store 要求

- 512x512 px 高分辨率图标
- PNG 格式
- 32 位色深
- 透明背景（可选）
