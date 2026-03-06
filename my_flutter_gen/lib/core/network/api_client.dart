import 'package:dio/dio.dart';

/// 全局 Dio 单例，供 Retrofit 接口使用
class ApiClient {
  ApiClient._();

  static final ApiClient _instance = ApiClient._();
  static ApiClient get instance => _instance;

  late final Dio dio;

  /// 初始化，在 main() 中调用
  void init({
    required String baseUrl,
    Duration connectTimeout = const Duration(seconds: 15),
    Duration receiveTimeout = const Duration(seconds: 15),
  }) {
    dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: connectTimeout,
        receiveTimeout: receiveTimeout,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // ── 拦截器 ──────────────────────────────────────────
    dio.interceptors.addAll([
      _logInterceptor(),
      // 可在此添加 Token 拦截器、重试拦截器等
    ]);
  }

  /// 设置 Authorization Token
  void setToken(String token) {
    dio.options.headers['Authorization'] = 'Bearer $token';
  }

  /// 清除 Token
  void clearToken() {
    dio.options.headers.remove('Authorization');
  }

  InterceptorsWrapper _logInterceptor() {
    return InterceptorsWrapper(
      onRequest: (options, handler) {
        print('┌── REQUEST ──────────────────────────────');
        print('│ ${options.method} ${options.uri}');
        print('│ Headers: ${options.headers}');
        if (options.data != null) print('│ Body: ${options.data}');
        print('└─────────────────────────────────────────');
        handler.next(options);
      },
      onResponse: (response, handler) {
        print('┌── RESPONSE ─────────────────────────────');
        print('│ ${response.statusCode} ${response.requestOptions.uri}');
        print('│ Data: ${response.data}');
        print('└─────────────────────────────────────────');
        handler.next(response);
      },
      onError: (error, handler) {
        print('┌── ERROR ────────────────────────────────');
        print('│ ${error.type} ${error.message}');
        print('└─────────────────────────────────────────');
        handler.next(error);
      },
    );
  }
}

