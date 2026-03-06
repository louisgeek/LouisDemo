import 'package:flutter/foundation.dart';
import 'package:my_flutter_gen/core/network/api_result.dart';
import 'package:my_flutter_gen/feature/auth/domain/repository/auth_repository.dart';
import 'package:my_flutter_gen/feature/auth/ui/register/register_ui_state.dart';

/// 注册页 ViewModel（ChangeNotifier）
class RegisterViewModel extends ChangeNotifier {
  final AuthRepository _repository;

  RegisterViewModel({required AuthRepository repository})
      : _repository = repository;

  RegisterState _state = RegisterState.initial;
  RegisterState get state => _state;

  /// 执行注册
  Future<void> register({
    required String username,
    required String email,
    required String password,
  }) async {
    _state = _state.copyWith(isLoading: true, errorMessage: null);
    notifyListeners();

    final result = await _repository.register(
      username: username,
      email: email,
      password: password,
    );

    _state = result.when(
      success: (user) => _state.copyWith(isLoading: false, user: user),
      failure: (error) =>
          _state.copyWith(isLoading: false, errorMessage: error.message),
    );
    notifyListeners();
  }

  /// 清除错误信息
  void clearError() {
    _state = _state.copyWith(errorMessage: null);
    notifyListeners();
  }
}

