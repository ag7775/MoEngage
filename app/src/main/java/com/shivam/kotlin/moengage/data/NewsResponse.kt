package com.shivam.kotlin.moengage.data

import com.shivam.kotlin.moengage.modal.Article

//Response for NewsAPI
data class NewsResponse(
    val status : String,
    val articles: List<Article>
)