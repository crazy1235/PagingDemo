package com.syan1.pagingdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.load_state_view.view.*

class LoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bindData(loadState, retry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.load_state_view, parent, false)
        )
    }

}

class LoadStateViewHolder(private val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bindData(loadState: LoadState, retry: () -> Unit) {
        val progressView = view.load_state_progress
        val retryBtn = view.load_state_retry
        val errorMsgView = view.load_state_errorMessage

        retryBtn.isVisible = loadState !is LoadState.Loading
        errorMsgView.isVisible = loadState !is LoadState.Loading
        progressView.isVisible = loadState is LoadState.Loading

        if (loadState is LoadState.Error) {
            errorMsgView.text = loadState.error.localizedMessage
        }
        retryBtn.setOnClickListener { retry.invoke() }
    }

}
