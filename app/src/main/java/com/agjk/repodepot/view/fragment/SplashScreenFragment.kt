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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.agjk.repodepot.R
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.log

class SplashScreenFragment : Fragment() {

    private lateinit var thisContext: Context

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var provider = OAuthProvider.newBuilder("github.com")

    private val SPLASH_TIME: Long = 500

    private lateinit var sloganTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var loginbtn: MaterialButton
//    private lateinit var loginText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.splash_screen_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sloganTextView = view.findViewById(R.id.slogan)
        logoImageView = view.findViewById(R.id.RepotDepotLogo)
        loginbtn = view.findViewById(R.id.sign_in_Button_main)
//        loginText = view.findViewById(R.id.login_text)
        progressBar = view.findViewById(R.id.progress_bar)

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
        val animationRotate = AnimationUtils.loadAnimation(thisContext, R.anim.rotate_text)
        logoImageView.startAnimation(animationRotate)

        // Slogan animation
        sloganTextView.visibility = View.VISIBLE
        val animationZoomIn = AnimationUtils.loadAnimation(thisContext, R.anim.zoom_in_text)
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
            val animationSlideDown = AnimationUtils.loadAnimation(thisContext, R.anim.slide_down_text)
            loginbtn.startAnimation(animationSlideDown)

            // OAUTH GO!
            loginbtn.setOnClickListener {
                //Check if login is pending, sign in if not
                checkPendingResult()
            }
        }()
    }
    
    private fun closeSplashToMainActivity() {
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