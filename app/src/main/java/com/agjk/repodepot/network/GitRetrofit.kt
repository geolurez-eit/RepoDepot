package com.agjk.repodepot.network

import com.agjk.repodepot.model.data.*
import com.agjk.repodepot.util.Constants.Companion.BASE_URL
import com.agjk.repodepot.util.DebugLogger
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Implementation of Retrofit for GitHub API calls.
 */
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


    //////////////////////////
    // Repository functions
    //////////////////////////
    /**
     * Runs the API call to GitHub to retrieve a list of repositories for a given user.
     * @param username GitHub username to pull repositories for
     * @param page Page of results to pull from
     * @return List of repositories as an Observable
     */
    fun getUserRepositories(username: String, page: Int): Observable<List<GitRepo.GitRepoItem>> {
        DebugLogger("GitRetroFit.getUserRepositories")
        DebugLogger("username: $username")
        return gitApi.getGitRepos(username, page, 100)
    }

    /**
     * Runs the API call to GitHub to retrieve a list of repositories for the current user,
     * including private repositories.
     * @param token GitHub Oauth token to be used as the Authorization header in the API call
     * @param page Page of results to pull from
     * @return List of repositories as an Observable
     */
    fun getUserPrivateRepositories(
        token: String,
        page: Int
    ): Observable<List<GitRepo.GitRepoItem>> {
        DebugLogger("GitRetroFit.getUserPrivateRepositories")
        DebugLogger(token)
        return gitApi.getGitReposPrivate("token $token", page, 100)
    }

    //////////////////////////
    // Commit functions
    //////////////////////////
    /**
     * Runs the API call to Github for retrieving the commits for a given repository.
     * @param token GitHub Oauth token to be used as the Authorization header in the API call
     * @param username GitHub username of repository owner
     * @param repo GitHub repository to pull commits from
     * @param page Page of results to pull from
     * @return
     */
    fun getRepositoryCommits(
        token: String,
        username: String,
        repo: String,
        page: Int
    ): Observable<List<GitRepoCommits.GitRepoCommitsItem>> =
        gitApi.getGitRepoCommits("token $token", username, repo, page, 100)

    /**
     * Makes an API call to GitHub to log the current API call rate limits of the current user.
     * This API call DOES NOT count against said limits.
     * @return RateLimit object, contains data for various rate limits for the GitHub API
     * and the current user's call counts.
     */
    fun getRateLimit(): Observable<RateLimit> {
        return gitApi.getRateLimit()
    }

    /**
     * Runs an API call to Github to retrieve the public profile data of a given user
     * @param username GitHub username
     * @return GitUser data object
     */
    fun getUserProfile(username: String): Observable<GitUser> = gitApi.getUserProfile(username)

    /**
     * Runs an API call to GitHub to retrieve user account search results
     * @param stringSearch username to search for
     * @return UserSearch data object that contains search results
     */
    fun getUserSearchResults(stringSearch: String): Observable<UserSearch> =
        gitApi.searchUsers(stringSearch)

}