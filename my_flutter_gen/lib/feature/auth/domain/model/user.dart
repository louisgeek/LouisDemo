/// 业务层用户模型（纯 Dart，不依赖任何框架）
class User {
  final int id;
  final String username;
  final String email;
  final String? avatarUrl;
  final String? token;

  const User({
    required this.id,
    required this.username,
    required this.email,
    this.avatarUrl,
    this.token,
  });

  User copyWith({
    int? id,
    String? username,
    String? email,
    String? avatarUrl,
    String? token,
  }) {
    return User(
      id: id ?? this.id,
      username: username ?? this.username,
      email: email ?? this.email,
      avatarUrl: avatarUrl ?? this.avatarUrl,
      token: token ?? this.token,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is User &&
          runtimeType == other.runtimeType &&
          id == other.id &&
          username == other.username &&
          email == other.email;

  @override
  int get hashCode => id.hashCode ^ username.hashCode ^ email.hashCode;

  @override
  String toString() => 'User(id: $id, username: $username, email: $email)';
}

