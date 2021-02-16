package com.agjk.repodepot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.model.data.UserSearch
import com.agjk.repodepot.model.data.Preferences

class RepoViewModel : ViewModel() {

    /* getting data from Firebase */
    // public only - common use
    fun getStoredReposForUser(username: String): LiveData<List<GitRepo.GitRepoItem>> =
        DepotRepository.getReposForUser(username)

    // private and public - for signed in user
    fun getStoredPrivateReposForUser(
        username: String,
        token: String
    ): LiveData<List<GitRepo.GitRepoItem>> =
        DepotRepository.getReposForUserPrivate(username, token)

    fun getStoredCommitsForUser(
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        DepotRepository.getCommitsForUser(username, repoName)

    fun getUserList(): LiveData<List<String>> =
        DepotRepository.getUserList()

    fun addUserToList(userName: String) {
        DepotRepository.addUserToList(userName)
    }

    fun searchUsers(stringSearch: String) = DepotRepository.searchForUsers(stringSearch)

    fun addUserPreferences(preferences: Preferences) {
        DepotRepository.addUserPreferences(preferences)
    }

    fun getUserPreferences(): LiveData<Preferences> = DepotRepository.getUserPreferences()
}