package com.syan1.pagingdemo

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiService {

    private const val BASE_URL = "https://api.github.com/"

    private val retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor()).build())
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

    private fun <T> createApi(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

    fun getUserService() = createApi(RepoService::class.java)
}