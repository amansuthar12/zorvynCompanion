package com.zorvyn.zorvyncompanion

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.MainScope
import kotlin.math.log

class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        val logo  = findViewById<ImageView>(R.id.logo)
        val textView = findViewById<TextView>(R.id.appName)
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_scale)
        val textAnim = AnimationUtils.loadAnimation(this, R.anim.text_fade)

        logo.startAnimation(textAnim)
        textAnim.startOffset = 200
        logo.startAnimation(logoAnim)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}