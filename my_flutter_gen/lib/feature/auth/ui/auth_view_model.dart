import 'package:flutter/foundation.dart';
import 'package:my_flutter_gen/core/network/api_result.dart';
import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';
import 'package:my_flutter_gen/feature/auth/domain/repository/auth_repository.dart';

/// 跨页面共享的认证状态（全局 ChangeNotifier）
class AuthViewModel extends ChangeNotifier {
  final AuthRepository _repository;

  AuthViewModel({required AuthRepository repository})
      : _repository = repository;

  User? _currentUser;
  User? get currentUser => _currentUser;

  bool _isLoggedIn = false;
  bool get isLoggedIn => _isLoggedIn;

  bool _isLoading = true;
  bool get isLoading => _isLoading;

  /// 应用启动时调用，检查登录状态
  Future<void> init() async {
    _isLoading = true;
    notifyListeners();

    _currentUser = await _repository.getCachedUser();
    _isLoggedIn = _currentUser?.token != null;

    _isLoading = false;
    notifyListeners();
  }

  /// 登录成功后更新全局状态
  void onLoginSuccess(User user) {
    _currentUser = user;
    _isLoggedIn = true;
    notifyListeners();
  }

  /// 登出
  Future<void> logout() async {
    final result = await _repository.logout();
    result.when(
      success: (_) {
        _currentUser = null;
        _isLoggedIn = false;
        notifyListeners();
      },
      failure: (error) {
        // 即使远程登出失败，也清除本地状态
        _currentUser = null;
        _isLoggedIn = false;
        notifyListeners();
      },
    );
  }
}

