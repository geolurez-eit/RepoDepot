package com.agjk.repodepot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.GitRepo

class RepoViewModel : ViewModel() {

    fun getStoredRepos(): LiveData<List<GitRepo.GitRepoItem>> = DepotRepository.getRepos()
    fun getNewRepos(userName: String) {
        DepotRepository.saveNewRepos(userName)
    }

    fun getNewCommits(userName: String, repoName: String) {
        DepotRepository.saveNewCommits(userName, repoName)
    }

}