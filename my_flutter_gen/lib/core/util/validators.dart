/// 通用表单验证工具
class Validators {
  Validators._();

  /// 邮箱验证
  static String? email(String? value) {
    if (value == null || value.isEmpty) return '请输入邮箱';
    final regex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    if (!regex.hasMatch(value)) return '请输入有效的邮箱地址';
    return null;
  }

  /// 密码验证（至少 6 位）
  static String? password(String? value) {
    if (value == null || value.isEmpty) return '请输入密码';
    if (value.length < 6) return '密码至少 6 位';
    return null;
  }

  /// 用户名验证
  static String? username(String? value) {
    if (value == null || value.isEmpty) return '请输入用户名';
    if (value.length < 2) return '用户名至少 2 个字符';
    return null;
  }

  /// 确认密码
  static String? Function(String?) confirmPassword(String password) {
    return (String? value) {
      if (value == null || value.isEmpty) return '请确认密码';
      if (value != password) return '两次密码不一致';
      return null;
    };
  }
}

