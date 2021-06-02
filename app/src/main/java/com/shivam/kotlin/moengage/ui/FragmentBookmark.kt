package com.shivam.kotlin.moengage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.shivam.kotlin.moengage.NewsRecyclerAdapter
import com.shivam.kotlin.moengage.R
import com.shivam.kotlin.moengage.adapter.Page
import com.shivam.kotlin.moengage.modal.Article
import com.shivam.kotlin.moengage.viewmodels.BookmarkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FragmentBookmark : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: ImageView
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private lateinit var backButton : ImageView
    private lateinit var deleteButton : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookMarkAdapter = NewsRecyclerAdapter(
            onBookmarkClick = {
                bookmarkViewModel.onBookmarkClick(it)
            },
            onImageClicked = { article ->
                openNews(article)
            }
        )
        recyclerView = view.findViewById(R.id.bookmark_recycler)
        backButton = view.findViewById(R.id.backIcon)
        deleteButton = view.findViewById(R.id.deleteIcon)
        empty = view.findViewById(R.id.no_bookmark)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookMarkAdapter
            itemAnimator?.changeDuration = 0
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            bookmarkViewModel.bookmarks.collect {
                val bookmarks = it ?: return@collect

                bookMarkAdapter.submitList(bookmarks)
                recyclerView.isVisible = bookmarks.isNotEmpty()
                empty.isVisible =  bookmarks.isEmpty()

            }
        }
        bookmarkViewModel.bookMarkMsg.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener{
            onBackButtonPressed()
        }

        //Delete all bookmark
        deleteButton.setOnClickListener{
            bookmarkViewModel.onDeleteAllBookmarks()
        }
    }

    //Back Button
    private fun onBackButtonPressed(){
        if (activity != null && activity is MainActivity){
            (activity as MainActivity).moveToPage(Page.FragmentNews)
        }
    }

    //OPen News
    private fun openNews(article: Article) {
        if (activity != null && activity is MainActivity) {
            (activity as MainActivity).openNews(article)
        }
    }


    fun shareNews(article: Article){
        if (activity != null && activity is MainActivity){
            (activity as MainActivity).shareNews(article)
        }
    }
}