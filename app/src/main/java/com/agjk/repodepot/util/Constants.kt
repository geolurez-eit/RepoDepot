package com.agjk.repodepot.util

class Constants {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val USER_NAME_PATH = "username"
        const val REPO_PATH = "repo"
        const val URL_PATH_REPOS = "/users/{$USER_NAME_PATH}/repos"
        const val URL_PATH_COMMITS = "/repos/{$USER_NAME_PATH}/${REPO_PATH}/commits"
        const val TAG_X = "TAG_X"
    }
}