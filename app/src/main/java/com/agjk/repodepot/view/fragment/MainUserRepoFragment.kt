package com.agjk.repodepot.view.fragment

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
import de.hdodenhof.circleimageview.CircleImageView

class MainUserRepoFragment : Fragment() {

    private lateinit var profilePicture: CircleImageView
    private lateinit var rvUserRepo: RecyclerView
    private lateinit var tvName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvDetail: TextView

    private val repoAdapter = RepoAdapter(mutableListOf())

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
    }

    private fun initalize(view: View) {
        view.apply {
            profilePicture = findViewById(R.id.iv_user_profile)
            rvUserRepo = findViewById(R.id.rv_user_repo)
            tvName = findViewById(R.id.tv_name_main)
            tvUsername = findViewById(R.id.tv_username)
            tvDetail = findViewById(R.id.tv_light_details)
        }
        rvUserRepo.adapter = repoAdapter
    }

    fun updateRepoRecyclerview(repoList: List<Repos>){
        repoAdapter.updateRepo(repoList)
    }


}