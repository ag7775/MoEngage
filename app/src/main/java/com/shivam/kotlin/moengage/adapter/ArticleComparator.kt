package com.shivam.kotlin.moengage.adapter

import androidx.recyclerview.widget.DiffUtil
import com.shivam.kotlin.moengage.modal.Article


//Comparator for RecyclerView
class ArticleComparator : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article) =
        oldItem.url == newItem.url

    override fun areContentsTheSame(oldItem: Article, newItem: Article) =
       oldItem == newItem
}