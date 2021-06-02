package com.shivam.kotlin.moengage.api

import com.google.gson.Gson
import com.shivam.kotlin.moengage.data.NewsResponse
import java.net.HttpURLConnection
import java.net.URL


//SingleTon Class for NEW FETCHING
object NewsApiService {

    //KOTLIN EXTENSION FUNCTION
    fun getAllNews(): NewsResponse {
        val connection = URL(NEWS_API).openConnection() as HttpURLConnection // In Build Web Api Class
        try {
            val data = connection.inputStream.bufferedReader().use { it.readText() }
            return Gson().fromJson(data, NewsResponse::class.java)
        } finally {
            connection.disconnect()
        }
    }

    const val NEWS_API =
            "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

}