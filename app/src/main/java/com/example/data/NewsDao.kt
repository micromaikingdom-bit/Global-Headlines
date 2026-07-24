package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_articles ORDER BY timestamp DESC")
    fun getAllNews(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles ORDER BY timestamp DESC")
    suspend fun getAllNewsSync(): List<NewsArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<NewsArticle>)
    
    @Query("DELETE FROM news_articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getCount(): Int
}
