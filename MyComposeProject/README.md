# Android 登录注册项目

## 架构说明

采用 **Clean Architecture + MVVM** 官方架构模式

### 项目结构

```
app/
├── data/                          # 数据层
│   ├── local/                     # 本地数据源
│   │   └── PreferencesManager.kt  # SharedPreferences 管理
│   ├── remote/                    # 远程数据源
│   │   ├── ApiClient.kt           # Retrofit 客户端配置
│   │   ├── AuthApi.kt             # API 接口定义
│   │   └── AuthDto.kt             # 网络请求/响应数据类
│   └── repository/                # 仓库实现
│       └── AuthRepositoryImpl.kt  # 认证仓库实现
│
├── domain/                        # 领域层（业务逻辑）
│   ├── common/                    # 通用类
│   │   └── UiState.kt             # UI 状态封装
│   ├── model/                     # 领域模型
│   │   └── User.kt                # 用户模型
│   ├── repository/                # 仓库接口
│   │   └── AuthRepository.kt      # 认证仓库接口
│   └── usecase/                   # 用例（业务逻辑）
│       ├── LoginUseCase.kt        # 登录用例
│       ├── RegisterUseCase.kt     # 注册用例
│       └── LogoutUseCase.kt       # 登出用例
│
├── presentation/                  # 表现层（UI）
│   ├── login/                     # 登录模块
│   │   ├── LoginViewModel.kt      # 登录 ViewModel
│   │   └── LoginScreen.kt         # 登录界面
│   ├── register/                  # 注册模块
│   │   ├── RegisterViewModel.kt   # 注册 ViewModel
│   │   └── RegisterScreen.kt      # 注册界面
│   ├── home/                      # 主页模块
│   │   └── HomeScreen.kt          # 主页界面
│   └── ViewModelFactory.kt        # ViewModel 工厂
│
├── ui/theme/                      # UI 主题
├── Dependencies.kt                # 手动依赖注入
└── MainActivity.kt                # 主 Activity
```

## 技术栈

- **UI**: Jetpack Compose + Material3
- **网络**: Retrofit + OkHttp + Gson
- **架构**: Clean Architecture + MVVM
- **导航**: Navigation Compose
- **状态管理**: StateFlow
- **本地存储**: SharedPreferences
- **依赖注入**: 手动 DI（无第三方库）

## 功能特性

✅ 用户登录
✅ 用户注册
✅ 登录状态持久化
✅ 自动登录
✅ 用户登出
✅ 表单验证
✅ 错误处理
✅ 加载状态

## 使用说明

### 1. 配置 API 地址

在 `ApiClient.kt` 中修改 `BASE_URL`:

```kotlin
private const val BASE_URL = "https://your-api.com/"
```

### 2. API 接口要求

**登录接口**: POST `/auth/login`
```json
// Request
{
  "email": "user@example.com",
  "password": "password123"
}

// Response
{
  "id": "user_id",
  "email": "user@example.com",
  "token": "jwt_token"
}
```

**注册接口**: POST `/auth/register`
```json
// Request
{
  "email": "user@example.com",
  "password": "password123",
  "name": "User Name"
}

// Response
{
  "id": "user_id",
  "email": "user@example.com",
  "token": "jwt_token"
}
```

## 架构优势

1. **关注点分离**: 每层职责清晰
2. **可测试性**: 各层独立，易于单元测试
3. **可维护性**: 代码结构清晰，易于维护
4. **可扩展性**: 易于添加新功能
5. **无第三方 DI**: 减少依赖，降低复杂度

## 依赖注入流程

```
MainActivity 
    ↓
Dependencies.init(context)
    ↓
PreferencesManager → AuthRepositoryImpl → UseCase → ViewModel
```

## 数据流向

```
UI (Compose) 
    ↓ 用户操作
ViewModel 
    ↓ 调用
UseCase (业务逻辑验证)
    ↓ 调用
Repository (数据获取)
    ↓ 调用
API / Local Storage
```
