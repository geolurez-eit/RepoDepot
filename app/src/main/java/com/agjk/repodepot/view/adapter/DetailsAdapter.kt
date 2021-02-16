package com.agjk.repodepot.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Commits
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.model.data.Users
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class DetailsAdapter(var commitList: List<Commits>) : RecyclerView.Adapter<DetailsAdapter.UserDetailsViewHolder>() {


    inner class UserDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val detailsCard: CardView = itemView.findViewById(R.id.cv_detail_card)
        val userImage : CircleImageView = itemView.findViewById(R.id.details_user_profil)
        val authorName : TextView = itemView.findViewById(R.id.author_details)
        val commitMessage : TextView= itemView.findViewById(R.id.commit_message)
        val commitCode: TextView = itemView.findViewById(R.id.tv_commit_hashcode) // (this is the sha in the api


        // animation container
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.d_linearLayout)
    }
    private lateinit var mycontext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDetailsViewHolder {
        mycontext= parent.context
        return UserDetailsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_details, parent, false))
    }

    override fun getItemCount(): Int {
        return commitList.size
    }

    override fun onBindViewHolder(holder: UserDetailsViewHolder, position: Int) {


        val commit = commitList[position]

        DebugLogger("Repo Count Size: ${commitList.size}")



        holder.apply {
            Glide.with(itemView.context)
                .load(commit.imageUrl)
                .placeholder(R.drawable.github_logo)
                .into(userImage)
            authorName.text = commit.authorName
            commitMessage.text = commit.commitMessage
            commitCode.text = commit.commitCode
        }
        holder.constraintLayout.visibility = View.VISIBLE
        val animationFadeScale = AnimationUtils.loadAnimation(mycontext, R.anim.fade_scale_repo_recycler)
        holder.constraintLayout.startAnimation(animationFadeScale)

    }

    fun updateDetails(newDetailsList: List<Commits>){
        DebugLogger("DetailsList Size Update -------> ${newDetailsList.size}")
        commitList = newDetailsList
        notifyDataSetChanged()
    }

}