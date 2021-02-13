package com.agjk.repodepot.model.data

import androidx.fragment.app.Fragment

data class Users(val imageUrl: String, val username: String, val userFragment: Fragment, val repo: List<Repos>)