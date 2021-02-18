@file:Suppress("unused")

package com.agjk.repodepot.model

import android.database.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agjk.repodepot.model.data.*
import com.agjk.repodepot.network.GitRetrofit
import com.agjk.repodepot.util.DebugLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime

/**
 * DepotRepository implements the majority of the logic for handling calls
 * and the data from those calls to GitHub and Firebase. This singleton communicates primarily to
 * Firebase Database and Auth using the current instance and utilizes the Retrofit implmentation
 * to get data fom GitHub.
 */
object DepotRepository {

    // Firebase variables
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val gitRetrofit = GitRetrofit
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    // Lists to aggregate; used to traverse pages in results
    private val resultRepoList: MutableList<GitRepo.GitRepoItem> = mutableListOf()
    private val resultCommitList: MutableList<GitRepoCommits.GitRepoCommitsItem> = mutableListOf()

    // LiveData
    private val repoUserLiveData: MutableLiveData<List<GitRepo.GitRepoItem>> = MutableLiveData()
    private val userListLiveData: MutableLiveData<List<GitUser>> = MutableLiveData()
    private val commitLiveData: MutableLiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        MutableLiveData()
    var prefLiveData: MutableLiveData<Preferences> = MutableLiveData()
    private var userProfile: MutableLiveData<GitUser> = MutableLiveData()
    val userSearchLiveData: MutableLiveData<List<UserSearch.Item>> = MutableLiveData()

    //////////////////////
    // Database Init
    //////////////////////
    init {
        firebaseDatabase.setPersistenceEnabled(true)
        firebaseDatabase.reference.keepSynced(true)
    }

    ///////////////////////
    // Repository Functions
    ///////////////////////
    /**
     * Get a list of user's repositories
     * @param username GitHub username
     * @return List of repositories as LiveData
     */
    fun getReposForUser(username: String): LiveData<List<GitRepo.GitRepoItem>> {
        DebugLogger("DepotRepository.getReposForUser")
        // Check if it has been 24 hours
        firebaseDatabase.reference.child("REPOSITORIES").child(username).child("lastUpdated")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(String::class.java).runCatching {
                        DebugLogger("Last Updated: $this")
                        DebugLogger(
                            "Last Updated + 24: " + LocalDateTime.parse(this).plusDays(1L)
                        )
                        DebugLogger("Now: " + LocalDateTime.now())
                        if (LocalDateTime.parse(this).plusDays(1L) < LocalDateTime.now()) {
                            DebugLogger("It has been 24 hours")
                            //Update repos for user
                            DebugLogger("Updating repos")
                            saveNewRepos(username, 1)
                        } else {
                            DebugLogger("Still has not been 24 hours \nUnable to update repos")
                        }

                    }.getOrElse {
                        //Update repos for user
                        DebugLogger("No timestamp\nUpdating repos")
                        saveNewRepos(username, 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("24 Hour Check on Cancelled: " + error.message)
                }

            })
        //Retrieve stored repos
        return getRepositories(username)
    }

    /**
     * Get a list of a user's repositories, including private ones
     * @param username GitHub username
     * @param token GitHub Oauth token
     * @return List of repositories as LiveData
     */
    fun getReposForUserPrivate(
        username: String,
        token: String
    ): LiveData<List<GitRepo.GitRepoItem>> {
        DebugLogger("DepotRepository.getReposForUser")
        // Check if it has been 24 hours
        firebaseDatabase.reference.child("REPOSITORIES").child(username + "_private")
            .child("lastUpdated")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(String::class.java).runCatching {
                        DebugLogger("Last Updated: $this")
                        DebugLogger(
                            "Last Updated + 24: " + LocalDateTime.parse(this).plusDays(1L)
                        )
                        DebugLogger("Now: " + LocalDateTime.now())
                        if (LocalDateTime.parse(this).plusDays(1L) < LocalDateTime.now()) {
                            DebugLogger("It has been 24 hours")
                            //Update repos for user
                            DebugLogger("Updating repos")
                            saveNewPrivateRepos(username, token, 1)
                        } else {
                            DebugLogger("Still has not been 24 hours \nUnable to update repos")
                        }

                    }.getOrElse {
                        //Update repos for user
                        DebugLogger("No timestamp\nUpdating repos")
                        saveNewPrivateRepos(username, token, 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("24 Hour Check on Cancelled: " + error.message)
                }
            })
        //add user to userlist
        DebugLogger(firebaseAuth.currentUser?.displayName.toString())
        //Retrieve stored repos
        return getRepositories(username + "_private")
    }

    /**
     * Make an API call to GitHub and store the list of repositories
     * into Firebase Database
     * @param userName GitHub username
     * @param page The page of results to retrieve
     */
    private fun saveNewRepos(userName: String, page: Int) {
        DebugLogger("DepotRepository - saveNewRepos")
        DebugLogger("repoDisposable.add")
        val repoDisposable = CompositeDisposable()
        repoDisposable.add(
            gitRetrofit.getUserRepositories(userName, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //DebugLogger(".subscribe - it: $it")
                    if (it.size < 100) {
                        resultRepoList.addAll(it)
                        postRepos(userName, resultRepoList)
                        repoDisposable.clear()
                        resultRepoList.clear()
                    } else {
                        resultRepoList.addAll(it)
                        saveNewRepos(userName, page + 1)
                    }
                }, {
                    DebugLogger("saveNewRepos .subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
    }

    /**
     * Make an API call to GitHub and store the list of repositories into Firebase Database.
     * This is the private repository version of the public method.
     * @param userName GitHub username
     * @param token GitHub Oath token
     * @param page The page of results to retrieve
     */
    private fun saveNewPrivateRepos(userName: String, token: String, page: Int) {
        DebugLogger("DepotRepository - saveNewRepos")
        DebugLogger("compositeDisposable.add")
        val repoDisposable = CompositeDisposable()
        repoDisposable.add(
            gitRetrofit.getUserAllRepositories(token, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.size < 100) {
                        resultRepoList.addAll(it)
                        postRepos(userName + "_private", resultRepoList)
                        repoDisposable.clear()
                        resultRepoList.clear()
                    } else {
                        resultRepoList.addAll(it)
                        saveNewPrivateRepos(userName, token, page + 1)
                        repoDisposable.clear()
                    }
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
    }

    /**
     * Store list of repositories into Firebase Database
     * @param userName GitHub username
     * @param repo List of repositories to store
     */
    private fun postRepos(userName: String, repo: List<GitRepo.GitRepoItem>) {
        DebugLogger("DepotRepository.postRepos")
        firebaseDatabase.reference.child("REPOSITORIES").child(userName)
            .setValue(repo)
        DebugLogger(LocalDateTime.now().toString())
        firebaseDatabase.reference.child("REPOSITORIES").child(userName).child("lastUpdated")
            .setValue(LocalDateTime.now().toString())
        DebugLogger("Repos for :${userName} added!")
    }

    /**
     * Retrieve stored list of repositories from Firebase Database
     * @param username GitHub username
     * @return List of repositories as LiveData
     */
    private fun getRepositories(username: String): MutableLiveData<List<GitRepo.GitRepoItem>> {
        firebaseDatabase.reference.child("REPOSITORIES").child(username)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val repoList = mutableListOf<GitRepo.GitRepoItem>()
                    snapshot.children.forEach {
                        if (it.key != "lastUpdated") {
                            it.getValue(GitRepo.GitRepoItem::class.java)?.let { repo ->
                                if (repo.owner?.login == username || repo.owner?.login + "_private" == username)
                                    repoList.add(repo)
                            }
                        }
                    }
                    repoUserLiveData.value = repoList
                }
            })
        DebugLogger("Returning from getRepositories")
        return repoUserLiveData
    }

    //////////////////////
    // Commit Functions
    //////////////////////
    fun getCommitsForUser(
        token: String,
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> {
        DebugLogger("DepotRepository.getCommitsForUser")
        // Check if it has been 24 hours
        //Update commits for repo
        saveNewCommits(token, username, repoName, 1)

        //Retrieve stored commits
        firebaseDatabase.reference.child("COMMITS").child(username)
            .child(repoName.replace(".", "_"))
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val commitList = mutableListOf<GitRepoCommits.GitRepoCommitsItem>()
                    snapshot.children.forEach {
                        if (it.key != "lastUpdated") {
                            it.getValue(GitRepoCommits.GitRepoCommitsItem::class.java)
                                ?.let { commit ->
                                    commitList.add(commit)
                                }
                        }
                    }
                    commitLiveData.value = commitList
                }
            })
        DebugLogger("Returning from getCommitsForUser")
        return commitLiveData
    }

    /**
     * Make an API call to GitHub and store the lists of commits for a given user's repository
     * @param token GitHub Oauth token
     * @param userName GitHub username
     * @param repoName GitHub repository name
     * @param page Page of results to save
     */
    private fun saveNewCommits(token: String, userName: String, repoName: String, page: Int) {
        DebugLogger("DepotRepository - saveNewCommits")
        DebugLogger("compositeDisposable.add")
        val commitDisposable = CompositeDisposable()
        val repoNameClean = repoName.replace(".", "_")
        DebugLogger("DepotRepository.saveNewCommits repoNameClean: $repoNameClean")
        firebaseDatabase.reference.child("COMMITS").child(userName).child(repoNameClean)
            .child("lastUpdated")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(String::class.java).runCatching {
                        DebugLogger("Last Updated: $this")
                        DebugLogger(
                            "Last Updated + 24: " + LocalDateTime.parse(this).plusDays(1L)
                        )
                        DebugLogger("Now: " + LocalDateTime.now())
                        if (LocalDateTime.parse(this).plusDays(1L) < LocalDateTime.now()) {
                            DebugLogger("It has been 24 hours")
                            commitDisposable.add(
                                gitRetrofit.getRepositoryCommits(token, userName, repoName, page)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it.size < 100) {
                                            resultCommitList.addAll(it)
                                            postCommits(it, userName, repoName)
                                            commitDisposable.clear()
                                        } else {
                                            resultCommitList.addAll(it)
                                            saveNewCommits(token, userName, repoName, page + 1)
                                        }
                                    }, {
                                        DebugLogger("DepotRepository.saveNewCommits $userName $repoName .subscribe Error: " + it.localizedMessage + "\n" + it.stackTrace)
                                    })
                            )
                        } else {
                            DebugLogger("Still has not been 24 hours")
                        }
                    }.getOrElse {
                        DebugLogger("DepotRepository.saveNewCommits Error: " + it.localizedMessage + "\n" + it.stackTrace)
                        commitDisposable.add(
                            gitRetrofit.getRepositoryCommits(token, userName, repoName, page)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ commitList ->
                                    if (commitList.size < 100) {
                                        resultCommitList.addAll(commitList)
                                        postCommits(commitList, userName, repoNameClean)
                                        commitDisposable.clear()
                                    } else {
                                        resultCommitList.addAll(commitList)
                                        saveNewCommits(token, userName, repoName, page + 1)
                                    }
                                }, { subscribeThrowable ->
                                    DebugLogger("DepotRepository.saveNewCommits getOrElse.subscribe Error: " + subscribeThrowable.localizedMessage + "\n" + subscribeThrowable.stackTrace)
                                })
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("24 Hour Check on Cancelled: " + error.message)
                }
            })
    }

    /**
     * Store list of commits to Firebase Database
     * @param commits List of commits to store
     * @param userName GitHub username
     * @param repoName GitHub repository name
     */
    private fun postCommits(
        commits: List<GitRepoCommits.GitRepoCommitsItem>,
        userName: String,
        repoName: String
    ) {
        DebugLogger("DepotRepository.postCommits")
        firebaseDatabase.reference.child("COMMITS").child(userName).child(repoName)
            .setValue(commits)
        firebaseDatabase.reference.child("COMMITS").child(userName).child(repoName)
            .child("lastUpdated")
            .setValue(LocalDateTime.now().toString())
        DebugLogger("Commits for :${repoName} added!")
    }

    //////////////////////
    // Userlist Functions
    //////////////////////
    /**
     * Retrieve a userlist from Firebase Database
     * @param thisUserName The user whose list is being retrieved
     * @return List of users as LiveData
     */
    fun getUserList(thisUserName: String): LiveData<List<GitUser>> {
        firebaseDatabase.reference.child("USERLISTS")
            .child(thisUserName)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val usersList = mutableListOf<GitUser>()
                        snapshot.children.forEach {
                            it.getValue(GitUser::class.java).let { user ->
                                //DebugLogger("getUserList user: " + user.toString())
                                user?.let { it1 -> usersList.add(it1) }
                            }
                        }

                        userListLiveData.value = usersList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        DebugLogger("Error ${error.message}")
                    }
                })
        return userListLiveData
    }

    /**
     * Store GitHub user into a Firebase user's userlist
     * @param userName User to store into the list
     */
    fun addUserToList(userName: String) {
        val thisUserName = firebaseAuth.currentUser?.displayName.toString()
        val userDisposable = CompositeDisposable()
        userDisposable.add(
            gitRetrofit.getUserProfile(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    firebaseDatabase.reference.child("USERLISTS")
                        .child(thisUserName).child(it.login.toString()).setValue(it)
                }, {
                    DebugLogger("Error within addUserToList " + it.message)
                })
        )
    }

    ////////////////////////
    // Preferences Functions
    ////////////////////////
    /**
     * Store preferences for the current user into Firebase Database
     * @param preferences Preferences to store
     */
    fun addUserPreferences(preferences: Preferences) {
        firebaseDatabase.reference.child("USER_PREFERENCES")
            .child(firebaseAuth.currentUser?.displayName.toString())
            .setValue(preferences)
    }

    /**
     * Retrieve stored preferences for the current user from Firebase Database
     * @return Preferences as LiveData
     */
    fun getUserPreferences(): LiveData<Preferences> {
        firebaseDatabase.reference.child("USER_PREFERENCES")
            .child(firebaseAuth.currentUser?.displayName.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    prefLiveData.value = snapshot.getValue(Preferences::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }
            })
        return prefLiveData
    }

    /**
     * Make an API call to GitHub to get a given user's public profile data
     * @param userName GitHub username
     * @return GitUser data
     */
    fun getUserProfile(userName: String): LiveData<GitUser> {
        val userDisposable = CompositeDisposable()
        userDisposable.add(
            gitRetrofit.getUserProfile(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    userProfile.value = it
                }, {
                    DebugLogger("Error " + it.message)
                })
        )
        return userProfile
    }

    /**
     * Makes an API call to GitHub to local store results into LiveData
     * @param stringSearch The search query inputted by the current user
     */
    fun searchForUsers(stringSearch: String) {

        if (stringSearch.isEmpty()) {
            userSearchLiveData.postValue(listOf())
            return
        }

        compositeDisposable.add(
            gitRetrofit.getUserSearchResults(stringSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.items?.let { items ->
                        userSearchLiveData.postValue(items)
                    }
                    compositeDisposable.clear()
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
    }

    /**
     * Makes an API call to GitHub to log the current API call rate limits of the current user.
     * This API call DOES NOT count against said limits.
     */
    private fun logRateLimit() {
        compositeDisposable.add(
            gitRetrofit.getRateLimit()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    DebugLogger("Remaining calls: " + it.resources?.core?.remaining.toString())
                    DebugLogger("Remaining time: " + (it.resources?.core?.reset.toString()))
                }
        )
    }

}