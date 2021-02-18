package com.agjk.repodepot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.model.data.GitUser
import com.agjk.repodepot.model.data.Preferences
import com.google.firebase.auth.FirebaseAuth

class RepoViewModel : ViewModel() {

    val isMainLoaded: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isMainLoaded.postValue(false)
    }

    /* getting data from Firebase */
    // public only - common use
    fun getStoredReposForUser(username: String, token:String): LiveData<List<GitRepo.GitRepoItem>> {
        return if (username != FirebaseAuth.getInstance().currentUser?.displayName)
            DepotRepository.getReposForUser(username)
        else
            DepotRepository.getReposForUserPrivate(username, token)
    }

    fun getStoredCommitsForUser(
        token:String,
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        DepotRepository.getCommitsForUser(token,username, repoName)

    fun getUserList(userName: String): LiveData<List<GitUser>> =
        DepotRepository.getUserList(userName)

    fun addUserToList(userName: String) {
        DepotRepository.addUserToList(userName)
    }
    fun removeUserFromList(userName: String){
        DepotRepository.removeUserFromList(userName)
    }

    fun getProfile(userName: String): LiveData<GitUser> = DepotRepository.getUserProfile(userName)

    fun searchUsers(stringSearch: String) = DepotRepository.searchForUsers(stringSearch)

    fun addUserPreferences(preferences: Preferences) {
        DepotRepository.addUserPreferences(preferences)
    }

    fun getUserPreferences(): LiveData<Preferences> = DepotRepository.getUserPreferences()
}