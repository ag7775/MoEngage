package com.shivam.kotlin.moengage.di

import android.content.Context
import com.shivam.kotlin.moengage.data.ArticleDao
import com.shivam.kotlin.moengage.data.NewsDatabase
import com.shivam.kotlin.moengage.api.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//Dependency Injection Using HILT
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNewDatabase(@ApplicationContext context: Context): NewsDatabase {
        return NewsDatabase.getInstance(context = context)
    }

    @Provides
    fun provideArticlesDao(db: NewsDatabase): ArticleDao {
        return db.getArticlesDao()
    }
    
}