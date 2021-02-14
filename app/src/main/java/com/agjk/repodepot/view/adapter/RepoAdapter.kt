package com.agjk.repodepot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.util.DebugLogger

class RepoAdapter(var repoList: List<Repos>) : RecyclerView.Adapter<RepoAdapter.UserRepoViewHolder>() {

    inner class UserRepoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val repoDetailCard: CardView = itemView.findViewById(R.id.cv_repo_detail_card)
        val repoTitle: TextView = itemView.findViewById(R.id.tv_repo_name)
        //val repoDescription: TextView = itemView.findViewById(R.id.tv_repo_description)
        val repoLanguage: TextView = itemView.findViewById(R.id.tv_repo_language)
        val repoRating: TextView = itemView.findViewById(R.id.tv_rating_count)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRepoViewHolder {
        return UserRepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repos, parent, false))
    }

    override fun getItemCount(): Int {
        DebugLogger("RepoList Size: ------------>   ${repoList.size}")
        DebugLogger("RepoList -------------->: ${repoList}")
        return repoList.size
    }

    override fun onBindViewHolder(holder: UserRepoViewHolder, position: Int) {
        val repo = repoList[position]

        DebugLogger("Repo Count Size: ${repoList.size}")



        holder.apply {
            repoTitle.text = repo.repoName
            //repoDescription.text = repo.repoDescription
            repoLanguage.text = repo.repoLanguage
            repoRating.text = repo.repoStarNum.toString()

            repoDetailCard.setOnClickListener {
                // TODO: transition to detail fragment
            }
        }
    }

    fun updateRepo(newRepoList: List<Repos>){
        DebugLogger("RepoList Size Update -------> ${newRepoList.size}")
        repoList = newRepoList
        notifyDataSetChanged()
    }
}