package com.agjk.repodepot.network

import com.agjk.repodepot.model.data.*
import com.agjk.repodepot.util.Constants.Companion.PAGE_QUERY
import com.agjk.repodepot.util.Constants.Companion.PAGE_SIZE_QUERY
import com.agjk.repodepot.util.Constants.Companion.REPO_PATH
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_COMMITS
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_RATE_LIMIT
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_REPOS
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_REPOS_PRIVATE
import com.agjk.repodepot.util.Constants.Companion.URL_PATH_USERS
import com.agjk.repodepot.util.Constants.Companion.USER_NAME_PATH
import com.agjk.repodepot.util.Constants.Companion.USER_SEARCH_PATH
import com.agjk.repodepot.util.Constants.Companion.USER_SEARCH_QUERY
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The retrofit interface class for queries to the GitHub API
 */
interface GitApi {
    /**
     * Query for getting the GitHub public profile data for a user
     * @param username GitHub username
     * @return GitHub profile data
     */
    @GET(URL_PATH_USERS)
    fun getUserProfile(@Path(USER_NAME_PATH) username: String): Observable<GitUser>

    /**
     * Query for getting the repositories of a given GitHub user
     * @param username GitHub username
     * @param page Which page of results to retrieve
     * @param pageSize How many results per page (max 100)
     * @return List of GitHub repositories
     */
    @GET(URL_PATH_REPOS)
    fun getGitRepos(
        @Path(USER_NAME_PATH) username: String, @Query(PAGE_QUERY) page: Int, @Query(
            PAGE_SIZE_QUERY
        ) pageSize: Int
    ): Observable<List<GitRepo.GitRepoItem>>

    /**
     * Query for getting the repositories of the current GitHub user, including private ones
     * @param authHeader GitHub Oauth token passed as an Authorization header
     * @param page Which page of results to retrieve
     * @param pageSize How many results per page (max 100)
     * @return List of GitHub repositories
     */
    @GET(URL_PATH_REPOS_PRIVATE)
    fun getGitReposPrivate(
        @Header("Authorization") authHeader: String, @Query(PAGE_QUERY) page: Int, @Query(
            PAGE_SIZE_QUERY
        ) pageSize: Int
    ): Observable<List<GitRepo.GitRepoItem>>

    /**
     * Query for getting the commits for a given user's repository
     * @param authHeader GitHub Oauth token passed as an Authorization header
     * @param username GitHub username
     * @param repo GitHub repository name
     * @param page Which page of results to retrieve
     * @param pageSize How many results per page (max 100)
     * @return List of GitHub commits
     */
    @GET(URL_PATH_COMMITS)
    fun getGitRepoCommits(
        @Header("Authorization") authHeader: String,
        @Path(USER_NAME_PATH) username: String,
        @Path(REPO_PATH) repo: String, @Query(PAGE_QUERY) page: Int, @Query(
            PAGE_SIZE_QUERY
        ) pageSize: Int
    ): Observable<List<GitRepoCommits.GitRepoCommitsItem>>

    /**
     * Makes an API call to GitHub to log the current API call rate limits of the current user.
     * This API call DOES NOT count against said limits.
     * @return RateLimit object, contains data for various rate limits for the GitHub API
     * and the current user's call counts.
     */
    @GET(URL_PATH_RATE_LIMIT)
    fun getRateLimit(): Observable<RateLimit>

    /**
     * Query for getting GitHub user search results
     * @param stringSearch String used to search for GitHub usernames
     * @return UserSearch object that contains the data for the search results
     */
    @GET(USER_SEARCH_PATH)
    fun searchUsers(@Query(USER_SEARCH_QUERY) stringSearch: String): Observable<UserSearch>
}