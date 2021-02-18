package com.agjk.repodepot.model.data

data class Profile(
    val name: String?,
    val bio: String?,
    val username: String?,
    val email: String?,
    val imageUrl: String?,
    val followers: Int,
    val followings: Int,
    val htmlUrl: String,
    val numOfRepo: String,
)

