package com.example.elizabethcakeshn

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.elizabethcakeshn.FireStore
import com.example.elizabethcakeshn.LoginActivity
import com.example.elizabethcakeshn.R

import kotlinx.android.synthetic.main.activity_splash.*

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        Handler().postDelayed(
            {


                val currentUserID = FireStore().getCurrentUserID()

                if (currentUserID.isNotEmpty()) {

                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                } else {

                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()
            },
            2500
        )
    }
}