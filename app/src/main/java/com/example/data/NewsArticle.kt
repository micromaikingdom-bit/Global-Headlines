package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val fullText: String,
    val timestamp: Long,
    val url: String = ""
)
