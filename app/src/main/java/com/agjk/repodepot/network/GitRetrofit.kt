package com.agjk.repodepot.network

import android.annotation.SuppressLint
import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.model.data.RateLimit
import com.agjk.repodepot.util.Constants.Companion.BASE_URL
import com.agjk.repodepot.util.DebugLogger
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object GitRetrofit {
    private var gitApi: GitApi
    private val client = OkHttpClient.Builder().build()

    init {
        gitApi = createGitApi(createRetrofit())
    }

    private fun createGitApi(retrofit: Retrofit): GitApi {
        return retrofit.create(GitApi::class.java)
    }

    private fun createRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()


    fun getUserRepositories(username: String, page:Int): Observable<List<GitRepo.GitRepoItem>> {
        DebugLogger("GitRetroFit.getUserRepositories")
        DebugLogger("username: $username")
        DebugLogger("gitApi: $gitApi")
        return  gitApi.getGitReposPage(username,page,100)
    }
    fun getRateLimit():Observable<RateLimit>{
        return gitApi.getRateLimit()
    }

    fun getUserAllRepositories(token: String): Observable<List<GitRepo.GitRepoItem>> {
        DebugLogger("GitRetroFit.getUserRepositories")
        DebugLogger("gitApi: $gitApi")
        DebugLogger(token)
        return gitApi.getGitReposPrivate("token $token")
    }

    fun getRepositoryCommits(
        username: String,
        repo: String
    ): Observable<List<GitRepoCommits.GitRepoCommitsItem>> =
        gitApi.getGitRepoCommits(username, repo)

}