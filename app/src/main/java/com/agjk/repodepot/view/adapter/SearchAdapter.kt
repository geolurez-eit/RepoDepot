package com.agjk.repodepot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.UserSearch

class SearchAdapter(var suggestionList: List<UserSearch.Item>) : RecyclerView.Adapter<SearchAdapter.SuggestionViewHolder>() {

    fun updateSuggestionList(newList: List<UserSearch.Item>) {
        suggestionList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder =
        SuggestionViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false))

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.suggestion.text = suggestionList[position].login ?: ""
    }

    override fun getItemCount(): Int = suggestionList.size

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val suggestion: TextView = itemView.findViewById(R.id.search_suggestion)
    }
}