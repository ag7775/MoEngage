package com.shivam.kotlin.moengage.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shivam.kotlin.moengage.modal.Article
import com.shivam.kotlin.moengage.modal.RecentNews
import kotlinx.coroutines.flow.Flow

//Document Object Model for ROOM DATABASE
@Dao
interface ArticleDao {

    fun getAllRecentNewsBySortOrder(sortOrder: Boolean) : Flow<List<Article>>{
        return when (sortOrder) {
            //Latest first
            false -> getAllRecentNewsArticlesLatestFirst()
            //oldest first
            true -> getAllRecentNewsArticleOldestFirst()
        }
    }

    @Query("SELECT * FROM recent_news INNER JOIN articles ON articleUrl = url ORDER BY publishedLong DESC")
    fun getAllRecentNewsArticlesLatestFirst(): Flow<List<Article>>

    @Query("SELECT * FROM recent_news INNER JOIN articles ON articleUrl = url ORDER BY publishedLong")
    fun getAllRecentNewsArticleOldestFirst(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1")
    fun getAllBookmarkedArticles(): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentNews(breakingNews: List<RecentNews>)

    @Update
    suspend fun updateArticle(article: Article)

    @Query("UPDATE articles SET isBookmarked = 0")
    suspend fun resetAllBookmarks()

    @Query("DELETE FROM recent_news")
    suspend fun deleteAllRecentNews()

    @Query("DELETE FROM articles WHERE updatedAt < :timestampInMillis AND isBookmarked = 0")
    suspend fun deleteNonBookmarkedArticlesOlderThan(timestampInMillis: Long)
}