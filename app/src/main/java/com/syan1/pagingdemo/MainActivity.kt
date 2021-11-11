package com.syan1.pagingdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

const val TAG = "paging3---"

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = RepoViewModel()

        var pagingData: Flow<PagingData<RepoBean>>? = null
        var pagingRxData : Observable<PagingData<RepoBean>>? = null

        val adapter = RepoListAdapter()
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.addLoadStateListener { state ->
            val currentStates = state.source
            Log.i(TAG, "addLoadStateListener: $state")
            swipeRefreshLayout.isRefreshing = state.refresh is LoadState.Loading
//            // 如果append没有处于加载状态，但是refreshLayout出于加载状态，refreshLayout停止加载状态
//            if (state.append is LoadState.NotLoading && swipeRefreshLayout.isRefreshing) {
//                swipeRefreshLayout.isRefreshing = false
//            }
//            // 如果refresh没有出于加载状态，但是refreshLayout出于刷新状态，refreshLayout停止刷新
//            if (state.source.refresh is LoadState.NotLoading && swipeRefreshLayout.isRefreshing) {
//                swipeRefreshLayout.isRefreshing = false
//            }
        }

//        pagingData = viewModel.searchRepos("Android")
        pagingRxData = viewModel.searchReposUseRx("Android")

        lifecycleScope.launch {

            pagingRxData.subscribe {
                GlobalScope.launch (Dispatchers.IO) {
                    adapter.submitData(it)
                }
            }

            pagingData?.collectLatest {
                recyclerView.scrollToPosition(0)
                adapter.submitData(it)
            }
        }


        swipeRefreshLayout.setOnRefreshListener {
            pagingData = viewModel.searchRepos("iOS")
            adapter.refresh()
        }

    }
}