package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsArticle::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}
