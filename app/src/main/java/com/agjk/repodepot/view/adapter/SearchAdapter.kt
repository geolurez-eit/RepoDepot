package com.agjk.repodepot.view.adapter

import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.UserSearch

class SearchAdapter(var suggestionList: List<UserSearch.Item>, val delegate: resultClickDelegate)
    : RecyclerView.Adapter<SearchAdapter.SuggestionViewHolder>() {

    interface resultClickDelegate {
        fun displayUserResult()
        // TODO: pass the user login? or the url for all their repos?
    }

    fun updateSuggestionList(newList: List<UserSearch.Item>) {
        suggestionList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder =
        SuggestionViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false))

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val user = suggestionList[position].login ?: ""

        holder.apply {
            result.text = user
            itemView.setOnClickListener{
                // TODO: get user data and display user fragment
//                DepotRepository.getReposForUser(user)
                delegate.displayUserResult()
            }
        }
    }

    override fun getItemCount(): Int = suggestionList.size

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val result: TextView = itemView.findViewById(R.id.search_suggestion)
    }
}