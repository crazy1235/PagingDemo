package com.syan1.pagingdemo

import androidx.paging.PagingSource
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitLast

abstract class RxPagingSourceV2<Key : Any, Value : Any> : PagingSource<Key, Value>() {

    abstract fun loadSingle(params: LoadParams<Key>): Observable<LoadResult<Key, Value>>

    final override suspend fun load(params: LoadParams<Key>): LoadResult<Key, Value> {
        return loadSingle(params).awaitLast()
    }
}