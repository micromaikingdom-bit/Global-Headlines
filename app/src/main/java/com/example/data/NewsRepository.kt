package com.example.data

import com.example.BuildConfig
import com.example.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class NewsRepository(private val newsDao: NewsDao) {
    val allNews: Flow<List<NewsArticle>> = newsDao.getAllNews()

    suspend fun refreshNews(force: Boolean = false): Boolean {
        return withContext(Dispatchers.IO) {
            val count = newsDao.getCount()
            if (force || count == 0) {
                try {
                    val apiKey = BuildConfig.GNEWS_API_KEY
                    if (apiKey.isEmpty() || apiKey == "YOUR_GNEWS_API_KEY") {
                        if (count == 0 || force) {
                            if (force) newsDao.clearAll()
                            insertMockData()
                        }
                        return@withContext false
                    }
                    
                    val response = RetrofitClient.service.getTopHeadlines(apiKey = apiKey, max = 5)
                    
                    if (response.articles.isNotEmpty()) {
                        val currentNews = newsDao.getAllNewsSync()
                        val currentTitles = currentNews.map { it.title }.toSet()
                        val newTitles = response.articles.map { it.title }.toSet()
                        
                        if (currentTitles.isNotEmpty() && currentTitles.containsAll(newTitles)) {
                            return@withContext false
                        }

                        val newsList = response.articles.map { article ->
                            NewsArticle(
                                id = UUID.randomUUID().toString(),
                                title = article.title,
                                summary = article.description ?: "",
                                fullText = article.content ?: "",
                                timestamp = System.currentTimeMillis(),
                                url = article.url
                            )
                        }
                        // Clear old news and insert new
                        if (force) {
                            newsDao.clearAll()
                        }
                        newsDao.insertAll(newsList)
                        return@withContext true
                    } else if (count == 0 || force) {
                        if (force) newsDao.clearAll()
                        insertMockData()
                        return@withContext false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback to mock data on error if no data exists or forced
                    if (count == 0 || force) {
                        if (force) newsDao.clearAll()
                        insertMockData()
                    }
                    return@withContext false
                }
            }
            return@withContext false
        }
    }

    private suspend fun insertMockData() {
        val mockNews = listOf(
            NewsArticle(
                id = "1",
                title = "全球气候峰会达成历史性协议",
                summary = "195个国家的领导人签署了一项具有里程碑意义的条约，以加速向可再生能源的过渡。",
                fullText = "在一次历史性的转折中，为期两周的全球气候峰会今天在巴黎闭幕，195个参会国家达成一致。新条约要求大幅减少碳排放，并建立一个数十亿美元的基金，以支持发展中国家向太阳能、风能和地热能过渡。分析人士预测，这将在未来十年重塑全球经济版图。",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                url = "https://example.com"
            ),
            NewsArticle(
                id = "2",
                title = "空间望远镜首次捕捉到系外行星大气层影像",
                summary = "新型轨道天文台对40光年外的一颗类地行星进行了史无前例的化学分析。",
                fullText = "天文学家今天宣布了一项重大突破，下一代空间望远镜成功捕捉并分析了开普勒-186f（一颗位于其恒星宜居带的类地系外行星）的大气成分。光谱数据表明存在水蒸气和甲烷，这引发了关于外星生命存在的令人兴奋的新问题。进一步的观测已安排在下个月进行。",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                url = "https://example.com"
            ),
            NewsArticle(
                id = "3",
                title = "量子纠错技术取得重大突破",
                summary = "研究人员成功将稳定的量子态维持了创纪录的时长，为实用量子计算机铺平了道路。",
                fullText = "一个由国际物理学家组成的联盟解决了量子计算中的最大障碍之一：纠错。通过利用一种新颖的量子比特拓扑排列，该团队能够将量子相干性维持超过10分钟，比以前的记录增加了100万倍。这一发展使我们更接近利用量子计算机进行复杂的药物发现和气候建模。",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
                url = "https://example.com"
            ),
            NewsArticle(
                id = "4",
                title = "数月动荡后全球供应链趋于稳定",
                summary = "随着主要港口清理了长期积压的货物，国际航运费率已恢复到疫情前水平。",
                fullText = "在经历了近两年的前所未有的中断之后，全球供应链显示出强劲的稳定迹象。洛杉矶、上海和鹿特丹等主要港口已成功清理了积压的集装箱船。与去年峰值相比，运输成本暴跌了60%，为面临通胀压力的国际零售商和消费者提供了急需的缓解。",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 12,
                url = "https://example.com"
            ),
            NewsArticle(
                id = "5",
                title = "新型电池技术有望实现1000英里续航",
                summary = "一种新型固态电池原型展示了现有锂离子电池两倍的能量密度。",
                fullText = "一家汽车初创公司推出了一种革命性的固态电池，可能永远改变电动汽车的格局。该原型具有超高的能量密度，一次充电即可实现1000英里的续航里程，同时完全消除了热失控起火的风险。几家主要汽车制造商已经签署了合作协议，预计将在三年内开始量产。",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                url = "https://example.com"
            )
        )
        newsDao.insertAll(mockNews)
    }
}
