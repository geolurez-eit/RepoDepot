package com.agjk.repodepot.view.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.adapter.RepoAdapter
import de.hdodenhof.circleimageview.CircleImageView

class MainUserRepoFragment(var repo: List<Repos>) : Fragment() {

    private lateinit var profilePicture: CircleImageView
    private lateinit var rvUserRepo: RecyclerView
    private lateinit var tvName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvDetail: TextView

    private var repoAdapter = RepoAdapter(mutableListOf())
    private var repoList: List<Repos> = listOf()

    private var dummyRepo: MutableList<Repos> = mutableListOf()

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
        rvUserRepo.adapter = repoAdapter

        DebugLogger("Fragment Debug")



       /* dummyRepo.add(Repos("name", "description", "Kotlin", 7))
        dummyRepo.add(Repos("name2", "description2", "Kotlin", 5))
        dummyRepo.add(Repos("name3", "description3", "Kotlin", 2))
        dummyRepo.add(Repos("name4", "description4", "Kotlin", 6))*/

        repoAdapter.updateRepo(repo)
        DebugLogger("${repoList.size} List size #1")

    }

    private fun initalize(view: View) {
        view.apply {
            profilePicture = findViewById(R.id.iv_user_profile)
            rvUserRepo = findViewById(R.id.rv_user_repo)
            tvName = findViewById(R.id.tv_name_main)
            tvUsername = findViewById(R.id.tv_username)
            tvDetail = findViewById(R.id.tv_light_details)
        }

    }


    /*fun updateRepoList(repo: List<Repos>) {
        repoList = repo
        DebugLogger("${repoList.size} List size #2")
        repoAdapter.updateRepo(repoList)
    }*/


}