package com.syan1.pagingdemo.base

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.autoDispose
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class PagingDataSource<Value>(
    private val loadDataObservable: ((pageIndex: Int) -> Observable<Pair<List<Value>, Int>>),
    private val scopeProvider: ScopeProvider,
    private val loadState: MutableLiveData<LoadState>?,
    private val firstPageIndex: Int,
    private val pageSize: Int
) :
    PageKeyedDataSource<Int, Value>() {

    private var totalCount = 0

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Value>
    ) {
        loadState?.postValue(LoadState.Loading(true))
        loadDataObservable.invoke(firstPageIndex)
            .subscribeOn(Schedulers.io())
            .autoDispose(scopeProvider)
            .subscribe({
                loadState?.postValue(LoadState.Success(true))
                totalCount = it.second
                callback.onResult(
                    it.first,
                    if (firstPageIndex == 1) null else firstPageIndex - 1,
                    firstPageIndex + 1
                )
            }, {
                loadState?.postValue(LoadState.Failed(true, it))
            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Value>) {
        loadState?.postValue(LoadState.Loading(false))
        loadDataObservable.invoke(params.key)
            .subscribeOn(Schedulers.io())
            .autoDispose(scopeProvider)
            .subscribe({
                loadState?.postValue(LoadState.Success(false))
                callback.onResult(it.first, params.key - 1)
            }, {
                loadState?.postValue(LoadState.Failed(false, it))
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Value>) {
        if ((params.key - 1) * pageSize >= totalCount) return
        loadState?.postValue(LoadState.Loading(false))
        loadDataObservable.invoke(params.key)
            .subscribeOn(Schedulers.io())
            .autoDispose(scopeProvider)
            .subscribe({
                callback.onResult(it.first, params.key + 1)
                loadState?.postValue(LoadState.Success(false))
            }, {
                loadState?.postValue(LoadState.Failed(false, it))
            })
    }

}
