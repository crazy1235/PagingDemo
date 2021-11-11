package com.syan1.pagingdemo

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

class RepoPagingSource(
    private val queryString: String,
    private val repoService: RepoService,
    private val pageSize: Int
) :
    PagingSource<Int, RepoBean>() {

    private val start_index = 1

    override fun getRefreshKey(state: PagingState<Int, RepoBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepoBean> {
        val pos = params.key ?: start_index
        Log.i(TAG, "load: pos: $pos -- loadSize: ${params.loadSize}")
        return try {
            val repos = repoService.searchReposSuspend(
                "${queryString}in:name,description",
                pos,
                params.loadSize
            )
            val preKey = if (pos == start_index) null else pos - 1
            val nextKey = if (repos.items.isEmpty()) {
                null
            } else {
                pos + (params.loadSize / pageSize)
            }
            Log.i(TAG, "preKey: $preKey -- nextKey: $nextKey")
            LoadResult.Page(
                data = repos.items,
                prevKey = preKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}