# 常见问题 (FAQ)

## 一般问题

### Q: 这个项目是做什么的？
A: 这是一个采用 Clean Architecture + MVVM 架构的 Android 登录注册应用，使用 Jetpack Compose 构建 UI，Retrofit 处理网络请求。

### Q: 支持哪些 Android 版本？
A: 最低支持 Android 7.0 (API 24)，目标版本 Android 14 (API 36)。

### Q: 是否需要后端 API？
A: 是的，需要配置后端 API 地址。可以在 `util/Constants.kt` 中修改 `BASE_URL`。

### Q: 如何运行项目？
A: 
1. 克隆项目
2. 在 Android Studio 中打开
3. 配置 API 地址
4. 同步 Gradle
5. 运行到设备或模拟器

## 技术问题

### Q: 为什么不使用 Hilt 或 Dagger？
A: 为了保持项目轻量级和易于理解，采用了手动依赖注入。对于大型项目，建议使用 Hilt。

### Q: 如何添加新的 API 接口？
A: 
1. 在 `AuthApi.kt` 添加接口方法
2. 在 `AuthDto.kt` 添加数据类
3. 在 `Repository` 实现方法
4. 创建对应的 `UseCase`
5. 在 `ViewModel` 中调用

### Q: 如何修改主题颜色？
A: 编辑 `ui/theme/Color.kt` 和 `Theme.kt` 文件。

### Q: 如何添加新页面？
A:
1. 创建 Screen Composable
2. 创建 ViewModel（如需要）
3. 在 `Routes.kt` 添加路由常量
4. 在 `AppNavigation.kt` 添加路由配置

### Q: 如何处理网络错误？
A: 项目已集成 `NetworkErrorHandler`，自动处理常见的网络错误并返回友好提示。

## 配置问题

### Q: 如何配置 API 地址？
A: 修改 `util/Constants.kt` 中的 `BASE_URL` 常量。

### Q: 如何修改超时时间？
A: 修改 `util/Constants.kt` 中的 `CONNECT_TIMEOUT` 和 `READ_TIMEOUT`。

### Q: 如何启用/禁用日志？
A: 在 `MyApplication.kt` 中调用 `Logger.setDebugMode(true/false)`。

### Q: 如何配置 ProGuard？
A: 编辑 `proguard-rules.pro` 文件，已包含基本的混淆规则。

## 开发问题

### Q: 如何运行测试？
A: 执行 `./gradlew test` 或使用 `test.sh` 脚本。

### Q: 如何生成发布版本？
A: 
1. 配置签名密钥
2. 执行 `./gradlew assembleRelease`
3. 或使用 `release.sh` 脚本

### Q: 如何添加单元测试？
A: 在 `app/src/test/java` 目录下创建测试类，参考 `ValidatorTest.kt`。

### Q: 如何调试网络请求？
A: 查看 Logcat，OkHttp Logging Interceptor 会打印所有请求和响应。

## 部署问题

### Q: 如何发布到 Google Play？
A: 参考 `DEPLOYMENT.md` 文档的详细步骤。

### Q: 需要什么签名密钥？
A: 使用 keytool 生成 JKS 密钥文件，详见 `DEPLOYMENT.md`。

### Q: 如何更新版本号？
A: 修改 `app/build.gradle.kts` 中的 `versionCode` 和 `versionName`。

## 安全问题

### Q: 如何保护 API 密钥？
A: 
1. 不要硬编码在代码中
2. 使用环境变量
3. 使用 BuildConfig
4. 不要提交到版本控制

### Q: Token 如何存储？
A: 使用 SharedPreferences 加密存储，生产环境建议使用 EncryptedSharedPreferences。

### Q: 如何防止反编译？
A: 
1. 启用 ProGuard 混淆
2. 使用代码混淆
3. 不在客户端存储敏感信息

## 性能问题

### Q: 如何优化启动速度？
A: 
1. 延迟初始化
2. 减少启动页停留时间
3. 异步加载数据
4. 优化依赖注入

### Q: 如何减少 APK 大小？
A: 
1. 启用 ProGuard
2. 使用 AAB 格式
3. 移除未使用的资源
4. 压缩图片资源

### Q: 如何优化内存使用？
A: 
1. 避免内存泄漏
2. 及时释放资源
3. 使用弱引用
4. 优化图片加载

## 兼容性问题

### Q: 支持哪些设备？
A: 支持所有运行 Android 7.0+ 的设备。

### Q: 支持平板吗？
A: UI 使用 Compose 自适应布局，支持平板设备。

### Q: 支持深色模式吗？
A: 是的，已配置深色主题，跟随系统设置。

### Q: 支持多语言吗？
A: 目前支持中文和英文，可以添加更多语言资源。

## 贡献问题

### Q: 如何贡献代码？
A: 
1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request
5. 参考 `CONTRIBUTING.md`

### Q: 代码规范是什么？
A: 遵循 Kotlin 官方代码风格，使用 ktlint 检查。

### Q: 如何报告 Bug？
A: 在 GitHub Issues 中使用 Bug Report 模板提交。

## 获取帮助

### Q: 在哪里可以获得帮助？
A: 
- GitHub Issues
- 项目文档
- Email: support@example.com

### Q: 有示例项目吗？
A: 本项目本身就是完整的示例项目。

### Q: 有视频教程吗？
A: 目前没有，但文档非常详细。

## 更多问题？

如果您的问题没有在这里找到答案：
1. 查看项目文档
2. 搜索 GitHub Issues
3. 创建新的 Issue
4. 联系维护者
