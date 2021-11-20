package com.syan1.pagingdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val TAG = "paging3---"

class MainActivity : AppCompatActivity() {

    private val viewModel = RepoViewModel()
    private val adapter = RepoListAdapter()
    private var pagingData: Flow<PagingData<RepoBean>>? = null
    private var pagingRxData: Observable<PagingData<RepoBean>>? = null
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter.withLoadStateFooter(LoadStateAdapter {
            // todo retry
            adapter.retry()
        })

        adapter.addLoadStateListener { loadState ->
            Log.i(TAG, "addLoadStateListener: $loadState")
            if (loadState.refresh is LoadState.Loading) {
                swipeRefreshLayout.isRefreshing = true
            } else {
                swipeRefreshLayout.isRefreshing = false
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    Toast.makeText(this, it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        executeWithLiveData()

        swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

    }

    private fun executeWithFlow() {
        lifecycleScope.launch {
            viewModel.searchRepos("iOS")
                .map { pagingData ->
                    // 这里可以做一些数据过滤等操作
//                    pagingData.map {
//                        it.id
//                    }
                    pagingData
                }.collectLatest {
                    recyclerView.scrollToPosition(0)
                    adapter.submitData(it)
                }
        }
    }

    private fun executeWithRxJava() {
        disposable = viewModel.searchReposUseRx("flutter")
//            .map { pagingData ->
//                pagingData.map {
//                    it.url
//                }
//            }
            .subscribeOn(Schedulers.io())
            .subscribe({
                adapter.submitData(lifecycle = this.lifecycle, it)
            }, {

            })
    }

    private fun executeWithLiveData() {
        viewModel.searchReposUseLiveData("compose")
            .observe(this) {
                adapter.submitData(this.lifecycle, it)
            }
    }

}