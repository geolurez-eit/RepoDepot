package com.agjk.repodepot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits

class RepoViewModel : ViewModel() {

    /* getting data from Firebase */
    // public only - common use
    fun getStoredReposForUser(username: String): LiveData<List<GitRepo.GitRepoItem>> =
        DepotRepository.getReposForUser(username)

    // private and public - for signed in user
    fun getStoredPrivateReposForUser(username: String): LiveData<List<GitRepo.GitRepoItem>> =
        DepotRepository.getReposForUser(username+"_private")

    fun getStoredCommitsForUser(
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        DepotRepository.getCommitsForUser(username, repoName)
    /* getting data from Firebase */

    /* getting new data */
    fun getNewRepos(userName: String) {
        DepotRepository.saveNewRepos(userName)
    }
    fun getNewPrivateRepos(userName: String, token:String) {
        DepotRepository.saveNewPrivateRepos(userName,token)
    }

    fun getNewCommits(userName: String, repoName: String) {
        DepotRepository.saveNewCommits(userName, repoName)
    }
    /* getting new data */
}