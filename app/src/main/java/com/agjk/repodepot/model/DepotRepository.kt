package com.agjk.repodepot.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.network.GitRetrofit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object DepotRepository {
    private val repoLiveData: MutableLiveData<List<GitRepo.GitRepoItem>> = MutableLiveData()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val gitRetrofit: GitRetrofit = GitRetrofit()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()


    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun getRepos(): LiveData<List<GitRepo.GitRepoItem>> {
        firebaseDatabase.reference.child("REPOSITORIES")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val repoList = mutableListOf<GitRepo.GitRepoItem>()
                    snapshot.children.forEach {
                        it.getValue(GitRepo.GitRepoItem::class.java)?.let { repo ->
                            repoList.add(repo)
                        }
                    }
                    repoLiveData.value = repoList
                }
            })
        return repoLiveData
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
                    postRepos(it)
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
                    postCommits(it, repoName)
                    compositeDisposable.clear()
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
    }

    private fun postRepos(repo: List<GitRepo.GitRepoItem>) {
        firebaseDatabase.reference.child("REPOSITORIES").child(repo.first().owner?.login.toString())
            .setValue(repo)
        DebugLogger(firebaseDatabase.reference.toString())
        DebugLogger("Repos for :${repo.first().owner?.login} added!")
    }

    private fun postCommits(commits: List<GitRepoCommits.GitRepoCommitsItem>, repoName: String) {
        firebaseDatabase.reference.child("COMMITS").child(repoName).setValue(commits)
        DebugLogger(firebaseDatabase.reference.toString())
        DebugLogger("Repos for :${repoName} added!")
    }

    fun postReposComplete(repo: List<GitRepo.GitRepoItem>) {
        firebaseDatabase.reference.child("REPOSITORIES").child(repo.first().owner?.login.toString())
            .setValue(repo)
        DebugLogger(firebaseDatabase.reference.toString())
        DebugLogger("Repos for :${repo.first().owner?.login} added!")
    }


}