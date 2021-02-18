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
import com.agjk.repodepot.util.DebugLogger
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class CommitAdapter(var commitList: List<Commits>) : RecyclerView.Adapter<CommitAdapter.UserDetailsViewHolder>() {


    inner class UserDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val detailsCard: CardView = itemView.findViewById(R.id.cv_repo_detail_card)
        val userImage : CircleImageView = itemView.findViewById(R.id.iv_user_image)
        val authorName : TextView = itemView.findViewById(R.id.tv_author_details)
        val commitMessage : TextView= itemView.findViewById(R.id.tv_repo_details)
        val commitCode: TextView = itemView.findViewById(R.id.tv_commit_hashcode) // (this is the sha in the api


        // animation container
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.linearLayout)
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
                .placeholder(R.drawable.ic_logo_white_cat)
                .into(userImage)
            authorName.text = commit.authorName
            commitMessage.text = commit.commitMessage
            commitCode.text = commit.commitHashCode
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