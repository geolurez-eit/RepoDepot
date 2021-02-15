package com.agjk.repodepot.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.UserSearch
import com.agjk.repodepot.view.adapter.SearchAdapter
import com.agjk.repodepot.viewmodel.RepoViewModel

class SearchResultsFragment : Fragment(), SearchAdapter.resultClickDelegate {

    private lateinit var resultsRecyclerView: RecyclerView
    private val searchAdapter = SearchAdapter(listOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.search_results_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultsRecyclerView = view.findViewById(R.id.search_results_recycler)
        resultsRecyclerView.adapter = searchAdapter

        DepotRepository.userSearchLiveData.observe(viewLifecycleOwner, {
            searchAdapter.updateSuggestionList(it)
        })
    }

    override fun displayUserResult() {
        // TODO: close fragment, and display found user fragment
    }
}