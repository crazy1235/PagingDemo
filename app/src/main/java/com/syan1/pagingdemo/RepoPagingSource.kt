package com.syan1.pagingdemo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.observable
import io.reactivex.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

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
            // 从数据库拉去数据
//            val shoes = repoService.searchRepos("in:name,description", pos, params.loadSize)
//            val disposable = shoes.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe({
//                    LoadResult.Page(
//                        data = it.items,
//                        prevKey = if (pos == start_index) null else pos - 1,
//                        nextKey = if (it.items.isNullOrEmpty()) null else pos + (params.loadSize / 10)
//                    )
//                }, {
//
//                })

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

@ExperimentalCoroutinesApi
class RepoViewModel : ViewModel() {

    private val repoResult = ApiService.getUserService()

    fun searchRepos(queryString: String, pageSize: Int = 5): Flow<PagingData<RepoBean>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize
            ),
            pagingSourceFactory = {
                RepoRxPagingSource(queryString, repoResult, pageSize)
//                RepoPagingSource(queryString, repoResult, pageSize)
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun searchReposUseRx(queryString: String, pageSize: Int = 5): Observable<PagingData<RepoBean>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize
            ),
            pagingSourceFactory = {
//                RepoRxPagingSourceV2(queryString, repoResult, pageSize)
                RepoRxPagingSource(queryString, repoResult, pageSize)
//                RepoPagingSource(queryString, repoResult, pageSize)
            }
        ).observable.cachedIn(viewModelScope)
    }
}