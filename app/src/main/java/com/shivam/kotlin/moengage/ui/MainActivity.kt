package com.shivam.kotlin.moengage.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.shivam.kotlin.moengage.R
import com.shivam.kotlin.moengage.adapter.Page
import com.shivam.kotlin.moengage.adapter.ViewPagerAdapter
import com.shivam.kotlin.moengage.databinding.ActivityMainBinding
import com.shivam.kotlin.moengage.modal.Article
import com.shivam.kotlin.moengage.util.ViewPagerTransformer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.setPageTransformer(ViewPagerTransformer())
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(1, false)

    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem != 1) {
            binding.viewPager.currentItem = 1
        } else{
            super.onBackPressed()
        }
    }


    //Change Page
    fun moveToPage(page:Page){
        when (page) {
            Page.FragmentBookMark -> binding.viewPager.currentItem = 0
            Page.FragmentNews -> binding.viewPager.currentItem = 1
        }
    }

    //Share News
    fun shareNews(article: Article){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND

        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Share News")
        sendIntent.putExtra(Intent.EXTRA_TEXT,article.url)

        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent,"Share News"))
    }

    //Open News Intent
    fun openNews(article: Article){
        val uri = Uri.parse(article.url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}