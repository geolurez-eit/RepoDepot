package com.agjk.repodepot.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.view.adapter.RepoAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class MainUserRepoFragment(var repo: List<Repos>, val avatarUrl: String, val userName: String, val userBio: String) : Fragment(), RepoAdapter.RepoDelegate {

    private lateinit var profilePicture: CircleImageView
    private lateinit var rvUserRepo: RecyclerView
    private lateinit var tvUsername: TextView
    private lateinit var tvDetail: TextView

    private lateinit var thisContext: Context

    //private var repoAdapter = RepoAdapter(mutableListOf())
    private val repoAdapter = RepoAdapter(repo, this)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repo_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize(view)

        Glide.with(thisContext)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_logo_white_cat)
            .into(profilePicture)

        tvUsername.text = userName
        tvDetail.text = userBio

        rvUserRepo.adapter = repoAdapter
        repoAdapter.updateRepo(repo)
    }

    private fun initalize(view: View) {
        view.apply {
            profilePicture = findViewById(R.id.iv_user_image)
            rvUserRepo = findViewById(R.id.repo_recycler_view)
            tvUsername = findViewById(R.id.repo_user_userName)
            tvDetail = findViewById(R.id.repo_bio)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }

    override fun openDetailFragment(repoName: String, repoUrl: String, repoStartCount: String, repoDescription: String, repoForkCount: String) {
        val  userDetailFragment = UserDetailsFragment(userName, avatarUrl, repoName, repoUrl, repoStartCount, repoDescription, repoForkCount)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_in_bottom,
            R.anim.slide_out_bottom, R.anim.slide_out_bottom)
            .add(R.id.detail_fragment_container, userDetailFragment)
            .addToBackStack(userDetailFragment.tag)
            .commit()
    }

}