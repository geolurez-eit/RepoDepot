package com.agjk.repodepot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.DebugLogger
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.network.GitRetrofit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RepoViewModel : ViewModel() {
    private val gitRetrofit: GitRetrofit = GitRetrofit()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    var repoData: MutableList<GitRepo.GitRepoItem> = mutableListOf()


    fun getStoredRepos(): LiveData<List<GitRepo.GitRepoItem>> = DepotRepository.getRepos()

    fun saveNewRepos(userName: String) {
        DebugLogger("RepoViewModel - saveNewRepos")
        DebugLogger("compositeDisposable.add")
        compositeDisposable.add(
            gitRetrofit.getUserRepositories(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    DebugLogger(".subscribe - it: $it")
                    //repoData.postValue(it)
                    repoData = it as MutableList<GitRepo.GitRepoItem>
                    compositeDisposable.clear()
                }, {
                    DebugLogger(".subscribe Error")
                    DebugLogger(it.localizedMessage)
                })
        )
        DebugLogger("repoData: $repoData")
        repoData.forEach{
            DepotRepository.postRepo(it)
        }
    }

    override fun onCleared() {
        //repoData.removeObserver {  }
        super.onCleared()
    }

}