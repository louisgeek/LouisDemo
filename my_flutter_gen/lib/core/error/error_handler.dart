import 'dart:io';
import 'package:dio/dio.dart';
import 'package:my_flutter_gen/core/error/api_error.dart';
import 'package:my_flutter_gen/core/network/api_result.dart';
/// ErrorHandler - maps DioException and other exceptions to ApiError
class ErrorHandler {
  const ErrorHandler._();
  /// Convert any exception to ApiError
  static ApiError handle(dynamic error) {
    if (error is DioException) {
      return _fromDioException(error);
    }
    if (error is FormatException) {
      return const ApiError(
        type: ApiErrorType.parsing,
        message: 'Data parsing failed',
      );
    }
    if (error is SocketException) {
      return const ApiError(
        type: ApiErrorType.network,
        message: 'Network connection failed',
      );
    }
    return ApiError(
      type: ApiErrorType.unknown,
      message: error.toString(),
      originalError: error,
    );
  }
  /// Safely execute async action, auto-catch and return ApiResult
  static Future<ApiResult<T>> guardAsync<T>(
    Future<T> Function() action,
  ) async {
    try {
      final data = await action();
      return ApiSuccess(data);
    } catch (e) {
      return ApiFailure(handle(e));
    }
  }
  static ApiError _fromDioException(DioException error) {
    switch (error.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return ApiError(
          type: ApiErrorType.timeout,
          message: 'Request timeout, please try again',
          originalError: error,
        );
      case DioExceptionType.connectionError:
        return ApiError(
          type: ApiErrorType.network,
          message: 'Network connection failed',
          originalError: error,
        );
      case DioExceptionType.badResponse:
        return _fromStatusCode(
          error.response?.statusCode,
          error.response?.data,
          error,
        );
      case DioExceptionType.cancel:
        return ApiError(
          type: ApiErrorType.unknown,
          message: 'Request cancelled',
          originalError: error,
        );
      default:
        return ApiError(
          type: ApiErrorType.unknown,
          message: error.message ?? 'Unknown network error',
          originalError: error,
        );
    }
  }
  static ApiError _fromStatusCode(
    int? statusCode,
    dynamic data,
    DioException error,
  ) {
    final msg = data is Map ? (data['message'] ?? '') as String : '';
    if (statusCode == 401) {
      return ApiError(
        type: ApiErrorType.unauthorized,
        message: msg.isNotEmpty ? msg : 'Unauthorized, please login again',
        statusCode: statusCode,
        originalError: error,
      );
    }
    if (statusCode == 403) {
      return ApiError(
        type: ApiErrorType.forbidden,
        message: msg.isNotEmpty ? msg : 'Forbidden',
        statusCode: statusCode,
        originalError: error,
      );
    }
    if (statusCode == 404) {
      return ApiError(
        type: ApiErrorType.notFound,
        message: msg.isNotEmpty ? msg : 'Resource not found',
        statusCode: statusCode,
        originalError: error,
      );
    }
    if (statusCode != null && statusCode >= 500) {
      return ApiError(
        type: ApiErrorType.server,
        message: msg.isNotEmpty ? msg : 'Server error, please try later',
        statusCode: statusCode,
        originalError: error,
      );
    }
    return ApiError(
      type: ApiErrorType.client,
      message: msg.isNotEmpty ? msg : 'Request failed ($statusCode)',
      statusCode: statusCode,
      originalError: error,
    );
  }
}
