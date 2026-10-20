package com.example.ch09.data

import com.example.ch09.data.local.ArticleDao
import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.mapper.toEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.model.OfflineInfo
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed>
    suspend fun searchNews(query: String): Result<NewsFeed>
    // Untuk layar detail (Tugas 2): artikel apa pun yang pernah dimuat
    suspend fun getArticle(url: String): Article?
}

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: () -> Long = System::currentTimeMillis
) : NewsRepository {

    override suspend fun getTopHeadlines(
        category: NewsCategory
    ): Result<NewsFeed> {
        val cacheKey = category.apiValue

        return fetch(
            cacheKey = cacheKey,
            remote = {
                api.getTopHeadlines(category = category.apiValue)
            },
            cached = {
                dao.getArticles(cacheKey)
            }
        )
    }

    override suspend fun searchNews(
        query: String
    ): Result<NewsFeed> {
        return fetch(
            cacheKey = "__search__",
            remote = {
                api.searchNews(query = query)
            },
            cached = {
                dao.searchCached(query)
                    .distinctBy { it.url }
            }
        )
    }

    override suspend fun getArticle(
        url: String
    ): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }

    private suspend fun fetch(
        cacheKey: String,
        remote: suspend () -> NewsResponseDto,
        cached: suspend () -> List<com.example.ch09.data.local.ArticleEntity>
    ): Result<NewsFeed> {

        try {
            val articles = remote().articles
                .mapNotNull { it.toDomain() }
                .distinctBy { it.url }

            val cachedAt = clock()

            val entities = articles.mapIndexed { index, article ->
                article.toEntity(
                    category = cacheKey,
                    position = index,
                    cachedAt = cachedAt
                )
            }

            dao.replaceCategory(cacheKey, entities)

            return Result.success(
                NewsFeed(articles = articles)
            )

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            val fallback = try {
                cached()
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (cacheError: Exception) {
                return Result.failure(e)
            }

            if (fallback.isEmpty()) {
                return Result.failure(e)
            }

            return Result.success(
                NewsFeed(
                    articles = fallback.map { it.toDomain() },
                    offline = OfflineInfo(
                        cachedAt = fallback.minOf { it.cachedAt },
                        reason = e.toUserMessage()
                    )
                )
            )
        }
    }
}
