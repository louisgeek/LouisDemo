# 故障排查指南

## 常见问题

### 1. 构建失败

#### Gradle 同步失败
```
问题：Could not resolve dependencies
解决：
1. 检查网络连接
2. 清理缓存：./gradlew clean
3. 删除 .gradle 文件夹
4. 重新同步
```

#### 编译错误
```
问题：Compilation failed
解决：
1. 检查 JDK 版本（需要 JDK 11）
2. 更新 Gradle 版本
3. 检查依赖版本冲突
4. 清理并重新构建
```

### 2. 运行时错误

#### 网络请求失败
```
问题：Unable to resolve host / Connection timeout
解决：
1. 检查网络权限（AndroidManifest.xml）
2. 检查 API 地址配置（Constants.kt）
3. 检查设备网络连接
4. 查看 Logcat 日志
5. 测试 API 是否可访问
```

#### 登录失败
```
问题：Login failed with 401
解决：
1. 检查用户名密码是否正确
2. 检查 API 接口是否正常
3. 查看服务器返回的错误信息
4. 检查 Token 是否过期
```

#### 应用崩溃
```
问题：App crashes on startup
解决：
1. 查看 Logcat 崩溃日志
2. 检查 ProGuard 规则
3. 检查依赖初始化
4. 检查权限声明
```

### 3. UI 问题

#### 界面显示异常
```
问题：UI elements not displaying correctly
解决：
1. 检查 Compose 版本
2. 清理并重新构建
3. 检查主题配置
4. 检查设备兼容性
```

#### 键盘遮挡输入框
```
问题：Keyboard covers input fields
解决：
1. 检查 windowSoftInputMode 配置
2. 使用 adjustResize 模式
3. 添加滚动容器
```

### 4. 数据问题

#### SharedPreferences 数据丢失
```
问题：User data lost after app restart
解决：
1. 检查 apply() 是否调用
2. 检查文件权限
3. 检查应用是否被清理数据
4. 添加日志跟踪
```

#### 数据不同步
```
问题：Data not syncing
解决：
1. 检查网络连接
2. 检查 API 响应
3. 检查数据序列化
4. 添加错误处理
```

## 调试技巧

### 1. 日志调试
```kotlin
// 使用 Logger 工具
Logger.d("Debug message")
Logger.e("Error message", exception)
```

### 2. 网络调试
```
使用 OkHttp Logging Interceptor
查看请求和响应详情
```

### 3. 布局调试
```
使用 Android Studio Layout Inspector
检查 Compose 层次结构
```

### 4. 性能调试
```
使用 Android Profiler
检查内存泄漏
分析 CPU 使用
```

## 错误代码

### HTTP 状态码
- 400: 请求参数错误
- 401: 未授权（登录失败）
- 403: 禁止访问
- 404: 资源不存在
- 409: 冲突（用户已存在）
- 500: 服务器错误
- 503: 服务不可用

### 应用错误码
- E001: 网络连接失败
- E002: 数据解析错误
- E003: 本地存储错误
- E004: 验证失败

## 性能优化

### 1. 启动优化
- 延迟初始化
- 异步加载
- 减少启动页停留时间

### 2. 内存优化
- 避免内存泄漏
- 及时释放资源
- 使用弱引用

### 3. 网络优化
- 添加缓存
- 压缩请求
- 批量请求

### 4. UI 优化
- 减少重组
- 使用 remember
- 懒加载列表

## 获取帮助

### 查看日志
```bash
# 查看应用日志
adb logcat | grep MyComposeProject

# 查看崩溃日志
adb logcat | grep AndroidRuntime
```

### 生成 Bug 报告
```bash
adb bugreport
```

### 联系支持
- GitHub Issues: [项目地址]/issues
- Email: support@example.com
- 文档: 查看 README.md

## 预防措施

1. ✅ 定期更新依赖
2. ✅ 编写单元测试
3. ✅ 代码审查
4. ✅ 使用版本控制
5. ✅ 监控应用性能
6. ✅ 收集用户反馈
7. ✅ 定期备份数据
