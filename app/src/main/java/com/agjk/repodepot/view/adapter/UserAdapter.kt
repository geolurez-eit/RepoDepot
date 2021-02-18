package com.agjk.repodepot.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Users
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.bumptech.glide.Glide

class UserAdapter(var userList: List<Users>, val activity: MainActivity): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var selectedPosition = 0
    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val clUserBox: CardView = itemView.findViewById(R.id.cl_user_item_box)
        val ivProfile: ImageView = itemView.findViewById(R.id.iv_user_profile)
        val tvUsers: TextView = itemView.findViewById(R.id.tv_users_nav)

        // animation container
        val user_constraintLayout: ConstraintLayout = itemView.findViewById(R.id.user_constraintLayout)
    }

    private lateinit var mycontext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        mycontext = parent.context
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        )
    }

    override fun getItemCount(): Int = userList.size



    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

       holder.apply {
           Glide.with(itemView.context)
               .load(user.imageUrl)
               .placeholder(R.drawable.ic_logo_dark_cat)
               .into(ivProfile)

           tvUsers.text = user.username

           setSelectedUserColor(position)

           clUserBox.setOnClickListener {
               activity.loadViewPagerFragment(position)
               //activity.updateRepoList(user.repo)
               activity.closeNavDrawer()
           }
       }

        holder.user_constraintLayout.visibility = View.VISIBLE
        val animationFadeScale =
            AnimationUtils.loadAnimation(mycontext, R.anim.fade_scale_repo_recycler)
        holder.user_constraintLayout.startAnimation(animationFadeScale)
    }

    private fun UserViewHolder.setSelectedUserColor(position: Int) {
        if (selectedPosition == position) {
            clUserBox.setBackgroundResource(R.color.DarkGray50)
            tvUsers.setTextColor(Color.WHITE)
        } else {
            clUserBox.setBackgroundColor(Color.WHITE)
            tvUsers.setTextColor(Color.BLACK)
        }
    }

    fun selectedUser(position: Int){
        selectedPosition = position
        notifyDataSetChanged()
    }

    fun updateUsers(newUserList: List<Users>){
        userList = newUserList

        //DebugLogger("UserAdapter.updateUsers: ${userList.size}")
        notifyDataSetChanged()
    }
}