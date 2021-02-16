package com.agjk.repodepot.util

class Constants {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val USER_NAME_PATH = "username"
        const val REPO_PATH = "repo"
        const val URL_PATH_REPOS = "/users/{$USER_NAME_PATH}/repos"
        const val URL_PATH_REPOS_PRIVATE = "/user/repos"
        const val URL_PATH_RATE_LIMIT = "/rate_limit"
        const val URL_PATH_COMMITS = "/repos/{$USER_NAME_PATH}/{$REPO_PATH}/commits"
        const val PAGE_QUERY = "page"
        const val PAGE_SIZE_QUERY = "per_page"
        const val TAG_X = "TAG_X"

        const val USER_SEARCH_QUERY = "q"
        const val USER_SEARCH_PATH = "search/users"

        const val TOKEN_USER = "token_user"
    }
}