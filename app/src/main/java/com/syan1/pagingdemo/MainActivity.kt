package com.syan1.pagingdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.syan1.pagingdemo.base.BaseActivity
import com.syan1.pagingdemo.base.LoadState
import com.syan1.pagingdemo.base.loadPagedData
import com.syan1.pagingdemo.base.refresh
import com.uber.autodispose.autoDispose
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "paging3---"

class MainActivity : BaseActivity() {

    private val viewModel = RepoViewModel(this, this.lifecycleScope)
    private val adapter = RepoListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

//        viewModel.repoLiveData.observe(this, Observer(adapter::submitList))

//        viewModel.loadData("android")
//        viewModel.repoRxData.autoDispose(this).subscribe({ adapter.submitList(it) }, {})

        adapter.onItemClick = {
            Toast.makeText(this, "position: $it", Toast.LENGTH_SHORT).show()
        }

        swipeRefreshLayout.setOnRefreshListener {
//            viewModel.refresh()
            adapter.refresh()
        }

        viewModel.loadState.observe(this) {
            executeLoadState(it)
        }

        executeWithRx()
    }

    private fun executeLoadState(loadState: LoadState) {
        Log.i(TAG, "onCreate: loadState: $loadState -- ${adapter.currentList?.size}")

        if (loadState is LoadState.Failed) {
            Log.e(TAG, "load error: ${loadState.throwable.toString()}")
        } else {
            swipeRefreshLayout.isRefreshing =
                loadState is LoadState.Loading && loadState.isFirstPage
        }
    }

    /**
     * {@link PagedListAdapterExt }
     */
    private fun executeWithAdapterExt() {
        adapter.loadPagedData(
            loadOnePageDataCallback = {
                getSearchObservable("vue", it)
            },
            scopeProvider = this,
            lifecycleOwner = this,
            loadStateObserver = {
                executeLoadState(it)
            }
        )
    }

    private fun executeWithRx() {
        viewModel.repoRxData.autoDispose(this)
            .subscribe({
                adapter.submitList(it)
            }, {

            })
    }

    private fun executeWithLiveData() {
        viewModel.repoLiveData.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun getSearchObservable(
        queryString: String,
        pageIndex: Int
    ): Observable<Pair<List<RepoBean>, Int>> {
        return ApiService.getUserService()
            .searchReposObservable("${queryString}in:name,description", pageIndex, 10)
            .map { it.items to 25 }
    }

}
