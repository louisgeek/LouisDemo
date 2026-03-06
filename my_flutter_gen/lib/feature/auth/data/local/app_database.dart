import 'dart:io';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:my_flutter_gen/feature/auth/data/local/entity/user_entity.dart';
import 'package:my_flutter_gen/feature/auth/data/local/dao/user_dao.dart';

part 'app_database.g.dart';

/// Drift 数据库定义
@DriftDatabase(tables: [UserEntities], daos: [UserDao])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 1;

  /// 单例
  static AppDatabase? _instance;
  static AppDatabase get instance => _instance ??= AppDatabase();
}

LazyDatabase _openConnection() {
  return LazyDatabase(() async {
    final dbFolder = await getApplicationDocumentsDirectory();
    final file = File(p.join(dbFolder.path, 'my_flutter_gen.sqlite'));
    return NativeDatabase.createInBackground(file);
  });
}

