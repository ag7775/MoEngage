package com.shivam.kotlin.moengage.modal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
        val author: String?,
        val title: String,
        val description: String,
        @PrimaryKey val url: String,
        val urlToImage: String,
        val publishedAt: String,
        @ColumnInfo(name = "content")
        val content: String?,
        val isBookMarked: Boolean,
        val publishedLong: Long,
        val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_news")
data class RecentNews(
        val articleUrl: String,
        @PrimaryKey(autoGenerate = true) val id: Int = 0
)