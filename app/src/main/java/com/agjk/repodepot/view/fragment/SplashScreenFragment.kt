package com.agjk.repodepot.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.agjk.repodepot.R
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.log

class SplashScreenFragment : Fragment() {

    private lateinit var thisContext: Context

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var provider = OAuthProvider.newBuilder("github.com")

    private val SPLASH_TIME: Long = 500

    private lateinit var splashImg : ImageView
    private lateinit var lottieAnimation : LottieAnimationView


    private lateinit var sloganTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var loginbtn: MaterialButton
//    private lateinit var loginText: TextView
    private lateinit var progressBar: ProgressBar

    private var tokenSaved: String = ""

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

        // Lottie  Screen vars
        splashImg = view.findViewById(R.id.img)
        lottieAnimation = view.findViewById(R.id.lottieAnimation)
        splashImg.animate().translationY(-2400F).setDuration(1000).setStartDelay(2000)
        lottieAnimation.animate().translationY(1400F).setDuration(1000).setStartDelay(2000)


        //// OAUTH
        // Target specific email with login hint.
//        provider.addCustomParameter("login", "george.perez@enhanceit.us")

        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        val scopes: List<String> = listOf("user", "repo:status")
        provider.scopes = scopes
        ////

        // Logo animation
        logoImageView.visibility = View.VISIBLE
        val animationRotate = AnimationUtils.loadAnimation(thisContext, R.anim.text_fade_in)
        logoImageView.startAnimation(animationRotate)

        // Slogan animation
        sloganTextView.visibility = View.VISIBLE
        val animationZoomIn = AnimationUtils.loadAnimation(thisContext, R.anim.text_fade_in)
        sloganTextView.startAnimation(animationZoomIn)

//        // Login text animation
//        loginText.visibility = View.VISIBLE
//        val animationFadeIn = AnimationUtils.loadAnimation(thisContext, R.anim.text_fade_in)
//        loginText.startAnimation(animationFadeIn)



        // Only show login buttons if no 'currentUser'
        firebaseAuth.currentUser?.let {
            closeSplashToMainActivity()
        } ?: {
            // Animation for the login button
            loginbtn.visibility = View.VISIBLE
            val animationSlideDown = AnimationUtils.loadAnimation(thisContext, R.anim.text_fade_in)
            loginbtn.startAnimation(animationSlideDown)

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
            delay(SPLASH_TIME)
            logoImageView.animation?.let {
                while (!it.hasEnded()) { /* no-op */ }
            }
            (context as MainActivity).closeSplash()
        }
    }

    private fun startSignIn() {

        firebaseAuth
            .startActivityForSignInWithProvider( /* activity= */(context as MainActivity), provider.build())
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // authResult.getCredential().getAccessToken().
                DebugLogger(it.credential?.provider.toString())

                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(it.additionalUserInfo?.username.toString())
                    .build()
                firebaseAuth.currentUser?.updateProfile(profileUpdate)
                tokenSaved = (it.credential as OAuthCredential).accessToken

                DebugLogger("Token Log : -----> $tokenSaved")

                (thisContext as MainActivity).saveToken(tokenSaved)

                Log.d("TAG_A", "sign in success")
                closeSplashToMainActivity()
            }
            .addOnFailureListener {
                // Handle failure.
                DebugLogger(it.localizedMessage)
                Log.d("TAG_A", "sign in failure -> ${it.localizedMessage}")
            }
    }

    private fun checkPendingResult() {
        val pendingResultTask: Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener {
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().

                        Log.d("TAG_A", "pending result success")
                        closeSplashToMainActivity()
                    })
                .addOnFailureListener {
                    // Handle failure.
                    Log.d("TAG_A", "pending result failure")
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            Log.d("TAG_A", "pending result is null, start sign in")
            startSignIn()
        }
    }
}