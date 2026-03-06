/// API 错误类型枚举
enum ApiErrorType {
  /// 网络不可用
  network,

  /// 请求超时
  timeout,

  /// 服务器错误 (5xx)
  server,

  /// 客户端错误 (4xx)
  client,

  /// 未授权 (401)
  unauthorized,

  /// 禁止访问 (403)
  forbidden,

  /// 资源未找到 (404)
  notFound,

  /// 数据解析错误
  parsing,

  /// 未知错误
  unknown,
}

/// 统一的 API 错误模型
class ApiError implements Exception {
  final ApiErrorType type;
  final String message;
  final int? statusCode;
  final dynamic originalError;

  const ApiError({
    required this.type,
    required this.message,
    this.statusCode,
    this.originalError,
  });

  @override
  String toString() => 'ApiError($type, $statusCode): $message';
}

