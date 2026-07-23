package com.example.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class GNewsResponse(
    val totalArticles: Int,
    val articles: List<GNewsArticle>
)

@Serializable
data class GNewsArticle(
    val title: String,
    val description: String,
    val content: String,
    val url: String,
    val publishedAt: String
)

interface NewsApiService {
    @GET("api/v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("apikey") apiKey: String,
        @Query("category") category: String = "general",
        @Query("lang") lang: String = "zh",
        @Query("country") country: String = "cn",
        @Query("max") max: Int = 5
    ): GNewsResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://gnews.io/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: NewsApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(NewsApiService::class.java)
    }
}
