# 部署指南

## Google Play Store 发布流程

### 1. 准备工作

#### 生成签名密钥
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

#### 配置签名
在 `app/build.gradle.kts` 中添加：
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "your-store-password"
            keyAlias = "my-key-alias"
            keyPassword = "your-key-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 2. 构建发布版本

#### 构建 APK
```bash
./gradlew assembleRelease
```
输出：`app/build/outputs/apk/release/app-release.apk`

#### 构建 AAB（推荐）
```bash
./gradlew bundleRelease
```
输出：`app/build/outputs/bundle/release/app-release.aab`

### 3. 测试发布版本

```bash
# 安装到设备
adb install app/build/outputs/apk/release/app-release.apk

# 测试所有功能
# 检查 ProGuard 混淆是否正常
# 验证签名
```

### 4. 上传到 Play Console

1. 登录 [Google Play Console](https://play.google.com/console)
2. 选择应用
3. 进入"发布" > "生产"
4. 创建新版本
5. 上传 AAB 文件
6. 填写版本说明
7. 提交审核

### 5. 版本管理

#### 更新版本号
在 `app/build.gradle.kts` 中：
```kotlin
defaultConfig {
    versionCode = 2  // 每次发布递增
    versionName = "1.0.1"
}
```

### 6. 发布检查清单

- [ ] 更新版本号
- [ ] 运行所有测试
- [ ] 检查 ProGuard 规则
- [ ] 测试发布版本
- [ ] 准备应用截图
- [ ] 更新应用描述
- [ ] 准备版本说明
- [ ] 检查权限声明
- [ ] 验证 API 配置
- [ ] 备份签名密钥

## 内部测试

### Alpha 测试
1. 在 Play Console 创建 Alpha 测试轨道
2. 添加测试用户
3. 上传 AAB
4. 分享测试链接

### Beta 测试
1. 创建 Beta 测试轨道
2. 设置测试用户组
3. 收集反馈
4. 修复问题

## 持续集成/持续部署 (CI/CD)

### GitHub Actions
已配置自动构建，每次推送到 main 分支时：
- 运行测试
- 构建 APK
- 上传构建产物

### 自动发布（可选）
使用 Fastlane 或 GitHub Actions 自动发布到 Play Store

## 回滚策略

如果发现严重问题：
1. 在 Play Console 停止发布
2. 回滚到上一个稳定版本
3. 修复问题
4. 重新测试
5. 发布修复版本

## 监控和分析

### Firebase Crashlytics
集成崩溃报告工具

### Google Analytics
跟踪用户行为

### Play Console 报告
- 崩溃报告
- ANR 报告
- 用户反馈
- 评分和评论

## 安全注意事项

- ⚠️ 不要将签名密钥提交到版本控制
- ⚠️ 使用环境变量存储敏感信息
- ⚠️ 定期更新依赖库
- ⚠️ 启用 ProGuard 混淆
- ⚠️ 使用 HTTPS 通信
