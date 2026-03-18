# 开发指南

## 代码规范

### Kotlin 代码风格
- 使用 4 空格缩进
- 遵循 Kotlin 官方代码风格
- 使用有意义的变量名

### 命名规范
- **类名**: PascalCase (例: `LoginViewModel`)
- **函数名**: camelCase (例: `getUserData`)
- **常量**: UPPER_SNAKE_CASE (例: `BASE_URL`)
- **变量**: camelCase (例: `userName`)

### 文件组织
```
- 每个类一个文件
- 文件名与类名一致
- 相关类放在同一包下
```

## 添加新功能

### 1. 添加新的 API 接口
```kotlin
// 1. 在 AuthApi.kt 添加接口
@GET("user/profile")
suspend fun getProfile(): ProfileResponse

// 2. 在 AuthDto.kt 添加数据类
data class ProfileResponse(
    val id: String,
    val name: String
)

// 3. 在 Repository 添加方法
suspend fun getProfile(): Result<Profile>
```

### 2. 添加新的 UseCase
```kotlin
class GetProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Profile> {
        return repository.getProfile()
    }
}
```

### 3. 添加新的 Screen
```kotlin
// 1. 创建 ViewModel
class ProfileViewModel(private val useCase: GetProfileUseCase) : ViewModel()

// 2. 创建 Screen
@Composable
fun ProfileScreen(viewModel: ProfileViewModel)

// 3. 在 MainActivity 添加路由
composable("profile") {
    ProfileScreen(viewModel)
}
```

## 测试

### 单元测试
```kotlin
@Test
fun `test case description`() {
    // Arrange
    val input = "test"
    
    // Act
    val result = function(input)
    
    // Assert
    assertEquals(expected, result)
}
```

### UI 测试
```kotlin
@Test
fun loginButtonIsDisplayed() {
    composeTestRule.setContent {
        LoginScreen()
    }
    composeTestRule.onNodeWithText("Login").assertIsDisplayed()
}
```

## Git 工作流

### 分支命名
- `feature/功能名` - 新功能
- `bugfix/问题描述` - Bug 修复
- `hotfix/紧急修复` - 紧急修复

### Commit 规范
```
feat: 添加用户资料页面
fix: 修复登录按钮点击无响应
docs: 更新 README 文档
refactor: 重构网络请求代码
test: 添加登录功能测试
```

## 架构原则

### Clean Architecture 层次
1. **Presentation 层**: 只负责 UI 展示
2. **Domain 层**: 包含业务逻辑
3. **Data 层**: 处理数据获取

### 依赖规则
- Presentation → Domain → Data
- 内层不依赖外层
- 使用接口解耦

## 性能优化

### Compose 优化
- 使用 `remember` 缓存状态
- 避免在 Composable 中执行耗时操作
- 使用 `LaunchedEffect` 处理副作用

### 网络优化
- 添加请求缓存
- 使用连接池
- 设置合理的超时时间

## 安全建议

- 不要在代码中硬编码敏感信息
- 使用 ProGuard 混淆代码
- HTTPS 通信
- Token 安全存储
