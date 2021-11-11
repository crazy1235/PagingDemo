package com.syan1.pagingdemo

import com.google.gson.annotations.SerializedName

data class RepoBean(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("full_name") val fullName: String,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("html_url") val url: String,
    @field:SerializedName("stargazers_count") val stars: Int,
    @field:SerializedName("forks_count") val forks: Int,
    @field:SerializedName("language") val language: String?
)

data class RepoSearchResult(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<RepoBean> = emptyList(),
)