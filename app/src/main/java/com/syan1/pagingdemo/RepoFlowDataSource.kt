package com.syan1.pagingdemo

import android.util.Log
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.syan1.pagingdemo.base.LoadState

class RepoFlowDataSource(private val repoViewModel: RepoViewModel) :
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
        repoViewModel.loadPagedDataWithFlow(repoViewModel.INITIAL_PAGE_INDEX) {
            Log.i(TAG, "loadInitial: load Success")
            loadState.postValue(LoadState.Success(true))
            callback.onResult(it, previousIndex, repoViewModel.INITIAL_PAGE_INDEX + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RepoBean>) {
        Log.i(TAG, "loadBefore: ${params.requestedLoadSize} -- currentPage: ${params.key}")
        loadState.postValue(LoadState.Loading(false))
        if (params.key > 1) {
            repoViewModel.loadPagedDataWithFlow(params.key) {
                Log.i(TAG, "loadBefore: load Success")
                loadState.postValue(LoadState.Success(false))
                callback.onResult(it, params.key - 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RepoBean>) {
        Log.i(TAG, "loadAfter: ${params.requestedLoadSize} -- currentPage: ${params.key}")
        loadState.postValue(LoadState.Loading(false))
        repoViewModel.loadPagedDataWithFlow(params.key) {
            Log.i(TAG, "loadAfter: load Success")
            loadState.postValue(LoadState.Success(false))
            callback.onResult(it, params.key + 1)
        }
    }

    override fun invalidate() {
        super.invalidate()
        Log.i(TAG, "invalidate -- ")
    }
}

class RepoFlowDataSourceFactory(private val repoViewModel: RepoViewModel) :
    DataSource.Factory<Int, RepoBean>() {
    override fun create(): DataSource<Int, RepoBean> {
        val dataSource = RepoFlowDataSource(repoViewModel)
        repoViewModel.refreshCallback = {
            dataSource.invalidate()
        }
        dataSource.addInvalidatedCallback {
            Log.i(TAG, "create --  onInvalidated()")
        }
        return dataSource
    }

}