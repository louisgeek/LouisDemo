import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';

/// 注册页面 UI 状态
class RegisterState {
  final bool isLoading;
  final String? errorMessage;
  final User? user;

  const RegisterState({
    this.isLoading = false,
    this.errorMessage,
    this.user,
  });

  /// 初始状态
  static const initial = RegisterState();

  RegisterState copyWith({
    bool? isLoading,
    String? errorMessage,
    User? user,
  }) {
    return RegisterState(
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage,
      user: user ?? this.user,
    );
  }

  /// 是否注册成功
  bool get isSuccess => user != null && !isLoading && errorMessage == null;
}

