package com.louis.lg_archj.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.louis.lg_archj.data.local.entity.NewsEntity;

import java.util.List;

@Dao
public interface NewsDao {
    @Query("SELECT * FROM news")
    List<NewsEntity> getAllNews();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NewsEntity> news);

    @Query("DELETE FROM news")
    void deleteAll();
}