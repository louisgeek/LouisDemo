# data 数据层
- local
- remote
- repository 

# Repository 实现
- 封装所有数据源（本地数据库、网络请求、文件存储等），是数据的 "统一出入口"，负责协调本地数据源（数据库、SP 等）和远程数据源（API）
- 提供不可变数据给上层，避免外部直接操作数据源
- 提供统一的接口供上层调用，隐藏底层实现细节

Data 层可以依赖 Domain 层
Repository 接口需要下沉到 domain