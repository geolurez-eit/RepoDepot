package com.agjk.repodepot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.model.data.Users

class MainUserRepoAdapter(val repoList: List<Repos>) : RecyclerView.Adapter<MainUserRepoAdapter.UserRepoViewHolder>() {

    inner class UserRepoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val repoDetailCard: ConstraintLayout = itemView.findViewById(R.id.cl_user_item_box)
        val repoTitle: TextView = itemView.findViewById(R.id.tv_repo_name)
        val repoDescription: TextView = itemView.findViewById(R.id.tv_repo_description)
        val repoLanguage: TextView = itemView.findViewById(R.id.tv_repo_language)
        val repoRating: TextView = itemView.findViewById(R.id.tv_rating_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRepoViewHolder {
        return UserRepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false))
    }

    override fun getItemCount(): Int = repoList.size

    override fun onBindViewHolder(holder: UserRepoViewHolder, position: Int) {
        val repo = repoList[position]

        holder.apply {
            repoTitle.text = repo.repoName
            repoDescription.text = repo.repoDescription
            repoLanguage.text = repo.repoLanguage
            repoRating.text = repo.repoStarNum.toString()
        }
    }
}