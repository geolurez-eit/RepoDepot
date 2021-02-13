package com.agjk.repodepot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
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

    private val SPLASH_TIME: Long = 2000

    private lateinit var sloganTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var loginbtn: FloatingActionButton
    private lateinit var loginText: TextView

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

        // Bounce animation for the slogan (textview)
        val animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_text)
        logoImageView.startAnimation(animationRotate)

        // Bounce animation for the slogan (textview)
        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in_text)
        sloganTextView.startAnimation(animationFadeIn)

        // only run button animation if no use logged int
        firebaseAuth.currentUser?.let {
            loginbtn.visibility = View.INVISIBLE
            loginText.visibility = View.INVISIBLE
        } ?: {
            // Animation for the login button
            val animationSlideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down_text)
            loginbtn.startAnimation(animationSlideDown)

            loginbtn.setOnClickListener {
                //Check if login is pending, sign in if not
                checkPendingResult()
            }
        }()
    }

    override fun onStart() {
        super.onStart()

        firebaseAuth.currentUser?.let {
            GlobalScope.launch {
                delay(SPLASH_TIME)
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).also { i ->
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
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
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
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