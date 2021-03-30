package com.android.mvvmretrofitjava.db;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.mvvmretrofitjava.model.Article;

import java.util.List;

@Dao
public interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Article article);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles();

    @Delete
    void deleteArticle(Article article);
}
