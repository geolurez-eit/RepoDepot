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

/**
 * Serves primarily as the entry point for activities and fragments to interact and work with data
 * provided by GitHub and Firebase via the DepotRepository.
 */
class RepoViewModel : ViewModel() {

    val isMainLoaded: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isMainLoaded.postValue(false)
    }

    ////////////////////////
    // Repository functions
    ////////////////////////

    /**
     * Retrieves the stored list of repositories for a given user from Firebase Database,
     * calling GitHub for an update if it has been more than 24 hours since the last call.
     * @param username Username of the user whose repositories are being retrieved
     * @param token GitHub Oauth token to be used in Authorization of API calls
     * @return List of repositories as LiveData
     */
    fun getStoredReposForUser(
        username: String,
        token: String
    ): LiveData<List<GitRepo.GitRepoItem>> {
        return if (username != FirebaseAuth.getInstance().currentUser?.displayName)
            DepotRepository.getReposForUser(username)
        else
            DepotRepository.getReposForUserPrivate(username, token)
    }

    ////////////////////////
    // Commits functions
    ////////////////////////

    /**
     * Retrieves the list of stored commits for a user's repository from Firebase Database,
     * calling GitHub for an update if it has been more than 24 hours since the last call.
     * @param token GitHub Oauth token to be used in Authorization of API calls
     * @param username Username of the repository owner
     * @param repoName Name of the repository
     * @return List of commits as LiveData
     */
    fun getStoredCommitsForUser(
        token: String,
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        DepotRepository.getCommitsForUser(token, username, repoName)

    ////////////////////////
    // Userlist functions
    ////////////////////////

    /**
     * Retrieves the stored list of users from Firebase for the current user.
     * @param userName User whose userlist is being retrieved
     * @return List of users as LiveData
     */
    fun getUserList(userName: String): LiveData<List<GitUser>> =
        DepotRepository.getUserList(userName)

    /**
     * Adds a GitHub user to follow in the current user's userlist on Firebase Database
     * @param userName Username to add
     */
    fun addUserToList(userName: String) {
        DepotRepository.addUserToList(userName)
    }

    /**
     * Removes GitHub user from the current user's userlist in Firebase Database
     * @param userName Username to remove
     */
    fun removeUserFromList(userName: String) {
        DepotRepository.removeUserFromList(userName)
    }

    ////////////////////////
    // Profile functions
    ////////////////////////

    /**
     * Retrieves the public profile data from GitHub for a given user.
     * @param userName Username of account to pull data for
     * @return GitUser data object as LiveData
     */
    fun getProfile(userName: String): LiveData<GitUser> = DepotRepository.getUserProfile(userName)

    ////////////////////////
    // Search functions
    ////////////////////////
    /**
     * Retrieves user search results from GitHub
     * @param stringSearch Username to search for
     */
    fun searchUsers(stringSearch: String) = DepotRepository.searchForUsers(stringSearch)


    ////////////////////////
    // Preferences functions
    ////////////////////////
    /**
     * Stores a user's Preferences object into Firebase Database
     * @param preferences Preferences to store
     */
    fun addUserPreferences(preferences: Preferences) {
        DepotRepository.addUserPreferences(preferences)
    }

    /**
     * Retrieves a users Preferences from Firebase Database
     * @return Preferences as LiveData
     */
    fun getUserPreferences(): LiveData<Preferences> = DepotRepository.getUserPreferences()
}