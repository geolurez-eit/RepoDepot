package com.agjk.repodepot.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agjk.repodepot.model.data.Users
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity

class MainFragmentAdapter (var userList : List<Users>, mainActivity: MainActivity) : FragmentStateAdapter(mainActivity){


    override fun getItemCount():Int {
        //DebugLogger("USERLIST SIZE ---> ${userList.size}")
        return userList.size
    }

    override fun createFragment(position: Int): Fragment {
        return userList[position].userFragment
    }

   // fun updateFragmentList(fragmentList: List<Fragment>){
        //this.fragmentList = fragmentList
        //notifyDataSetChanged()
    //}

    fun addFragmentToList(newUserList: List<Users>) {
        //DebugLogger("MainFragmentAdapter.addFragmentToList: "+newUserList.size.toString())
        userList = newUserList
        notifyDataSetChanged()
        // update nav drawer with new item?
    }

}