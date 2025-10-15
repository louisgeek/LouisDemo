# domain 领域层
- 核心业务逻辑（与具体技术实现无关），纯 java 依赖
- model 实体类（纯数据模型，与数据源无关）
- usecase 用例类（封装业务逻辑）

## UseCase
- 目的是成为 ViewModel 和 Repository 之间的中介（桥梁、可选）
- 每个 UseCase 专注一个业务场景，封装可复用的单一业务逻辑（单一职责），不持有状态（ViewModel 负责状态管理 ViewModel 负责切回主线程）
- 调用一个或多个 Repository，对数据做进一步处理（过滤、排序、转换、组合等）
- 调用 Repository 提供的数据方法，并对结果进行加工（如格式转换、错误处理）
- 理论上 UseCase 应该已经是主线程安全的（Repository 已经保证），Main-safe 的，可在主线程中被安全调用
- 不应该依赖任何 Android 平台相关对象，甚至 Context，需要将 Context 相关操作移至 Repository 或 Data Source（或通过接口抽象隐藏 Context 依赖，比如获取字符串供 UseCase 使用）
- 不应该包含 mutable 的数据
- 不应该包含与 UI 或平台无关的代码（如网络请求、数据库操作、数据转换等）
- 最好不要沦为只是一个 Repository 的包装器
  纯业务逻辑，不关心数据来源（本地/远程） Repository 接口需要写在 domain 层，对应实现写在 data 层