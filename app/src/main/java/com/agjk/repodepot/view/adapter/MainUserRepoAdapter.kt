package com.agjk.repodepot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.model.data.Users

class MainUserRepoAdapter(val repoList: List<Repos>) : RecyclerView.Adapter<MainUserRepoAdapter.UserRepoViewHolder>() {

    inner class UserRepoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRepoViewHolder {
        return UserRepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false))
    }

    override fun getItemCount(): Int = 0

    override fun onBindViewHolder(holder: UserRepoViewHolder, position: Int) {

    }
}