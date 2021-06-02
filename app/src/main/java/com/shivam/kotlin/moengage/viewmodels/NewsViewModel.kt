package com.shivam.kotlin.moengage.viewmodels

import androidx.lifecycle.*
import com.shivam.kotlin.moengage.util.PreferenceManager
import com.shivam.kotlin.moengage.modal.Article
import com.shivam.kotlin.moengage.data.NewsRepository
import com.shivam.kotlin.moengage.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {


    val preferenceFlow = preferenceManager.preferenceFlow
    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    var pendingScrollToTopAfterRefresh = false

    init {
        
        //Deleting the cached item if it is older than 7 days
        viewModelScope.launch {
            newsRepository.deleteNonBookmarkedArticlesOlderThan(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            )
        }
    }

    //Fetching the latest news when data[refresh,sortOrder] changes
    //Working on network thread
    val recentNews = combine(refreshTrigger, preferenceFlow) { refresh, sortOrder ->
        Pair(
            refresh,
            sortOrder
        )
    }.flatMapLatest { (refresh, so) ->
        newsRepository.getRecentNews(
            sortOrder = so,
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                pendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Lazily, null)

    //Fetch when the application launches
    fun onStart() {
        if (recentNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    //On Swipe to Refresh
    fun onManualRefresh() {
        if (recentNews.value !is Resource.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
        }
    }

    private val _bookMarkMsg = MutableLiveData<String>()
    val bookMarkMsg: LiveData<String>
        get() = _bookMarkMsg

    //Update bookmark flag of Article
    fun onBookmarkClick(article: Article) {
        val currentlyBookmarked = article.isBookMarked
        val updatedArticle = article.copy(isBookMarked = !currentlyBookmarked)
        viewModelScope.launch {
            newsRepository.updateArticle(updatedArticle)
        }

        _bookMarkMsg.value =
            if (updatedArticle.isBookMarked) "Bookmark Added" else "Removed Bookmark"
    }

    //Update the Sort Order and Save it in Pref
    fun updateSortOrder() = viewModelScope.launch {
        val sortOrder = preferenceFlow.first()
        preferenceManager.updateSortOrder(!sortOrder)
        _bookMarkMsg.value = if (!sortOrder) "Showing Oldest first" else "Showing Latest first"
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}