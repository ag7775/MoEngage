package com.shivam.kotlin.moengage.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shivam.kotlin.moengage.modal.Article
import com.shivam.kotlin.moengage.modal.RecentNews
import dagger.hilt.android.qualifiers.ApplicationContext

//Database
@Database(entities = [Article::class,RecentNews::class],version = 1,exportSchema = false)
abstract class NewsDatabase : RoomDatabase(){

    abstract fun getArticlesDao() : ArticleDao

    companion object{

        @Volatile
        private var INSTANCE : NewsDatabase? = null

        fun getInstance(@ApplicationContext context: Context): NewsDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context,
                        NewsDatabase::class.java,
                        "news_db"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}