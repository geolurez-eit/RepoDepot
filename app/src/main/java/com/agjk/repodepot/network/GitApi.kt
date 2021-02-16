package com.agjk.repodepot.network

import com.agjk.repodepot.model.data.GitRepo
import com.agjk.repodepot.model.data.GitRepoCommits
import com.agjk.repodepot.model.data.GitUser
import com.agjk.repodepot.model.data.RateLimit
import com.agjk.repodepot.util.Constants.Companion.PAGE_QUERY
import com.agjk.repodepot.util.Constants.Companion.PAGE_SIZE_QUERY
import com.agjk.repodepot.util.Constants.Companion.REPO_PATH
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_COMMITS
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_RATE_LIMIT
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_REPOS
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_REPOS_PRIVATE
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_USERS
import com.agjk.repodepot.util.Constants.Companion.USER_NAME_PATH
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GitApi {
    @GET(URL_PATH_REPOS)
    fun getGitRepos(@Path(USER_NAME_PATH) username: String): Observable<List<GitRepo.GitRepoItem>>
    @GET(URL_PATH_USERS)
    fun getUserProfile(@Path(USER_NAME_PATH) username: String): Observable<GitUser>

    @GET(URL_PATH_REPOS)
    fun getGitReposPage(
        @Path(USER_NAME_PATH) username: String, @Query(PAGE_QUERY) page: Int, @Query(
            PAGE_SIZE_QUERY
        ) pageSize: Int
    ): Observable<List<GitRepo.GitRepoItem>>

    @GET(URL_PATH_COMMITS)
    fun getGitRepoCommits(
        @Path(USER_NAME_PATH) username: String,
        @Path(REPO_PATH) repo: String,@Query(PAGE_QUERY) page: Int, @Query(
            PAGE_SIZE_QUERY
        ) pageSize: Int
    ): Observable<List<GitRepoCommits.GitRepoCommitsItem>>

    @GET(URL_PATH_REPOS_PRIVATE)
    fun getGitReposPrivate(
        @Header("Authorization") authHeader: String, @Query(PAGE_QUERY) page: Int, @Query(
            PAGE_SIZE_QUERY
        ) pageSize: Int
    ): Observable<List<GitRepo.GitRepoItem>>

    @GET(URL_PATH_RATE_LIMIT)
    fun getRateLimit(): Observable<RateLimit>
}