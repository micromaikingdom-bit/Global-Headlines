package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.NewsDatabase
import com.example.data.NewsRepository
import com.example.ui.NewsApp
import com.example.ui.NewsViewModel
import com.example.ui.NewsViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            NewsDatabase::class.java, "news-database"
        ).fallbackToDestructiveMigration().build()
        
        val repository = NewsRepository(db.newsDao())
        
        enableEdgeToEdge()
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDarkTheme) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val viewModel: NewsViewModel = viewModel(
                    factory = NewsViewModelFactory(repository)
                )
                NewsApp(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}
