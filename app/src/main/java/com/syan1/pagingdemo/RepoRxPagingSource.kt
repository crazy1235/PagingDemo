package com.syan1.pagingdemo

import android.util.Log
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RepoRxPagingSource(private val queryString: String,
                         private val repoService: RepoService,
                         private val pageSize: Int) : RxPagingSource<Int, RepoBean>() {

    private val start_index = 1

    override fun getRefreshKey(state: PagingState<Int, RepoBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, RepoBean>> {
        val pos = params.key ?: start_index
        Log.i(TAG, "RepoRxPagingSource -- loadSingle: $queryString")
        return repoService.searchReposSingle("${queryString}in:name,description", pos, params.loadSize)
            .subscribeOn(Schedulers.io())
            .map<LoadResult<Int, RepoBean>> {
                LoadResult.Page(
                    data = it.items,
                    prevKey = if (pos == start_index) null else pos - 1,
                    nextKey = if (it.items.isNullOrEmpty()) null else pos + (params.loadSize / pageSize)
                )
            }
            .onErrorReturn { LoadResult.Error(it) }
    }
}