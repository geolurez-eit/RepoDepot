package com.agjk.repodepot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.MainActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var provider = OAuthProvider.newBuilder("github.com")

    private val SPLASH_TIME: Long = 1000

    private lateinit var sloganTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var loginbtn: FloatingActionButton
    private lateinit var loginText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        // Target specific email with login hint.
        provider.addCustomParameter("login", "george.perez@enhanceit.us")
        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        val scopes: List<String> = listOf("user", "repo:status")
        provider.scopes = scopes

        sloganTextView= findViewById(R.id.slogan)
        logoImageView = findViewById(R.id.RepotDepotLogo)
        loginbtn = findViewById(R.id.sign_in_Button_main)
        loginText = findViewById(R.id.login_text)
        progressBar = findViewById(R.id.progress_bar)

        // Bounce animation for the slogan (textview)
        logoImageView.visibility = View.VISIBLE
        val animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_text)
        logoImageView.startAnimation(animationRotate)

        // Bounce animation for the slogan (textview)
        sloganTextView.visibility = View.VISIBLE
        val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in_text)
        sloganTextView.startAnimation(animationZoomIn)

        // only run button animation if no user logged in
        firebaseAuth.currentUser?.let {
            startMainActivity()
        } ?: {
            progressBar.visibility = View.INVISIBLE

            // Animation for the login button
            loginbtn.visibility = View.VISIBLE
            val animationSlideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down_text)
            loginbtn.startAnimation(animationSlideDown)

            loginbtn.setOnClickListener {
                //Check if login is pending, sign in if not
                checkPendingResult()
            }
        }()
    }

    private fun startMainActivity() {
        Log.d("TAG_A", "starting main activity")

        val animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fast_fade_out)
        loginbtn.visibility = View.INVISIBLE
        loginbtn.startAnimation(animFadeOut)
        loginText.visibility = View.INVISIBLE
        loginText.startAnimation(animFadeOut)

        progressBar.visibility = View.VISIBLE
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in)
        progressBar.startAnimation(animFadeIn)

        GlobalScope.launch {
            delay(SPLASH_TIME)
            runOnUiThread {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    private fun startSignIn() {
        firebaseAuth
            .startActivityForSignInWithProvider( /* activity= */this, provider.build())
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // authResult.getCredential().getAccessToken().
                DebugLogger(it.credential?.provider.toString())
                Log.d("TAG_A", "sign in success")
                startMainActivity()
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
                        startMainActivity()
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