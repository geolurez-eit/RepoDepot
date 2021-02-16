package com.agjk.repodepot.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Commits
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.view.adapter.DetailsAdapter
import com.agjk.repodepot.view.adapter.RepoAdapter
import de.hdodenhof.circleimageview.CircleImageView


// This fragment will show  details about repos
class UserDetailsFragment(var commit: List<Commits>) : Fragment() {



    private lateinit var profilePicture: CircleImageView
    private lateinit var detailsRecycler: RecyclerView
    private lateinit var repoName : TextView
    private lateinit var repoDescription : TextView
    private lateinit var repoLink : TextView
    private lateinit var ratingCount : TextView
    private lateinit var forksCount : TextView



    private var commitList: List<Commits> = listOf()
    private val detailsAdapter = DetailsAdapter(commit)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initalize(view)




    }


    private fun initalize(view: View) {

        view.apply {
            detailsRecycler=findViewById(R.id.rv_user_details)
            profilePicture = findViewById(R.id.iv_user_profile_details)
            repoName = findViewById(R.id.tv_repoName_details)
            repoDescription = findViewById(R.id.tv_repo_description)
            repoLink = findViewById(R.id.tv_repo_link)
            ratingCount=findViewById(R.id.tv_rating_count)
            forksCount=findViewById(R.id.tv_forks_count)
        }
    }


}