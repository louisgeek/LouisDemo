import 'package:my_flutter_gen/core/network/api_result.dart';
import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';

/// 认证仓库接口（Domain 层定义，Data 层实现）
abstract class AuthRepository {
  /// 登录
  Future<ApiResult<User>> login({
    required String email,
    required String password,
  });

  /// 注册
  Future<ApiResult<User>> register({
    required String username,
    required String email,
    required String password,
  });

  /// 登出
  Future<ApiResult<void>> logout();

  /// 获取当前缓存的用户信息
  Future<User?> getCachedUser();

  /// 检查是否已登录
  Future<bool> isLoggedIn();
}

