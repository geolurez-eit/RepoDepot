package com.agjk.repodepot.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Commits
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.agjk.repodepot.view.adapter.RepoAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class MainUserRepoFragment(var repo: List<Repos>, var avatarUrl: String, var userName: String, var userBio: String) : Fragment(), RepoAdapter.Delegate {

    private lateinit var profilePicture: CircleImageView
    private lateinit var rvUserRepo: RecyclerView
    private lateinit var tvName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvDetail: TextView


    private lateinit var detailList: List<Commits>
    private lateinit var thisContext: Context

    //private var repoAdapter = RepoAdapter(mutableListOf())
    private val repoAdapter = RepoAdapter(repo, this )

    private lateinit var mcontext: Context


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
            .placeholder(R.drawable.portrait)
            .into(profilePicture)

        tvUsername.text = userName
        tvDetail.text = userBio

        rvUserRepo.adapter = repoAdapter
        repoAdapter.updateRepo(repo)





    }

    private fun initalize(view: View) {

        view.apply {
            profilePicture = findViewById(R.id.repo_user_profil)
            rvUserRepo = findViewById(R.id.repo_recycler_view)
            tvUsername = findViewById(R.id.repo_user_userName)
            tvDetail = findViewById(R.id.repo_bio)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }

    override fun passToDetails() {

        val userDetailsFragment = UserDetailsFragment(detailList)

        (activity as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.repo_container, userDetailsFragment )
            .commit()

    }

}