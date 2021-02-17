package com.agjk.repodepot.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Commits
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


    private lateinit var detailList: List<Commits>

    //private var repoAdapter = RepoAdapter(mutableListOf())
    private var repoList: List<Repos> = listOf()
  //  private val repoAdapter = RepoAdapter(repo, this)


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

        //rvUserRepo.adapter = repoAdapter

        DebugLogger("Fragment Debug")


        detailList = listOf(
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4"),
            Commits("", "JohnClaude","Updated the user fragment", "475jfr4")
        )


       // repoAdapter.updateRepo(repo)
        DebugLogger("${repo.size} List size #1")




    }

    private fun initalize(view: View) {

        view.apply {
            profilePicture = findViewById(R.id.details_user_profil)
            rvUserRepo = findViewById(R.id.repo_recycler_view)
            tvName = findViewById(R.id.repo_user_name)
            tvUsername = findViewById(R.id.repo_user_userName)
            tvDetail = findViewById(R.id.tv_light_details)



        }

    }

/*    override fun returntoFrag() {

        val fragment = UserDetailsFragment(detailList)
        parentFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.fast_fade_out
        )
            .add(R.id.repo_container, fragment )
            .addToBackStack(fragment.tag)
            .commit()

    }*/




    /*  fun updateRepoList(repo: List<Repos>) {
          repoList = repo
          DebugLogger("${repoList.size} List size #2")
          repoAdapter.updateRepo(repoList)
      }
  */

}