import 'package:my_flutter_gen/core/network/api_result.dart';
import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';
import 'package:my_flutter_gen/feature/auth/domain/repository/auth_repository.dart';

/// 登录用例 —— 封装登录的业务逻辑
class LoginUseCase {
  final AuthRepository _repository;

  const LoginUseCase(this._repository);

  /// 执行登录
  Future<ApiResult<User>> call({
    required String email,
    required String password,
  }) {
    return _repository.login(email: email, password: password);
  }
}

