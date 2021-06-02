package com.shivam.kotlin.moengage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shivam.kotlin.moengage.ui.FragmentBookmark
import com.shivam.kotlin.moengage.ui.FragmentNews


//ViewPager
private const val MAX_PAGE = 2
enum class Page{
    FragmentNews,
    FragmentBookMark
}
class ViewPagerAdapter(fa:FragmentActivity) :FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return MAX_PAGE
    }

    override fun createFragment(position: Int): Fragment {
        return when {
            position == 0 -> FragmentBookmark()
            position == 1 -> FragmentNews()
            else -> FragmentNews()
        }
    }
}