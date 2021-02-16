package com.agjk.repodepot.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agjk.repodepot.model.data.*
import com.agjk.repodepot.network.GitRetrofit
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.fragment.MainUserRepoFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import java.util.*

object DepotRepository {
    private val resultRepoList: MutableList<GitRepo.GitRepoItem> = mutableListOf()
    private val resultCommitList: MutableList<GitRepoCommits.GitRepoCommitsItem> = mutableListOf()

    private val repoUserLiveData: MutableLiveData<List<GitRepo.GitRepoItem>> = MutableLiveData()
    private val userListLiveData: MutableLiveData<List<GitUser>> = MutableLiveData()
    private val commitLiveData: MutableLiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        MutableLiveData()
    var prefLiveData: MutableLiveData<Preferences> = MutableLiveData()
    private var userProfile: GitUser = GitUser()

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val gitRetrofit = GitRetrofit
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var is24HoursPassed = false


    init {
        firebaseDatabase.setPersistenceEnabled(true)
        firebaseDatabase.reference.keepSynced(true)
    }


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
                    DebugLogger(it.size.toString())
                    if (it.size < 100) {
                        resultRepoList.addAll(it)
                        postRepos(userName, resultRepoList)
                        repoDisposable.clear()
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

    private fun saveNewCommits(userName: String, repoName: String, page: Int) {
        DebugLogger("DepotRepository - saveNewCommits")
        DebugLogger("compositeDisposable.add")
        val commitDisposable = CompositeDisposable()
        commitDisposable.add(
            gitRetrofit.getRepositoryCommits(userName, repoName, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.size < 100) {
                        resultCommitList.addAll(it)
                        postCommits(it, userName, repoName)
                        commitDisposable.clear()
                    } else {
                        resultCommitList.addAll(it)
                        saveNewCommits(userName, repoName, page + 1)
                    }
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
    }

    private fun postRepos(userName: String, repo: List<GitRepo.GitRepoItem>) {
        DebugLogger("DepotRepository.postRepos")
        firebaseDatabase.reference.child("REPOSITORIES").child(userName)
            .setValue(repo)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DebugLogger(LocalDateTime.now().toString())
            firebaseDatabase.reference.child("REPOSITORIES").child(userName).child("lastUpdated")
                .setValue(LocalDateTime.now().toString())
        } else {
            DebugLogger(Calendar.getInstance().time.toString())
            firebaseDatabase.reference.child("REPOSITORIES").child(userName).child("lastUpdated")
                .setValue(Calendar.getInstance().time.toString())
        }
        DebugLogger("Repos for :${repo.first().owner?.login} added!")
    }

    private fun postCommits(
        commits: List<GitRepoCommits.GitRepoCommitsItem>,
        userName: String,
        repoName: String
    ) {
        DebugLogger("DepotRepository.postCommits")
        firebaseDatabase.reference.child("COMMITS").child(userName).child(repoName)
            .setValue(commits)
        DebugLogger("Commits for :${repoName} added!")
    }

    fun getReposForUser(username: String): LiveData<List<GitRepo.GitRepoItem>> {
        DebugLogger("DepotRepository.getReposForUser")
        // Check if it has been 24 hours
        checkIf24Hours(username)
        if (true) {
            //Update repos for user
            DebugLogger("Updating repos")
            saveNewRepos(username, 1)
        } else {
            DebugLogger("Unable to update repos")
        }
        //add user to userlist
        DebugLogger(firebaseAuth.currentUser?.displayName.toString())
        /*firebaseDatabase.reference.child("USERLISTS")
            .child(firebaseAuth.currentUser?.displayName.toString()).child(username)
            .setValue(username)*/
        //Retrieve stored repos
        return getRepositories(username)
    }

    private fun checkIf24Hours(userName: String) {
        DebugLogger("24 Hour Check")
        firebaseDatabase.reference.child("REPOSITORIES").child(userName).child("lastUpdated")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(String::class.java).runCatching {
                        DebugLogger("Last Updated: $this")
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DebugLogger(
                                "Last Updated + 24: " + LocalDateTime.parse(this).plusDays(1L)
                            )
                            DebugLogger("Now: " + LocalDateTime.now())
                            if (LocalDateTime.parse(this).plusDays(1L) < LocalDateTime.now()) {
                                DebugLogger("It has been 24 hours")
                                is24HoursPassed = true
                            } else {
                                DebugLogger("Still has not been 24 hours")
                            }
                        } else {
                            DebugLogger("Pre Oreo")
                            TODO("VERSION.SDK_INT < O")
                        }
                    }.getOrElse {
                        is24HoursPassed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("24 Hour Check on Cancelled: " + error.message)
                }

            })
    }


    fun getCommitsForUser(
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> {
        DebugLogger("DepotRepository.getCommitsForUser")
        // Check if it has been 24 hours
        if (true) {
            //Update commits for repo
            saveNewCommits(username, repoName, 1)
        }
        //Retrieve stored commits
        firebaseDatabase.reference.child("COMMITS").child(username).child(repoName)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    DebugLogger("onDataChange")
                    val commitList = mutableListOf<GitRepoCommits.GitRepoCommitsItem>()
                    snapshot.children.forEach {
                        it.getValue(GitRepoCommits.GitRepoCommitsItem::class.java)?.let { commit ->
                            commitList.add(commit)
                        }
                    }
                    commitLiveData.value = commitList
                }
            })
        DebugLogger("Returning from getCommitsForUser")
        return commitLiveData
    }

    fun getReposForUserPrivate(
        username: String,
        token: String
    ): LiveData<List<GitRepo.GitRepoItem>> {
        DebugLogger("DepotRepository.getReposForUser")
        // Check if it has been 24 hours
        checkIf24Hours(username + "_private")
        if (true) {
            //Update repos for user
            saveNewPrivateRepos(username, token, 1)
        } else {
            DebugLogger("Unable to update repos")
        }
        //add user to userlist
        DebugLogger(firebaseAuth.currentUser?.displayName.toString())
        /*firebaseDatabase.reference.child("USERLISTS")
            .child(firebaseAuth.currentUser?.displayName.toString()).child(username)
            .setValue(username)*/
        //Retrieve stored repos
        return getRepositories(username+"_private")
    }

    private fun getRepositories(username: String): MutableLiveData<List<GitRepo.GitRepoItem>> {
        firebaseDatabase.reference.child("REPOSITORIES").child(username)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    DebugLogger("onDataChange")
                    val repoList = mutableListOf<GitRepo.GitRepoItem>()
                    snapshot.children.forEach {
                        if (it.key != "lastUpdated") {
                            it.getValue(GitRepo.GitRepoItem::class.java)?.let { repo ->
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

    fun getUserList(thisUserName:String): LiveData<List<GitUser>> {
        firebaseDatabase.reference.child("USERLISTS")
            .child(thisUserName)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val usersList = mutableListOf<GitUser>()
                        snapshot.children.forEach{
                            it.getValue(GitUser::class.java).let { user ->
                                DebugLogger("getUserList user: "+user.toString())
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

    fun addUserPreferences(preferences: Preferences) {
        firebaseDatabase.reference.child("USER_PREFERENCES")
            .child(firebaseAuth.currentUser?.displayName.toString())
            .setValue(preferences)
    }

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
                    DebugLogger("Error within addUserToList "+it.message)
                })
        )
    }

    fun getUserProfile(userName: String): GitUser {
        var userDisposable = CompositeDisposable()
        userDisposable.add(
            gitRetrofit.getUserProfile(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    userProfile = it
                }, {
                    DebugLogger("Error " + it.message)
                })
        )
        return userProfile
    }
}