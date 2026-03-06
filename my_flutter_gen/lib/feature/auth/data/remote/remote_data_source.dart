import 'package:my_flutter_gen/feature/auth/data/remote/auth_api.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/model/login_request.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/model/login_response.dart';

/// 远程数据源 —— 封装对 AuthApi 的调用
class RemoteDataSource {
  final AuthApi _authApi;

  const RemoteDataSource(this._authApi);

  /// 登录
  Future<LoginResponse> login({
    required String email,
    required String password,
  }) {
    return _authApi.login(LoginRequest(email: email, password: password));
  }

  /// 注册
  Future<LoginResponse> register({
    required String username,
    required String email,
    required String password,
  }) {
    return _authApi.register({
      'username': username,
      'email': email,
      'password': password,
    });
  }

  /// 登出
  Future<void> logout() => _authApi.logout();

  /// 获取当前用户
  Future<LoginResponse> getCurrentUser() => _authApi.getCurrentUser();
}

