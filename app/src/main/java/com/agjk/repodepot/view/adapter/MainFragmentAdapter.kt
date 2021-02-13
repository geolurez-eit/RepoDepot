package com.agjk.repodepot.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agjk.repodepot.view.MainActivity

class MainFragmentAdapter (var fragmentList: MutableList<Fragment>, mainActivity: MainActivity) : FragmentStateAdapter(mainActivity){

    // TODO: Change everything to meet fragment\ needs


    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

   // fun updateFragmentList(fragmentList: List<Fragment>){
        //this.fragmentList = fragmentList
        //notifyDataSetChanged()
    //}

    fun addFragmentToList(newFrag: Fragment) {
        fragmentList.add(newFrag)
        notifyItemInserted(fragmentList.size)
        // update nav drawer with new item?
    }

}