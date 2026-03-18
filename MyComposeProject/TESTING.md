# 测试指南

## 测试金字塔

```
       /\
      /  \     E2E Tests (10%)
     /____\
    /      \   Integration Tests (20%)
   /________\
  /          \ Unit Tests (70%)
 /____________\
```

## 单元测试

### 1. ViewModel 测试
```kotlin
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var loginUseCase: LoginUseCase
    
    @Before
    fun setup() {
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }
    
    @Test
    fun `login success updates state`() = runTest {
        // Given
        val user = User("1", "test@example.com", "token")
        coEvery { loginUseCase("test@example.com", "password") } returns Result.success(user)
        
        // When
        viewModel.login("test@example.com", "password")
        
        // Then
        assertTrue(viewModel.state.value.isSuccess)
    }
}
```

### 2. UseCase 测试
```kotlin
class LoginUseCaseTest {
    private lateinit var useCase: LoginUseCase
    private lateinit var repository: AuthRepository
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = LoginUseCase(repository)
    }
    
    @Test
    fun `empty email returns failure`() = runTest {
        // When
        val result = useCase("", "password")
        
        // Then
        assertTrue(result.isFailure)
    }
}
```

### 3. Repository 测试
```kotlin
class AuthRepositoryTest {
    private lateinit var repository: AuthRepositoryImpl
    private lateinit var api: AuthApi
    private lateinit var prefs: PreferencesManager
    
    @Before
    fun setup() {
        api = mockk()
        prefs = mockk(relaxed = true)
        repository = AuthRepositoryImpl(api, prefs)
    }
    
    @Test
    fun `login success saves token`() = runTest {
        // Given
        val response = AuthResponse("1", "test@example.com", "token")
        coEvery { api.login(any()) } returns response
        
        // When
        repository.login("test@example.com", "password")
        
        // Then
        verify { prefs.saveToken("token") }
    }
}
```

## UI 测试

### 1. Compose 测试
```kotlin
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loginButton_isDisplayed() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockk(relaxed = true),
                onNavigateToRegister = {},
                onLoginSuccess = {}
            )
        }
        
        composeTestRule
            .onNodeWithText("Login")
            .assertIsDisplayed()
    }
    
    @Test
    fun emailInput_acceptsText() {
        composeTestRule.setContent {
            LoginScreen(...)
        }
        
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")
        
        composeTestRule
            .onNodeWithText("test@example.com")
            .assertExists()
    }
}
```

### 2. 导航测试
```kotlin
@Test
fun navigation_fromLoginToRegister() {
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    
    composeTestRule.setContent {
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        AppNavigation(navController)
    }
    
    composeTestRule
        .onNodeWithText("Register")
        .performClick()
    
    assertEquals(Routes.REGISTER, navController.currentBackStackEntry?.destination?.route)
}
```

## 集成测试

### 1. API 测试
```kotlin
class ApiIntegrationTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: AuthApi
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        api = retrofit.create(AuthApi::class.java)
    }
    
    @Test
    fun login_returnsUser() = runTest {
        // Given
        val response = """{"id":"1","email":"test@example.com","token":"token"}"""
        mockWebServer.enqueue(MockResponse().setBody(response))
        
        // When
        val result = api.login(LoginRequest("test@example.com", "password"))
        
        // Then
        assertEquals("1", result.id)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
```

## 测试覆盖率

### 配置 JaCoCo
```kotlin
// build.gradle.kts
plugins {
    id("jacoco")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(files("build/intermediates/javac/debug"))
    executionData.setFrom(files("build/jacoco/testDebugUnitTest.exec"))
}
```

### 运行覆盖率
```bash
./gradlew jacocoTestReport
```

## 测试最佳实践

### 1. AAA 模式
```kotlin
@Test
fun testExample() {
    // Arrange - 准备测试数据
    val input = "test"
    
    // Act - 执行测试
    val result = function(input)
    
    // Assert - 验证结果
    assertEquals(expected, result)
}
```

### 2. 使用有意义的测试名称
```kotlin
// ❌ 不好
@Test
fun test1() { }

// ✅ 好
@Test
fun `login with empty email returns error`() { }
```

### 3. 一个测试一个断言
```kotlin
// ❌ 不好
@Test
fun testMultiple() {
    assertEquals(1, result1)
    assertEquals(2, result2)
    assertEquals(3, result3)
}

// ✅ 好
@Test
fun testResult1() {
    assertEquals(1, result1)
}

@Test
fun testResult2() {
    assertEquals(2, result2)
}
```

### 4. 使用测试夹具
```kotlin
class MyTest {
    private lateinit var subject: MyClass
    
    @Before
    fun setup() {
        subject = MyClass()
    }
    
    @After
    fun tearDown() {
        subject.cleanup()
    }
}
```

## 测试工具

### 依赖
```kotlin
dependencies {
    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // UI Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    
    // Mock Server
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
```

## 运行测试

### 命令行
```bash
# 运行所有测试
./gradlew test

# 运行单元测试
./gradlew testDebugUnitTest

# 运行 UI 测试
./gradlew connectedAndroidTest

# 运行特定测试
./gradlew test --tests "LoginViewModelTest"
```

### Android Studio
- 右键点击测试类/方法
- 选择 "Run 'TestName'"
- 或使用快捷键 Ctrl+Shift+F10

## 持续集成

### GitHub Actions
```yaml
- name: Run tests
  run: ./gradlew test

- name: Upload test results
  uses: actions/upload-artifact@v3
  with:
    name: test-results
    path: app/build/test-results/
```

## 测试检查清单

- [ ] 所有 UseCase 有单元测试
- [ ] 所有 ViewModel 有单元测试
- [ ] 所有 Repository 有单元测试
- [ ] 关键 UI 流程有测试
- [ ] 网络请求有集成测试
- [ ] 测试覆盖率 > 70%
- [ ] 所有测试都能通过
- [ ] CI/CD 集成测试
