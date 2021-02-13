package com.agjk.repodepot.model.data

class GitRepoCommits : ArrayList<GitRepoCommits.GitRepoCommitsItem>(){
    data class GitRepoCommitsItem(
        val author: Author? = Author(),
        val comments_url: String? = "",
        val commit: Commit? = Commit(),
        val committer: Committer? = Committer(),
        val html_url: String? = "",
        val node_id: String? = "",
        val parents: List<Any?>? = listOf(),
        val sha: String? = "",
        val url: String? = ""
    ) {
        data class Author(
            val avatar_url: String? = "",
            val events_url: String? = "",
            val followers_url: String? = "",
            val following_url: String? = "",
            val gists_url: String? = "",
            val gravatar_id: String? = "",
            val html_url: String? = "",
            val id: Int? = 0,
            val login: String? = "",
            val node_id: String? = "",
            val organizations_url: String? = "",
            val received_events_url: String? = "",
            val repos_url: String? = "",
            val site_admin: Boolean? = false,
            val starred_url: String? = "",
            val subscriptions_url: String? = "",
            val type: String? = "",
            val url: String? = ""
        )
    
        data class Commit(
            val author: Author? = Author(),
            val comment_count: Int? = 0,
            val committer: Committer? = Committer(),
            val message: String? = "",
            val tree: Tree? = Tree(),
            val url: String? = "",
            val verification: Verification? = Verification()
        ) {
            data class Author(
                val date: String? = "",
                val email: String? = "",
                val name: String? = ""
            )
    
            data class Committer(
                val date: String? = "",
                val email: String? = "",
                val name: String? = ""
            )
    
            data class Tree(
                val sha: String? = "",
                val url: String? = ""
            )
    
            data class Verification(
                val payload: Any? = Any(),
                val reason: String? = "",
                val signature: Any? = Any(),
                val verified: Boolean? = false
            )
        }
    
        data class Committer(
            val avatar_url: String? = "",
            val events_url: String? = "",
            val followers_url: String? = "",
            val following_url: String? = "",
            val gists_url: String? = "",
            val gravatar_id: String? = "",
            val html_url: String? = "",
            val id: Int? = 0,
            val login: String? = "",
            val node_id: String? = "",
            val organizations_url: String? = "",
            val received_events_url: String? = "",
            val repos_url: String? = "",
            val site_admin: Boolean? = false,
            val starred_url: String? = "",
            val subscriptions_url: String? = "",
            val type: String? = "",
            val url: String? = ""
        )
    }
}