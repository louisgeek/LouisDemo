# 性能优化指南

## 启动优化

### 1. 延迟初始化
```kotlin
// 在 Application 中
override fun onCreate() {
    super.onCreate()
    // 必要的初始化
    Dependencies.init(this)
    
    // 延迟初始化
    lifecycleScope.launch {
        delay(100)
        initializeNonCriticalComponents()
    }
}
```

### 2. 启动页优化
- 减少启动页停留时间（1-2秒）
- 避免在启动页执行耗时操作
- 使用 SplashScreen API (Android 12+)

### 3. 懒加载
```kotlin
val heavyObject by lazy {
    // 只在首次使用时初始化
    HeavyObject()
}
```

## 内存优化

### 1. 避免内存泄漏
```kotlin
// 使用 WeakReference
private var activityRef: WeakReference<Activity>? = null

// 及时清理
override fun onDestroy() {
    super.onDestroy()
    activityRef?.clear()
    activityRef = null
}
```

### 2. 图片优化
```kotlin
// 使用 Coil 或 Glide 加载图片
// 设置合适的缓存策略
// 压缩大图片
```

### 3. 列表优化
```kotlin
// 使用 LazyColumn 而不是 Column + ScrollView
LazyColumn {
    items(list) { item ->
        ItemView(item)
    }
}
```

## Compose 优化

### 1. 避免不必要的重组
```kotlin
// 使用 remember
val state = remember { mutableStateOf("") }

// 使用 derivedStateOf
val filteredList = remember(list, query) {
    derivedStateOf { list.filter { it.contains(query) } }
}
```

### 2. 使用 key
```kotlin
LazyColumn {
    items(list, key = { it.id }) { item ->
        ItemView(item)
    }
}
```

### 3. 稳定的参数
```kotlin
// 使用 @Stable 或 @Immutable
@Immutable
data class User(val id: String, val name: String)
```

## 网络优化

### 1. 请求缓存
```kotlin
val cacheSize = 10 * 1024 * 1024 // 10 MB
val cache = Cache(context.cacheDir, cacheSize.toLong())

OkHttpClient.Builder()
    .cache(cache)
    .build()
```

### 2. 连接池
```kotlin
OkHttpClient.Builder()
    .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
    .build()
```

### 3. 请求合并
```kotlin
// 批量请求而不是多次单独请求
suspend fun fetchMultipleData() = coroutineScope {
    val user = async { api.getUser() }
    val posts = async { api.getPosts() }
    Pair(user.await(), posts.await())
}
```

## 数据库优化

### 1. 使用索引
```sql
CREATE INDEX idx_user_email ON users(email)
```

### 2. 批量操作
```kotlin
// 使用事务
database.runInTransaction {
    items.forEach { item ->
        dao.insert(item)
    }
}
```

### 3. 分页加载
```kotlin
// 使用 Paging 3
@Query("SELECT * FROM users LIMIT :limit OFFSET :offset")
suspend fun getUsers(limit: Int, offset: Int): List<User>
```

## APK 大小优化

### 1. 启用 ProGuard
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
    }
}
```

### 2. 使用 AAB
```bash
./gradlew bundleRelease
```

### 3. 移除未使用资源
```kotlin
android {
    buildTypes {
        release {
            isShrinkResources = true
        }
    }
}
```

### 4. 使用 WebP
- 将 PNG/JPG 转换为 WebP
- 减少 50-80% 的大小

## 电池优化

### 1. 合理使用后台任务
```kotlin
// 使用 WorkManager
val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()
```

### 2. 避免频繁唤醒
```kotlin
// 使用 AlarmManager 的不精确定时
alarmManager.setInexactRepeating(...)
```

### 3. 位置更新优化
```kotlin
// 使用合适的精度和频率
locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
locationRequest.interval = 60000 // 1 分钟
```

## 监控工具

### 1. Android Profiler
- CPU Profiler
- Memory Profiler
- Network Profiler
- Energy Profiler

### 2. LeakCanary
```kotlin
dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
}
```

### 3. Systrace
```bash
python systrace.py --time=10 -o trace.html sched gfx view
```

## 性能指标

### 关键指标
- 启动时间：< 2 秒
- 帧率：60 FPS
- 内存使用：< 100 MB
- APK 大小：< 20 MB
- 网络请求：< 3 秒

## 测试建议

1. 在低端设备上测试
2. 测试弱网环境
3. 长时间运行测试
4. 内存压力测试
5. 电池消耗测试
