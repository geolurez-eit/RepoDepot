package com.agjk.repodepot.model.data

import androidx.fragment.app.Fragment
import com.agjk.repodepot.view.fragment.MainUserRepoFragment

data class Users(
    val imageUrl: String = "",
    val username: String = "",
    var userFragment: Fragment = MainUserRepoFragment(
        listOf(),
        "",
        "",
        ""
    )
)