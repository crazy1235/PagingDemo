package com.syan1.pagingdemo;

import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

class ExamplePagingSource extends RxPagingSource<Integer, RepoBean> {
    private String mQuery;
    private RepoService repoService;

    public ExamplePagingSource(RepoService repoService) {
        this.repoService = repoService;
    }

    @Override
    public Single<PagingSource.LoadResult<Integer, RepoBean>> loadSingle(
            PagingSource.LoadParams<Integer> params) {
        // Start refresh at page 1 if undefined.
        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = 1;
        }

        return repoService.searchReposSingle("${queryString}in:name,description", nextPageNumber, params.getLoadSize())
                .subscribeOn(Schedulers.io())
                .map(repoSearchResult -> toLoadResult())
                .onErrorReturn(PagingSource.LoadResult.Error::new);
    }

    private LoadResult<Integer, RepoBean> toLoadResult() {
        return new LoadResult.Page<>(
                null,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Override
    public Integer getRefreshKey(PagingState<Integer, RepoBean> state) {
        return null;
    }
}
