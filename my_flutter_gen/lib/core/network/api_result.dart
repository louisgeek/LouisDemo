import 'package:my_flutter_gen/core/error/api_error.dart';

/// 统一的 API 结果封装，使用 sealed class
sealed class ApiResult<T> {
  const ApiResult();
}

/// 成功
final class ApiSuccess<T> extends ApiResult<T> {
  final T data;
  const ApiSuccess(this.data);
}

/// 失败
final class ApiFailure<T> extends ApiResult<T> {
  final ApiError error;
  const ApiFailure(this.error);
}

/// 扩展方法，方便使用
extension ApiResultX<T> on ApiResult<T> {
  /// 模式匹配
  R when<R>({
    required R Function(T data) success,
    required R Function(ApiError error) failure,
  }) {
    return switch (this) {
      ApiSuccess<T>(data: final d) => success(d),
      ApiFailure<T>(error: final e) => failure(e),
    };
  }

  /// 是否成功
  bool get isSuccess => this is ApiSuccess<T>;

  /// 获取数据（可能为 null）
  T? get dataOrNull => switch (this) {
    ApiSuccess<T>(data: final d) => d,
    _ => null,
  };

  /// 获取错误（可能为 null）
  ApiError? get errorOrNull => switch (this) {
    ApiFailure<T>(error: final e) => e,
    _ => null,
  };
}

