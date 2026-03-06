import 'package:flutter/foundation.dart';
import 'package:my_flutter_gen/core/network/api_result.dart';
import 'package:my_flutter_gen/feature/auth/domain/usecase/login_usecase.dart';
import 'package:my_flutter_gen/feature/auth/ui/login/login_ui_state.dart';

/// 登录页 ViewModel（ChangeNotifier）
class LoginViewModel extends ChangeNotifier {
  final LoginUseCase _loginUseCase;

  LoginViewModel({required LoginUseCase loginUseCase})
      : _loginUseCase = loginUseCase;

  LoginUiState _state = LoginUiState.initial;
  LoginUiState get state => _state;

  /// 执行登录
  Future<void> login({
    required String email,
    required String password,
  }) async {
    _state = _state.copyWith(isLoading: true, errorMessage: null);
    notifyListeners();

    final result = await _loginUseCase(email: email, password: password);

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

