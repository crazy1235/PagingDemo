package com.syan1.pagingdemo.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.RecyclerView
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDispose
import io.reactivex.Observable

const val DEFAULT_PAGE_SIZE = 10
const val DEFAULT_PRE_FETCH_SIZE = 3
const val DEFAULT_FIRST_PAGE_INDEX = 1

fun <Value, VH : RecyclerView.ViewHolder> PagedListAdapter<Value, VH>.loadPagedData(
    pageSize: Int = DEFAULT_PAGE_SIZE,
    preFetch: Int = DEFAULT_PRE_FETCH_SIZE,
    scopeProvider: ScopeProvider,
    loadOnePageDataCallback: ((pageIndex: Int) -> Observable<Pair<List<Value>, Int>>),
    lifecycleOwner: LifecycleOwner? = null,
    loadStateObserver: Observer<LoadState>? = null
) {

    var loadState: MutableLiveData<LoadState>? = null

    if (lifecycleOwner != null && loadStateObserver != null) {
        loadState = MutableLiveData<LoadState>()
        loadState.observe(lifecycleOwner, loadStateObserver)
    }

    val pagedObservable = RxPagedListBuilder(
        object : DataSource.Factory<Int, Value>() {
            override fun create(): DataSource<Int, Value> {
                return PagingDataSource(
                    loadOnePageDataCallback,
                    scopeProvider,
                    loadState,
                    DEFAULT_FIRST_PAGE_INDEX,
                    pageSize
                )
            }
        },
        PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setPrefetchDistance(preFetch)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
    ).buildObservable()

    pagedObservable.autoDispose(scopeProvider)
        .subscribe({
            submitList(it)
        }, { loadState?.postValue(LoadState.Failed(false, it)) })
}

fun <Value, VH : RecyclerView.ViewHolder> PagedListAdapter<Value, VH>.refresh() {
    currentList?.dataSource?.invalidate()
}