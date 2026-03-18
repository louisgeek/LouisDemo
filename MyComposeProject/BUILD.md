# 构建说明

## 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 11 或更高版本
- Android SDK API 36
- Gradle 8.13

## 构建步骤

### 1. 克隆项目
```bash
git clone <repository-url>
cd MyComposeProject
```

### 2. 配置 API 地址
编辑 `app/src/main/java/com/louis/mycomposeproject/util/Constants.kt`:
```kotlin
const val BASE_URL = "https://your-api-url.com/"
```

### 3. 同步 Gradle
```bash
./gradlew clean build
```

### 4. 运行项目
```bash
./gradlew installDebug
```

或在 Android Studio 中点击 Run 按钮

## 构建变体

### Debug
```bash
./gradlew assembleDebug
```

### Release
```bash
./gradlew assembleRelease
```

## 运行测试

### 单元测试
```bash
./gradlew test
```

### UI 测试
```bash
./gradlew connectedAndroidTest
```

## 常见问题

### 1. Gradle 同步失败
- 检查网络连接
- 清理缓存: `./gradlew clean`
- 删除 `.gradle` 文件夹重新同步

### 2. 编译错误
- 确保 JDK 版本正确
- 检查 SDK 版本是否安装

### 3. 运行时错误
- 检查 API 地址配置
- 确保网络权限已添加
- 查看 Logcat 日志
