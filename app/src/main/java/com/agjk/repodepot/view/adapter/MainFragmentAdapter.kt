package com.agjk.repodepot.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agjk.repodepot.model.data.Users
import com.agjk.repodepot.view.MainActivity

class MainFragmentAdapter (var userList : MutableList<Users>, mainActivity: MainActivity) : FragmentStateAdapter(mainActivity){


    override fun getItemCount(): Int = userList.size

    override fun createFragment(position: Int): Fragment {
        return userList[position].userFragment
    }

   // fun updateFragmentList(fragmentList: List<Fragment>){
        //this.fragmentList = fragmentList
        //notifyDataSetChanged()
    //}

    fun addFragmentToList(newUser: Users) {
        userList.add(newUser)
        notifyItemInserted(userList.size)
        // update nav drawer with new item?
    }

}