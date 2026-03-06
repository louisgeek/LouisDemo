import 'package:drift/drift.dart';
import 'package:my_flutter_gen/feature/auth/data/local/app_database.dart';
import 'package:my_flutter_gen/feature/auth/data/local/entity/user_entity.dart';

part 'user_dao.g.dart';

/// Drift DAO —— 用户数据访问对象
@DriftAccessor(tables: [UserEntities])
class UserDao extends DatabaseAccessor<AppDatabase> with _$UserDaoMixin {
  UserDao(super.db);

  /// 插入或更新用户
  Future<void> upsertUser(UserEntitiesCompanion user) {
    return into(userEntities).insertOnConflictUpdate(user);
  }

  /// 获取缓存的用户
  Future<UserEntity?> getCachedUser() {
    return (select(userEntities)..limit(1)).getSingleOrNull();
  }

  /// 删除所有用户（登出时调用）
  Future<int> deleteAllUsers() {
    return delete(userEntities).go();
  }
}

