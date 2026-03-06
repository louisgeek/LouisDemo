import 'package:drift/drift.dart';
import 'package:my_flutter_gen/feature/auth/data/local/dao/user_dao.dart';
import 'package:my_flutter_gen/feature/auth/data/local/app_database.dart';
import 'package:my_flutter_gen/feature/auth/domain/model/user.dart';

/// 鏈湴鏁版嵁婧?鈥斺€?灏佽瀵?Drift DAO 鐨勮皟鐢?
class LocalDataSource {
  final UserDao _userDao;

  const LocalDataSource(this._userDao);

  /// 缂撳瓨鐢ㄦ埛淇℃伅
  Future<void> cacheUser(User user) {
    return _userDao.upsertUser(
      UserEntitiesCompanion(
        id: Value(user.id),
        username: Value(user.username),
        email: Value(user.email),
        avatarUrl: Value(user.avatarUrl),
        token: Value(user.token),
      ),
    );
  }

  /// 鑾峰彇缂撳瓨鐨勭敤鎴?
  Future<User?> getCachedUser() async {
    final entity = await _userDao.getCachedUser();
    if (entity == null) return null;
    return User(
      id: entity.id,
      username: entity.username,
      email: entity.email,
      avatarUrl: entity.avatarUrl,
      token: entity.token,
    );
  }

  /// 娓呴櫎缂撳瓨
  Future<void> clearCache() => _userDao.deleteAllUsers();
}

