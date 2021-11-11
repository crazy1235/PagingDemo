package com.syan1.pagingdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.observable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

class RepoViewModel : ViewModel() {

    private val DEFAULT_PAGE_SIZE = 10
    private val DEFAULT_PREFETCH_SIZE = 3

    private val repoResult = ApiService.getUserService()

    private fun getPagingConfig(pageSize: Int): PagingConfig {
        return PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false,
            initialLoadSize = pageSize
        )
    }


    /**
     * flow
     */
    fun searchRepos(queryString: String, pageSize: Int = 5): Flow<PagingData<RepoBean>> {
        return Pager(
            config = getPagingConfig(pageSize),
            pagingSourceFactory = {
                RepoPagingSource(queryString, repoResult, pageSize)
            }
        ).flow.cachedIn(viewModelScope)
    }

    /**
     * Rx
     */
    fun searchReposUseRx(queryString: String, pageSize: Int = 5): Observable<PagingData<RepoBean>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = DEFAULT_PREFETCH_SIZE,
                enablePlaceholders = false,
                initialLoadSize = pageSize
            ),
            pagingSourceFactory = {
//                RepoRxPagingSourceV2(queryString, repoResult, pageSize)
                RepoRxPagingSource(queryString, repoResult, pageSize)
            }
        ).observable.cachedIn(viewModelScope)
    }

    /**
     * LiveData
     */
    fun searchReposUseLiveData(
        queryString: String,
        pageSize: Int = 5
    ): LiveData<PagingData<RepoBean>> {
        return Pager(config = getPagingConfig(pageSize), pagingSourceFactory = {
            RepoLiveDataPagingSource(queryString, repoResult, pageSize)
        }).liveData.cachedIn(viewModelScope)
    }
}