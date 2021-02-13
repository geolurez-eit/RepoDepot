package com.agjk.repodepot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SplashScreen : AppCompatActivity() {

    private val SPLASH_TIME: Long = 3000

    private lateinit var sloganTextView: TextView
    private lateinit var logoImageView: ImageView
    private lateinit var loginbtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

    /*    Handler().postDelayed({
            //startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        , SPLASH_TIME)*/


        sloganTextView= findViewById(R.id.slogan)
        logoImageView = findViewById(R.id.RepotDepotLogo)
        loginbtn = findViewById(R.id.sign_in_Button_main)

        // Bounce animation for the slogan (textview)
        logoImageView.visibility = View.VISIBLE
        val animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_text)
        logoImageView.startAnimation(animationRotate)

        // Bounce animation for the slogan (textview)
        sloganTextView.visibility = View.VISIBLE
        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in_text)
        sloganTextView.startAnimation(animationFadeIn)

        // Animation for the login button

        loginbtn.visibility = View.VISIBLE
        val animationSlideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down_text)
        loginbtn.startAnimation(animationSlideDown)



    }
}