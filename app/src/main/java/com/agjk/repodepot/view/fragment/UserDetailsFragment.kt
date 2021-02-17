package com.agjk.repodepot.view.fragment

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
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
                          val repoUrl: String,
                          val repoStartCount: String,
                          val repoDescription: String,
                          val repoForkCount: String) : Fragment() {

    private lateinit var ivAvatarUrl: CircleImageView
    private lateinit var tvRepoName: TextView
    private lateinit var tvRepoBio: TextView
    private lateinit var tvRepoLink: TextView
    private lateinit var tvStarCount: TextView
    private lateinit var tvForkCount: TextView

    private val stringSize = 60

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
            tvForkCount = findViewById(R.id.tv_forks_count)

            initRepoDetails(view)
        }

        //val italicSpan= UnderlineSpan()

        tvRepoLink.setOnClickListener {
            gotoUrl(repoUrl)
        }

        tvRepoBio.movementMethod = ScrollingMovementMethod()



    }

    private fun initRepoDetails(view: View) {
        Glide.with(view.context)
            .load(avatarUrl)
            .placeholder(R.drawable.portrait)
            .into(ivAvatarUrl)

        tvRepoName.text = repoName
        if (repoDescription.length == 0) {
            tvRepoBio.text = "No Description"
        } else {
            tvRepoBio.text = repoDescription
        }

        if (repoUrl.length > stringSize) {
            val s = "${repoUrl.substring(0, stringSize)}\n${repoUrl.substring(stringSize)}"
            tvRepoLink.text = s
        } else {
            tvRepoLink.text = repoUrl
        }

        tvStarCount.text = repoStartCount
        tvForkCount.text = repoForkCount
    }

    private fun gotoUrl(s: String) {
        val uri: Uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

}