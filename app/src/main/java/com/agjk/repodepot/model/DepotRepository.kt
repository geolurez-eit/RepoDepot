package com.agjk.repodepot.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.network.GitRetrofit
import com.agjk.repodepot.util.DebugLogger
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object DepotRepository {
    private val repoUserLiveData: MutableLiveData<List<GitRepo.GitRepoItem>> = MutableLiveData()
    private val commitLiveData: MutableLiveData<List<GitRepoCommits.GitRepoCommitsItem>> =
        MutableLiveData()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val gitRetrofit: GitRetrofit = GitRetrofit()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()


    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun saveNewRepos(userName: String) {
        DebugLogger("DepotRepository - saveNewRepos")
        DebugLogger("compositeDisposable.add")
        compositeDisposable.add(
            gitRetrofit.getUserRepositories(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    DebugLogger(".subscribe - it: $it")
                    postRepos(userName, it)
                    compositeDisposable.clear()
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
    }

    fun saveNewPrivateRepos(userName: String, token: String) {
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
    }

    fun saveNewCommits(userName: String, repoName: String) {
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
        DebugLogger("Returning from getReposForUser")
        return repoUserLiveData
    }

    fun getCommitsForUser(
        username: String,
        repoName: String
    ): LiveData<List<GitRepoCommits.GitRepoCommitsItem>> {
        DebugLogger("DepotRepository.getCommitsForUser")
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
}