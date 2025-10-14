package com.louis.lg_archj.data.local.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;

import com.louis.lg_archj.data.local.dao.NewsDao;
import com.louis.lg_archj.data.local.entity.NewsEntity;

@Database(entities = {NewsEntity.class}, version = 2, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDao newsDao();

    private static volatile NewsDatabase INSTANCE;

    public static NewsDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (NewsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NewsDatabase.class, "news_database")
                            .fallbackToDestructiveMigration() //当数据库版本更新时会自动删除旧数据并重新创建表结构
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}