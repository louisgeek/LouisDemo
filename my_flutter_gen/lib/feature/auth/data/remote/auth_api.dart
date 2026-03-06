import 'package:dio/dio.dart';
import 'package:retrofit/retrofit.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/model/login_request.dart';
import 'package:my_flutter_gen/feature/auth/data/remote/model/login_response.dart';

part 'auth_api.g.dart';

/// Retrofit 认证 API 接口
@RestApi()
abstract class AuthApi {
  factory AuthApi(Dio dio, {String baseUrl}) = _AuthApi;

  /// 登录
  @POST('/api/auth/login')
  Future<LoginResponse> login(@Body() LoginRequest request);

  /// 注册
  @POST('/api/auth/register')
  Future<LoginResponse> register(@Body() Map<String, dynamic> body);

  /// 登出
  @POST('/api/auth/logout')
  Future<void> logout();

  /// 获取当前用户信息
  @GET('/api/auth/me')
  Future<LoginResponse> getCurrentUser();
}

