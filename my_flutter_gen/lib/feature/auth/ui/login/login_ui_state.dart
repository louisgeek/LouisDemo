import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';

/// 登录页面 UI 状态
class LoginUiState {
  final bool isLoading;
  final String? errorMessage;
  final User? user;

  const LoginUiState({
    this.isLoading = false,
    this.errorMessage,
    this.user,
  });

  /// 初始状态
  static const initial = LoginUiState();

  LoginUiState copyWith({
    bool? isLoading,
    String? errorMessage,
    User? user,
  }) {
    return LoginUiState(
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage,
      user: user ?? this.user,
    );
  }

  /// 是否登录成功
  bool get isSuccess => user != null && !isLoading && errorMessage == null;
}

