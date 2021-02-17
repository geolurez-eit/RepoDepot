package com.agjk.repodepot.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.agjk.repodepot.R
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


// This fragment will show  details about repos
class UserDetailsFragment(val avatarUrl: String,
                          val repoName: String,
                          val userBio: String,
                          val repoUrl: String,
                          val repoStartCount: String) : Fragment() {

    private lateinit var ivAvatarUrl: CircleImageView
    private lateinit var tvRepoName: TextView
    private lateinit var tvRepoBio: TextView
    private lateinit var tvRepoLink: TextView
    private lateinit var tvStarCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            ivAvatarUrl = findViewById(R.id.iv_user_profile_details)
            tvRepoName = findViewById(R.id.tv_repoName_details)
            tvRepoBio = findViewById(R.id.tv_repo_description)
            tvRepoLink = findViewById(R.id.tv_repo_link)
            tvStarCount = findViewById(R.id.tv_rating_count)


            Glide.with(view.context)
                .load(avatarUrl)
                .placeholder(R.drawable.portrait)
                .into(ivAvatarUrl)

            tvRepoName.text = repoName
            tvRepoBio.text = userBio
            tvRepoLink.text = repoUrl
            tvStarCount.text = repoStartCount
        }
    }

}