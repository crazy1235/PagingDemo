package com.syan1.pagingdemo.base

sealed class LoadState {
    class Loading(val isFirstPage: Boolean = true) : LoadState()
    class Success(val isFirstPage: Boolean = true) : LoadState()
    class Failed(val isFirstPage: Boolean = true, val throwable: Throwable? = null) : LoadState()

    override fun toString(): String {
        return when (this) {
            is Loading -> {
                this::class.java.simpleName + " -> [isFirstPage: ${this.isFirstPage}]"
            }
            is Success -> {
                this::class.java.simpleName + " -> [isFirstPage: ${this.isFirstPage}]"
            }
            is Failed -> {
                this::class.java.simpleName + " -> [isFirstPage: ${this.isFirstPage}\n -> ${throwable}]"
            }
        }
    }
}