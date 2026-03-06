import 'package:drift/drift.dart';

/// Drift 表定义 —— 用户实体
class UserEntities extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get username => text().withLength(min: 1, max: 50)();
  TextColumn get email => text().withLength(min: 1, max: 100)();
  TextColumn get avatarUrl => text().nullable()();
  TextColumn get token => text().nullable()();
  DateTimeColumn get createdAt =>
      dateTime().withDefault(currentDateAndTime)();
}

