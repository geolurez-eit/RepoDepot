package com.agjk.repodepot.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Preferences
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.agjk.repodepot.viewmodel.RepoViewModel
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenFragment : Fragment() {

    private lateinit var thisContext: Context
    private var inSignOut = true

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var provider = OAuthProvider.newBuilder("github.com")

    private val SPLASH_TIME: Long = 500

    private lateinit var splashImg: ImageView
    private lateinit var lottieAnimation: LottieAnimationView

    private lateinit var sloganTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var loadingBar: ProgressBar
    private lateinit var loginbtn: MaterialButton
    private lateinit var websiteText: TextView

    //    private lateinit var loginText: TextView
    private lateinit var progressBar: ProgressBar

    private val repoViewModel: RepoViewModel by activityViewModels()

    private var mainAnimEnded = false
    private var logoAnimEnded = false
    private var isMainFinishedLoading = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.updated_splash_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sloganTextView = view.findViewById(R.id.slogan)
        logoImageView = view.findViewById(R.id.RepotDepotLogo)
        loginbtn = view.findViewById(R.id.sign_in_Button_main)
//        loginText = view.findViewById(R.id.login_text)
        progressBar = view.findViewById(R.id.progress_bar)
        websiteText = view.findViewById(R.id.website)

        // Lottie  Screen vars
        splashImg = view.findViewById(R.id.img)
        lottieAnimation = view.findViewById(R.id.lottieAnimation)

        inSignOut = arguments?.getBoolean(getString(R.string.SIGNOUT_KEY), false) ?: false

        if (!inSignOut) {
            splashImg.visibility = View.VISIBLE
            lottieAnimation.visibility = View.VISIBLE

            splashImg.animate().translationY(-3000F).setDuration(1000).setStartDelay(1500)
                .alpha(0f)
            lottieAnimation.animate().translationY(1400F).setDuration(1000).setStartDelay(1500)
                .withEndAction { mainAnimEnded = true }
        } else {
            splashImg.visibility = View.GONE
            lottieAnimation.visibility = View.GONE
            mainAnimEnded = true
        }

        //// OAUTH
        // Target specific email with login hint.
//        provider.addCustomParameter("login", "george.perez@enhanceit.us")

        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        val scopes: List<String> = listOf("user", "repo:status")
        provider.scopes = scopes
        ////

        // Logo animation
        var animFadeIn = AnimationUtils.loadAnimation(thisContext, R.anim.medium_fade_in_delay)
        if (inSignOut) {
            animFadeIn = AnimationUtils.loadAnimation(thisContext, R.anim.fast_fade_in)
        }

        logoImageView.visibility = View.VISIBLE
        logoImageView.startAnimation(animFadeIn)

        // Slogan animation
        sloganTextView.visibility = View.VISIBLE
        sloganTextView.startAnimation(animFadeIn)

        websiteText.visibility = View.VISIBLE
        websiteText.startAnimation(animFadeIn)

        repoViewModel.isMainLoaded.observe(viewLifecycleOwner, {
            isMainFinishedLoading = it
        })

        // Only show login buttons if no 'currentUser'
        firebaseAuth.currentUser?.let {
            closeSplashToMainActivity()
        } ?: {
            // Animation for the login button
            loginbtn.visibility = View.VISIBLE
            loginbtn.startAnimation(animFadeIn)

            // OAUTH GO!
            loginbtn.setOnClickListener {
                //Check if login is pending, sign in if not
                checkPendingResult()
            }
        }()
    }

    private fun closeSplashToMainActivity() {
        //(thisContext as MainActivity).saveUsername(username)

        val animFadeOut = AnimationUtils.loadAnimation(thisContext, R.anim.fast_fade_out)
        loginbtn.visibility = View.INVISIBLE
        loginbtn.startAnimation(animFadeOut)

//        loginText.visibility = View.INVISIBLE
//        loginText.startAnimation(animFadeOut)

        val animFadeIn = AnimationUtils.loadAnimation(thisContext, R.anim.fast_fade_in)
        progressBar.visibility = View.VISIBLE
        progressBar.startAnimation(animFadeIn)

        // TODO: change artificial loading delay
        lifecycleScope.launch(context = Dispatchers.Default) {
//            Log.d("TAG_Q", "start coroutine")
//            Log.d("TAG_Q", "main anim ended -> $mainAnimEnded, logo anim ended -> $logoAnimEnded")

            // let animations play out first
            while (!mainAnimEnded || !logoAnimEnded) {
//                Log.d("TAG_Q", "in anim loop check")
                logoImageView.animation?.let { logoAnimEnded = it.hasEnded() }
                    ?: { logoAnimEnded = true }()
            }

//            Log.d("TAG_Q", "post anim loop check")
            // start loading main activity data
            (context as MainActivity).loadMainInBackground()

            // then let progress bar continue until data is loaded
            while (!isMainFinishedLoading) { /* no-op */ }
//            Log.d("TAG_Q", "main finished loading")

            // then close the splash
            (context as MainActivity).closeSplash()
        }
    }

    private fun startSignIn() {

        firebaseAuth
            .startActivityForSignInWithProvider( /* activity= */(context as MainActivity),
                provider.build()
            )
            .addOnSuccessListener {
                //Update firebase displayname to match github username
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(it.additionalUserInfo?.username.toString())
                    .build()
                firebaseAuth.currentUser?.updateProfile(profileUpdate)
                //Store github token in firebase
                repoViewModel.addUserPreferences(Preferences(gitHubAccessToken = (it.credential as OAuthCredential).accessToken))
                closeSplashToMainActivity()
            }
            .addOnFailureListener {
                // Handle failure.
                DebugLogger(it.localizedMessage)
            }
    }

    private fun checkPendingResult() {
        val pendingResultTask: Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener {
                        //Update firebase displayname to match github username
                        val profileUpdate = UserProfileChangeRequest.Builder()
                            .setDisplayName(it.additionalUserInfo?.username.toString())
                            .build()
                        firebaseAuth.currentUser?.updateProfile(profileUpdate)
                            //Store github token in firebase
                        repoViewModel.addUserPreferences(Preferences(gitHubAccessToken = (it.credential as OAuthCredential).accessToken))

                        closeSplashToMainActivity()
                    })
                .addOnFailureListener {
                    // Handle failure.
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            startSignIn()
        }
    }
}