package com.agjk.repodepot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object RepoViewModelFactory : ViewModelProvider.Factory {
    var viewModel : RepoViewModel? = RepoViewModel()
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}