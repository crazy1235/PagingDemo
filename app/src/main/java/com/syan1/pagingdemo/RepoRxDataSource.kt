package com.syan1.pagingdemo

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.syan1.pagingdemo.base.LoadState

class RepoRxDataSource(private val repoViewModel: RepoViewModel) :
    PageKeyedDataSource<Int, RepoBean>() {

    private val loadState = repoViewModel.loadState

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, RepoBean>
    ) {
        loadState.postValue(LoadState.Loading(true))
        val previousIndex =
            if (repoViewModel.INITIAL_PAGE_INDEX > repoViewModel.FIRST_PAGE_INDEX) {
                repoViewModel.INITIAL_PAGE_INDEX - repoViewModel.FIRST_PAGE_INDEX
            } else null
        Log.i(
            TAG,
            "loadInitial: ${params.requestedLoadSize} -- currentPage: ${repoViewModel.INITIAL_PAGE_INDEX} -- previousIndex: $previousIndex"
        )
        repoViewModel.loadPagedDataWithRx(repoViewModel.INITIAL_PAGE_INDEX).subscribe({
            Log.i(TAG, "loadInitial: load Success")
            loadState.postValue(LoadState.Success(true))
            callback.onResult(it, previousIndex, repoViewModel.INITIAL_PAGE_INDEX + 1)
//            callback.onResult(
//                it,
//                repoViewModel.INITIAL_PAGE_INDEX,
//                60,
//                previousIndex,
//                repoViewModel.INITIAL_PAGE_INDEX + 1
//            )
        }, {
            loadState.postValue(LoadState.Failed(true, it))
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RepoBean>) {
        Log.i(TAG, "loadBefore: ${params.requestedLoadSize} -- currentPage: ${params.key}")
        loadState.postValue(LoadState.Loading(false))
        if (params.key > 1) {
            repoViewModel.loadPagedDataWithRx(params.key).subscribe({
                Log.i(TAG, "loadBefore: load Success")
                loadState.postValue(LoadState.Success(false))
                callback.onResult(it, params.key - 1)
            }, {
                loadState.postValue(LoadState.Failed(false, it))
            })
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RepoBean>) {
        Log.i(TAG, "loadAfter: ${params.requestedLoadSize} -- currentPage: ${params.key}")
        loadState.postValue(LoadState.Loading(false))
        repoViewModel.loadPagedDataWithRx(params.key).subscribe({
            Log.i(TAG, "loadAfter: load Success")
            loadState.postValue(LoadState.Success(false))
            callback.onResult(it, params.key + 1)
        }, {
            loadState.postValue(LoadState.Failed(false, it))
        })
    }

    override fun invalidate() {
        super.invalidate()
        Log.i(TAG, "invalidate -- ")
    }
}

class RepoRxDataSourceFactory(private val repoViewModel: RepoViewModel) :
    DataSource.Factory<Int, RepoBean>() {
    override fun create(): DataSource<Int, RepoBean> {
        val dataSource = RepoRxDataSource(repoViewModel)
        repoViewModel.refreshCallback = {
            dataSource.invalidate()
        }
        dataSource.addInvalidatedCallback {
            Log.i(TAG, "create --  onInvalidated()")
        }
        return dataSource
    }

}