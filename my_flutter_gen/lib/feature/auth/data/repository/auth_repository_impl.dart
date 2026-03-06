import 'package:my_flutter_gen/core/error/error_handler.dart';
import 'package:my_flutter_gen/core/network/api_client.dart';
import 'package:my_flutter_gen/core/network/api_result.dart';
import 'package:my_flutter_gen/feature/auth/data/local/local_data_source.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/model/login_response.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/remote_data_source.dart';
import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';
import 'package:my_flutter_gen/feature/auth/domain/repository/auth_repository.dart';

/// AuthRepository 的具体实现
class AuthRepositoryImpl implements AuthRepository {
  final RemoteDataSource _remoteDataSource;
  final LocalDataSource _localDataSource;

  const AuthRepositoryImpl({
    required RemoteDataSource remoteDataSource,
    required LocalDataSource localDataSource,
  })  : _remoteDataSource = remoteDataSource,
        _localDataSource = localDataSource;

  @override
  Future<ApiResult<User>> login({
    required String email,
    required String password,
  }) {
    return ErrorHandler.guardAsync(() async {
      final response = await _remoteDataSource.login(
        email: email,
        password: password,
      );
      final user = _mapResponseToUser(response);

      // 缓存用户 & 设置 Token
      await _localDataSource.cacheUser(user);
      ApiClient.instance.setToken(response.token);

      return user;
    });
  }

  @override
  Future<ApiResult<User>> register({
    required String username,
    required String email,
    required String password,
  }) {
    return ErrorHandler.guardAsync(() async {
      final response = await _remoteDataSource.register(
        username: username,
        email: email,
        password: password,
      );
      final user = _mapResponseToUser(response);

      await _localDataSource.cacheUser(user);
      ApiClient.instance.setToken(response.token);

      return user;
    });
  }

  @override
  Future<ApiResult<void>> logout() {
    return ErrorHandler.guardAsync(() async {
      await _remoteDataSource.logout();
      await _localDataSource.clearCache();
      ApiClient.instance.clearToken();
    });
  }

  @override
  Future<User?> getCachedUser() {
    return _localDataSource.getCachedUser();
  }

  @override
  Future<bool> isLoggedIn() async {
    final user = await _localDataSource.getCachedUser();
    return user?.token != null;
  }

  /// DTO → Domain Model 映射
  User _mapResponseToUser(LoginResponse response) {
    return User(
      id: response.id,
      username: response.username,
      email: response.email,
      avatarUrl: response.avatarUrl,
      token: response.token,
    );
  }
}

