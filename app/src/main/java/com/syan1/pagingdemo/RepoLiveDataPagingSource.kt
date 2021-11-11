package com.syan1.pagingdemo

import androidx.paging.ListenableFuturePagingSource
import androidx.paging.PagingState
import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import retrofit2.HttpException
import java.util.concurrent.Executors

class RepoLiveDataPagingSource(
    private val queryString: String,
    private val repoService: RepoService,
    private val pageSize: Int
) :
    ListenableFuturePagingSource<Int, RepoBean>() {

    private val start_index = 1

    private val executor = Executors.newSingleThreadExecutor()

    override fun getRefreshKey(state: PagingState<Int, RepoBean>): Int? {
        return null
    }

    override fun loadFuture(params: LoadParams<Int>): ListenableFuture<LoadResult<Int, RepoBean>> {
        val pos = params.key ?: 1
        val pageFuture = Futures.transform(
            repoService.searchRepoWithFuture(
                queryString,
                pos,
                pageSize
            ),
            object : Function<RepoSearchResult, LoadResult.Page<Int, RepoBean>> {
                override fun apply(input: RepoSearchResult?): LoadResult.Page<Int, RepoBean>? {
                    return toLoadResult(input, pos, params.loadSize)
                }
            },
            executor
        )
        val partialLoadResultFuture: ListenableFuture<LoadResult<Int, RepoBean>> =
            Futures.catching(
                pageFuture,
                HttpException::class.java,
                { input -> input?.let { LoadResult.Error(it) } }, executor
            )

        return Futures.catching(
            partialLoadResultFuture,
            Exception::class.java,
            { input -> input?.let { LoadResult.Error(it) } },
            executor
        )
    }

    private fun toLoadResult(
        input: RepoSearchResult?,
        pos: Int,
        loadSize: Int
    ): LoadResult.Page<Int, RepoBean>? {
        if (input == null) return null
        return LoadResult.Page(
            data = input.items,
            prevKey = if (pos == start_index) null else pos - 1,
            nextKey = if (input.items.isNullOrEmpty()) null else pos + (loadSize / pageSize)
        )
    }
}