package com.shivam.kotlin.moengage.data


import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import com.shivam.kotlin.moengage.api.NewsApiService
import com.shivam.kotlin.moengage.formatTo
import com.shivam.kotlin.moengage.modal.Article
import com.shivam.kotlin.moengage.modal.RecentNews
import com.shivam.kotlin.moengage.toDate
import com.shivam.kotlin.moengage.util.Resource
import com.shivam.kotlin.moengage.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//Class Responsible for fetching data
class NewsRepository @Inject constructor(
    private val db: NewsDatabase
) {

    private val newsArticleDao = db.getArticlesDao()

    fun getRecentNews(
        sortOrder: Boolean,
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Article>>> =
        networkBoundResource(
            query = {
                newsArticleDao.getAllRecentNewsBySortOrder(sortOrder)
            },
            fetch = {
                NewsApiService.getAllNews().articles
            },
            saveFetchResult = { serverBreakingNewsArticles ->
                val bookmarkedArticles = newsArticleDao.getAllBookmarkedArticles().first()

                val breakingNewsArticles =
                    serverBreakingNewsArticles.map { serverBreakingNewsArticle ->
                        val isBookmarked = bookmarkedArticles.any { bookmarkedArticle ->
                            bookmarkedArticle.url == serverBreakingNewsArticle.url
                        }

                        val date = serverBreakingNewsArticle.publishedAt.toDate()

                        Article(
                            title = serverBreakingNewsArticle.title,
                            url = serverBreakingNewsArticle.url,
                            urlToImage = serverBreakingNewsArticle.urlToImage,
                            isBookMarked = isBookmarked,
                            content = serverBreakingNewsArticle.content,
                            publishedAt = date.formatTo(),
                            publishedLong = date.time,
                            author = serverBreakingNewsArticle.author,
                            description = serverBreakingNewsArticle.description
                        )
                    }

                val breakingNews = breakingNewsArticles.map { article ->
                    RecentNews(article.url)
                }

                db.withTransaction {
                    newsArticleDao.deleteAllRecentNews()
                    newsArticleDao.insertArticles(breakingNewsArticles)
                    newsArticleDao.insertRecentNews(breakingNews)
                }
            },
            shouldFetch = { cachedArticles ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedArticles = cachedArticles.sortedBy { article ->
                        article.updatedAt
                    }
                    val oldestTimestamp = sortedArticles.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimestamp == null ||
                            oldestTimestamp < System.currentTimeMillis() -
                            TimeUnit.MINUTES.toMillis(60)
                    needsRefresh
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            }
        )

    fun getAllBookmarkedArticles(): Flow<List<Article>> =
        newsArticleDao.getAllBookmarkedArticles()

    suspend fun updateArticle(article: Article) {
        newsArticleDao.updateArticle(article)
    }

    suspend fun resetAllBookmarks() {
        newsArticleDao.resetAllBookmarks()
    }

    suspend fun deleteNonBookmarkedArticlesOlderThan(timestampInMillis: Long) {
        newsArticleDao.deleteNonBookmarkedArticlesOlderThan(timestampInMillis)
    }

}