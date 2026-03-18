# 资源文件说明

## 应用图标

应用图标位于 `app/src/main/res/mipmap-*` 目录下

### 图标尺寸
- mipmap-mdpi: 48x48 px
- mipmap-hdpi: 72x72 px
- mipmap-xhdpi: 96x96 px
- mipmap-xxhdpi: 144x144 px
- mipmap-xxxhdpi: 192x192 px

### 自定义图标
1. 使用 Android Studio 的 Image Asset Studio
2. 右键点击 `res` 文件夹
3. 选择 New > Image Asset
4. 配置图标并生成

## 字符串资源

位于 `app/src/main/res/values/strings.xml`

### 添加多语言支持
```
res/
  values/           # 默认（英文）
  values-zh/        # 中文
  values-ja/        # 日语
```

## 颜色资源

位于 `app/src/main/res/values/colors.xml`

### 主题颜色
- Primary: 主色调
- Secondary: 辅助色
- Tertiary: 第三色

## 主题配置

位于 `app/src/main/java/com/louis/mycomposeproject/ui/theme/`

### 自定义主题
- Color.kt: 颜色定义
- Theme.kt: 主题配置
- Type.kt: 字体样式
