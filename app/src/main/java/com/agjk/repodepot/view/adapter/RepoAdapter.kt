package com.agjk.repodepot.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.util.DebugLogger
import org.w3c.dom.Text

class RepoAdapter(var repoList: List<Repos>, val repoDelegate:RepoDelegate) : RecyclerView.Adapter<RepoAdapter.UserRepoViewHolder>() {

    interface RepoDelegate{
        fun openDetailFragment(repoName: String, repoUrl: String, repoStar: String, repoDescription: String)
    }


    inner class UserRepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repoDetailCard: CardView = itemView.findViewById(R.id.cv_repo_card)
        val repoTitle: TextView = itemView.findViewById(R.id.tv_repo_name)
        val repoLanguage: TextView = itemView.findViewById(R.id.tv_repo_language)
        val repoStarCount: TextView = itemView.findViewById(R.id.tv_rating)


        // animation container
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint_container)
    }

    private lateinit var mycontext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRepoViewHolder {
        mycontext = parent.context
        return UserRepoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_repos, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    override fun onBindViewHolder(holder: UserRepoViewHolder, position: Int) {
        val repo = repoList[position]




        holder.apply {
            repoTitle.text = repo.repoName
            repoLanguage.text = repo.repoLanguage
            repoStarCount.text = repo.repoStarGazer

            repoDetailCard.setOnClickListener {
                // TODO: transition to detail fragment
                repoDelegate.openDetailFragment(repo.repoName, repo.repoUrl, repo.repoStarGazer, repo.repoDescription)
            }

            holder.constraintLayout.visibility = View.VISIBLE
            val animationFadeScale =
                AnimationUtils.loadAnimation(mycontext, R.anim.fade_scale_repo_recycler)
            holder.constraintLayout.startAnimation(animationFadeScale)
        }

    }

    fun updateRepo(newRepoList: List<Repos>) {
        repoList = newRepoList
        notifyDataSetChanged()
    }
}
