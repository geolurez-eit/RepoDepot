package com.agjk.repodepot.view.fragment

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
import androidx.fragment.app.FragmentManager
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

    // Recycler view animation var
    private lateinit var constraintLayout: ConstraintLayout

    //private var repoAdapter = RepoAdapter(mutableListOf())
    private var repoList: List<Repos> = listOf()
    private val repoAdapter = RepoAdapter(repo, parentFragmentManager)


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


        repoAdapter.updateRepo(repo)
        DebugLogger("${repo.size} List size #1")


        // Recycler view animation
        /*constraintLayout.visibility = View.VISIBLE
        val animationFadeScale = AnimationUtils.loadAnimation(this.context, R.anim.fade_scale_repo_recycler)
        constraintLayout.startAnimation(animationFadeScale)*/



    }

    private fun initalize(view: View) {

        view.apply {
            profilePicture = findViewById(R.id.repo_user_profil)
            rvUserRepo = findViewById(R.id.repo_recycler_view)
            tvName = findViewById(R.id.repo_user_name)
            tvUsername = findViewById(R.id.repo_user_userName)
            tvDetail = findViewById(R.id.tv_light_details)

            // recycler container
            //constraintLayout = findViewById(R.id.constraint_container)


        }

    }


  /*  fun updateRepoList(repo: List<Repos>) {
        repoList = repo
        DebugLogger("${repoList.size} List size #2")
        repoAdapter.updateRepo(repoList)
    }
*/

}