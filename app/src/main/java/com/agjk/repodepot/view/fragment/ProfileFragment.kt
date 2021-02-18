package com.agjk.repodepot.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.agjk.repodepot.R
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.viewmodel.RepoViewModel
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class ProfileFragment(val username: String) : Fragment(){

    private lateinit var tvName: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvBio: TextView
    private lateinit var ivUserImage: ImageView
    private lateinit var tvFollowers: TextView
    private lateinit var tvFollowing: TextView
    private lateinit var tvHtmlUrl: TextView
    private lateinit var tvNumRepo: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnExit: Button

    private lateinit var thisContext: Context

    private val repoViewModel: RepoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstInit(view)
        getProfileData()

        btnAdd.setOnClickListener {
            repoViewModel.addUserToList(username)
        }
        btnExit.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        tvHtmlUrl.setOnClickListener {
            gotoUrl(tvHtmlUrl.text.toString())
        }

    }

    private fun gotoUrl(s: String) {
        val uri: Uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun firstInit(view: View) {
        view.apply {
            tvName = findViewById(R.id.tv_name)
            tvUserName = findViewById(R.id.tv_user_name)
            tvBio = findViewById(R.id.tv_bio)
            ivUserImage = findViewById(R.id.iv_user_image)
            tvFollowers = findViewById(R.id.tv_followers)
            tvFollowing = findViewById(R.id.tv_following)
            tvHtmlUrl = findViewById(R.id.tv_html_url)
            tvNumRepo = findViewById(R.id.tv_num_repo)
            btnAdd = findViewById(R.id.btn_add_profile)
            btnExit = findViewById(R.id.btn_back)
        }

        DebugLogger("HERHEHREHREHEHEHREHR")
    }

    private fun getProfileData() {
        repoViewModel.getProfile(username).observe(viewLifecycleOwner, {
            Glide.with(thisContext)
                .load(it.avatar_url)
                .placeholder(R.drawable.portrait)
                .into(ivUserImage)
            tvName.text = it.name ?: "No Name"
            tvUserName.text = it.login ?: "No Username"
            tvBio.text = it.bio.toString()
            tvFollowers.text = (it.followers?: 0).toString()
            tvFollowing.text = (it.following?: 0).toString()
            tvHtmlUrl.text = it.html_url ?: "No HTML"
            tvNumRepo.text = (it.public_repos?: 0).toString()

            DebugLogger("Result -------------------------------> $it")
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }
}
