# API 接口示例

## 基础配置

**Base URL**: `https://api.example.com/`

修改位置：`util/Constants.kt` 中的 `BASE_URL`

## 接口列表

### 1. 登录接口

**Endpoint**: `POST /auth/login`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Success Response** (200):
```json
{
  "id": "user_123",
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response** (401):
```json
{
  "error": "Invalid credentials"
}
```

---

### 2. 注册接口

**Endpoint**: `POST /auth/register`

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123"
}
```

**Success Response** (200):
```json
{
  "id": "user_123",
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response** (409):
```json
{
  "error": "User already exists"
}
```

---

## 错误码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 认证失败 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突（如用户已存在）|
| 500 | 服务器错误 |

## 测试建议

1. 使用 Postman 或类似工具测试 API
2. 可以使用 MockAPI.io 或 JSONPlaceholder 创建测试接口
3. 本地测试可以使用 JSON Server
