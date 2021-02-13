package com.agjk.repodepot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.agjk.repodepot.DebugLogger
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.network.GitRetrofit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RepoViewModel : ViewModel() {
    private val gitRetrofit: GitRetrofit = GitRetrofit()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val repoData: MutableLiveData<GitRepo.GitRepoItem> = MutableLiveData()


    fun getStoredRepos(): LiveData<List<GitRepo.GitRepoItem>> = DepotRepository.getRepos()

    fun saveNewRepos(userName: String) {
        compositeDisposable.add(
            gitRetrofit.getUserRepositories(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    repoData.postValue(it)
                    compositeDisposable.clear()
                }, {
                    DebugLogger(it.localizedMessage)
                })
        )
        repoData.observeForever {
            DepotRepository.postRepo(it)
        }
    }

    override fun onCleared() {
        //repoData.removeObserver {  }
        super.onCleared()
    }

}