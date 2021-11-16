package com.syan1.pagingdemo

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.syan1.pagingdemo.base.LoadState
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class RepoViewModel(
    private val scopeProvider: ScopeProvider,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) : ViewModel() {

    private val PER_PAGE_SIZE = 10
    private val PRE_DISTANCE = 3

    // 第一页页码
    val FIRST_PAGE_INDEX = 1

    // 初始加载第几页
    val INITIAL_PAGE_INDEX = 1

    val loadState = MutableLiveData<LoadState>()

    private val repoResult = ApiService.getUserService()

    private var searchString: String = "flutter"

    var refreshCallback: (() -> Unit)? = null

    /**
     * for DataSource
     */
    fun loadPagedDataWithRx(
        pageIndex: Int = 1
    ): ObservableSubscribeProxy<List<RepoBean>> {
        return repoResult.searchReposObservable(
            "${searchString}in:name,description",
            pageIndex,
            PER_PAGE_SIZE
        )
            .map { it.items }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(scopeProvider)
    }

    /**
     * for DataSource
     */
    fun loadPagedDataWithFlow(pageIndex: Int = 1, callback: (List<RepoBean>) -> Unit) {
        lifecycleCoroutineScope.launch {
            val result = repoResult.searchReposSuspend(
                "${searchString}in:name,description",
                pageIndex,
                PER_PAGE_SIZE
            ).items
            callback.invoke(result)
        }
    }

    val repoLiveData = LivePagedListBuilder(
        RepoFlowDataSourceFactory(this),
        PagedList.Config.Builder().setPageSize(PER_PAGE_SIZE).setEnablePlaceholders(false).build()
    ).build()

    val repoLiveData2 = RepoFlowDataSourceFactory(this).toLiveData(
        PagedList.Config.Builder()
            .setPageSize(PER_PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()
    )

    val repoRxData2 = RepoRxDataSourceFactory(this).toObservable(
        PagedList.Config.Builder()
            .setPageSize(PER_PAGE_SIZE)
            .setPrefetchDistance(PRE_DISTANCE)
            .setInitialLoadSizeHint(PER_PAGE_SIZE)
            .build()
    )

    val repoRxData = RxPagedListBuilder(
        RepoRxDataSourceFactory(this),
        PagedList.Config.Builder()
            .setPageSize(PER_PAGE_SIZE)
            .setPrefetchDistance(PRE_DISTANCE)
            .setInitialLoadSizeHint(PER_PAGE_SIZE)
//            .setMaxSize(PER_PAGE_SIZE * 3)
//            .setEnablePlaceholders(true)
            .build()
    ).setBoundaryCallback(object : PagedList.BoundaryCallback<RepoBean>() {
        override fun onZeroItemsLoaded() {
            super.onZeroItemsLoaded()
            Log.i(TAG, "onZeroItemsLoaded")
        }

        override fun onItemAtFrontLoaded(itemAtFront: RepoBean) {
            super.onItemAtFrontLoaded(itemAtFront)
            Log.i(TAG, "onItemAtFrontLoaded: $itemAtFront")
        }

        override fun onItemAtEndLoaded(itemAtEnd: RepoBean) {
            super.onItemAtEndLoaded(itemAtEnd)
            Log.i(TAG, "onItemAtEndLoaded: $itemAtEnd")
        }
    }).buildObservable()

    fun loadData(searchString: String) {
        this.searchString = searchString
    }

    fun refresh() {
        refreshCallback?.invoke()
    }
}