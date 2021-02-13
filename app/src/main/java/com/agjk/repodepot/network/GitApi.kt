package com.agjk.repodepot.network

import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.util.Constants.Companion.REPO_PATH
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_COMMITS
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_REPOS
import com.agjk.repodepot.util.Constants.Companion.USER_NAME_PATH
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitApi {
    @GET(URL_PATH_REPOS)
    fun getGitRepos(@Path(USER_NAME_PATH) userName: String): Observable<List<GitRepo.GitRepoItem>>

    @GET(URL_PATH_COMMITS)
    fun getGitRepoCommits(
        @Path(USER_NAME_PATH) userName: String,
        @Path(REPO_PATH) repoName: String
    ): Observable<GitRepoCommits.GitRepoCommitsItem>
}