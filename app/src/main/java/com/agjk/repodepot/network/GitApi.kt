package com.agjk.repodepot.network

import com.agjk.repodepot.util.Constants
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_COMMITS
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_REPOS
import com.agjk.repodepot.util.Constants.Companion.USER_NAME_PATH
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitApi {
    @GET(URL_PATH_REPOS)
    fun getGitRepos(@Path(USER_NAME_PATH) userName: String?)

    @GET(URL_PATH_COMMITS)
    fun getGitRepoCommits(@Path(USER_NAME_PATH) userName: String?)
}