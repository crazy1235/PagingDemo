package com.syan1.pagingdemo

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RepoService {

    @GET("search/repositories?sort=stars")
    fun searchReposSingle(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") itemsPerPage: Int = 10
    ): Single<RepoSearchResult>

    @GET("search/repositories?sort=stars")
    fun searchReposObservable(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") itemsPerPage: Int = 10
    ): Observable<RepoSearchResult>

    @GET("search/repositories?sort=stars")
    suspend fun searchReposSuspend(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepoSearchResult
}