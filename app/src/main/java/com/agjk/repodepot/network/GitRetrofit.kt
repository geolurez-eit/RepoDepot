package com.agjk.repodepot.network

import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.util.Constants.Companion.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitRetrofit {
    private var gitApi: GitApi

    init {
        gitApi = createGitApi(createRetrofit())
    }

    private fun createGitApi(retrofit: Retrofit): GitApi {
        return retrofit.create(GitApi::class.java)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getUserRepositories(username: String): Observable<GitRepo.GitRepoItem> =
        gitApi.getGitRepos(username)


    fun getRepositoryCommits(
        username: String,
        repo: String
    ): Observable<GitRepoCommits.GitRepoCommitsItem> = gitApi.getGitRepoCommits(username, repo)

}