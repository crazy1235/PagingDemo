package com.syan1.pagingdemo

import androidx.paging.PagingState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class RepoRxPagingSourceV2(
    private val queryString: String,
    private val repoService: RepoService,
    private val pageSize: Int
) : RxPagingSourceV2<Int, RepoBean>() {

    private val start_index = 1

    override fun getRefreshKey(state: PagingState<Int, RepoBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Observable<LoadResult<Int, RepoBean>> {
        val pos = params.key ?: start_index
        return repoService.searchReposObservable(
            "${queryString}in:name,description",
            pos,
            params.loadSize
        )
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