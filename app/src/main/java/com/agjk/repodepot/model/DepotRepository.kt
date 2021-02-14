package com.agjk.repodepot.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
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

object DepotRepository {
    private val resultRepoList: List<GitRepo.GitRepoItem> = listOf()
    private val repoUserLiveData: MutableLiveData<List<GitRepo.GitRepoItem>> = MutableLiveData()
    private val userListLiveData: MutableLiveData<List<String>> = MutableLiveData()
    private val commitLiveData: MutableLiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        MutableLiveData()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val gitRetrofit = GitRetrofit
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()


    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }


    private fun saveNewRepos(userName: String, page: Int) {
        DebugLogger("DepotRepository - saveNewRepos")
        DebugLogger("compositeDisposable.add")
        compositeDisposable.add(
            gitRetrofit.getUserRepositories(userName, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //DebugLogger(".subscribe - it: $it")
                    if (it.size < 100)
                        postRepos(userName, it)
                    else
                        resultRepoList
                    compositeDisposable.clear()
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
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

    private fun saveNewPrivateRepos(userName: String, token: String) {
        DebugLogger("DepotRepository - saveNewRepos")
        DebugLogger("compositeDisposable.add")
        compositeDisposable.add(
            gitRetrofit.getUserAllRepositories(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    DebugLogger(".subscribe - it: $it")
                    postRepos(userName + "_private", it)
                    compositeDisposable.clear()
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
        compositeDisposable.add(
            gitRetrofit.getRateLimit()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    DebugLogger(it.rate?.limit.toString())
                    DebugLogger(it.rate?.remaining.toString())
                    DebugLogger(it.rate?.reset.toString())
                    DebugLogger(it.resources?.core?.remaining.toString())
                    DebugLogger(it.resources?.core?.reset.toString())
                }
        )
    }

    private fun saveNewCommits(userName: String, repoName: String) {
        DebugLogger("DepotRepository - saveNewCommits")
        DebugLogger("compositeDisposable.add")
        compositeDisposable.add(
            gitRetrofit.getRepositoryCommits(userName, repoName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    DebugLogger(".subscribe - it: $it")
                    postCommits(it, userName, repoName)
                    compositeDisposable.clear()
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
        if (true) {
            //Update repos for user
            saveNewRepos(username, 1)
        }
        //add user to userlist
        DebugLogger(firebaseAuth.currentUser?.displayName.toString())
        firebaseDatabase.reference.child("USERLISTS")
            .child(firebaseAuth.currentUser?.displayName.toString()).child(username).setValue(username)
        //Retrieve stored repos
        return getRepositories(username)
    }

    fun getCommitsForUser(
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> {
        DebugLogger("DepotRepository.getCommitsForUser")
        // Check if it has been 24 hours
        if (true) {
            //Update commits for repo
            saveNewCommits(username, repoName)
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
        if (true) {
            //Update repos for user
            saveNewPrivateRepos(username, token)
        }
        //add user to userlist
        DebugLogger(firebaseAuth.currentUser?.displayName.toString())
        firebaseDatabase.reference.child("USERLISTS")
            .child(firebaseAuth.currentUser?.displayName.toString()).child(username).setValue(username)
        //Retrieve stored repos
        return getRepositories(username)
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
                        it.getValue(GitRepo.GitRepoItem::class.java)?.let { repo ->
                            repoList.add(repo)
                        }
                    }
                    repoUserLiveData.value = repoList
                }
            })
        DebugLogger("Returning from getRepositories")
        return repoUserLiveData
    }

    fun getUserList(): LiveData<List<String>> {
       val thisUserName = firebaseAuth.currentUser?.displayName.toString()
        firebaseDatabase.reference.child("USERLISTS")
            .child(thisUserName).child(thisUserName).setValue(thisUserName)
        firebaseDatabase.reference.child("USERLISTS")
            .child(thisUserName)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val usersList = mutableListOf<String>()
                        snapshot.children.forEach {
                            it.getValue(String::class.java).let { user ->
                                usersList.add(user.toString())
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

    fun addUserToList(userName: String) {
        val thisUserName = firebaseAuth.currentUser?.displayName.toString()
        firebaseDatabase.reference.child("USERLISTS")
            .child(thisUserName).child(userName).setValue(userName)
    }
}