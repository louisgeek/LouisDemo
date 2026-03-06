import 'package:json_annotation/json_annotation.dart';

part 'login_response.g.dart';

/// 登录响应 DTO
@JsonSerializable()
class LoginResponse {
  final int id;
  final String username;
  final String email;
  @JsonKey(name: 'avatar_url')
  final String? avatarUrl;
  final String token;

  const LoginResponse({
    required this.id,
    required this.username,
    required this.email,
    this.avatarUrl,
    required this.token,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) =>
      _$LoginResponseFromJson(json);

  Map<String, dynamic> toJson() => _$LoginResponseToJson(this);
}

