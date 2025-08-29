# DAG Fragment Navigation Example

## 导航图结构
```
Home ──┬─→ Login ──┬─→ Register ──→ Login
       │           └─→ PrivacyPolicy ──→ Login
       ├─→ More
       └─→ DeviceAdd ←──→ DeviceManager

Settings ──┬─→ Login
           ├─→ Logout ──→ Login  
           └─→ DeviceManager
```

## 实际使用场景

### 场景1: 用户注册流程
```java
// 用户路径: Home -> Login -> Register -> Login
navigationManager.navigateTo("Login");    // ✓ 允许
navigationManager.navigateTo("Register"); // ✓ 允许  
navigationManager.navigateTo("Login");    // ✓ 允许
```

### 场景2: 设备管理流程
```java
// 用户路径: Home -> DeviceAdd -> DeviceManager -> DeviceAdd
navigationManager.navigateTo("DeviceAdd");     // ✓ 允许
navigationManager.navigateTo("DeviceManager"); // ✓ 允许
navigationManager.navigateTo("DeviceAdd");     // ✓ 允许 (双向边)
```

### 场景3: 被阻止的非法跳转
```java
// More页面尝试跳转到Login
navigationManager.navigateTo("Login"); // ✗ 被阻止 (DAG中无此边)

// Register尝试跳转到DeviceAdd  
navigationManager.navigateTo("DeviceAdd"); // ✗ 被阻止 (DAG中无此边)
```

## Fragment Replace优势

1. **内存效率**: 同时只有一个Fragment实例
2. **状态清晰**: 每次replace都是全新状态
3. **导航安全**: DAG确保无循环依赖
4. **返回可控**: 基于栈的返回机制

## 拓扑排序结果
```
Level 0: [Home, Settings]           // 入度为0的起始页面
Level 1: [Login, More, DeviceAdd, Logout, DeviceManager]  
Level 2: [Register, PrivacyPolicy]  // 最深层页面
```