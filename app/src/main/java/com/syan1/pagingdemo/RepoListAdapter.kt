package com.syan1.pagingdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.repo_view_item.view.*

class RepoListAdapter : PagingDataAdapter<RepoBean, RepoViewHolder>(diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repo_view_item, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        getItem(position)?.let { holder.bindData(it) }
    }
}

class RepoViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bindData(repoBean: RepoBean) {
        view.repo_name.text = repoBean.fullName
        if (repoBean.description != null) {
            view.repo_description.visibility = View.VISIBLE
            view.repo_description.text = repoBean.description
        } else {
            view.repo_description.visibility = View.GONE
        }
        view.repo_stars.text = repoBean.stars.toString()
        view.repo_forks.text = repoBean.forks.toString()
        if (repoBean.language != null) {
            view.repo_language.visibility = View.VISIBLE
            view.repo_language.text = repoBean.language
        } else {
            view.repo_language.visibility = View.GONE
        }
    }
}

val diff = object : DiffUtil.ItemCallback<RepoBean>() {
    override fun areItemsTheSame(oldItem: RepoBean, newItem: RepoBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RepoBean, newItem: RepoBean): Boolean {
        return oldItem == newItem
    }

}