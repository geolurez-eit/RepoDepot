package com.agjk.repodepot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.*

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

    fun getUserList(userName:String): LiveData<List<GitUser>> =
        DepotRepository.getUserList(userName)

    fun addUserToList(userName: String) {
        DepotRepository.addUserToList(userName)
    }

    fun getProfile(userName: String):GitUser = DepotRepository.getUserProfile(userName)

    fun addUserPreferences(preferences: Preferences) {
        DepotRepository.addUserPreferences(preferences)
    }

    fun getUserPreferences(): LiveData<Preferences> = DepotRepository.getUserPreferences()
}