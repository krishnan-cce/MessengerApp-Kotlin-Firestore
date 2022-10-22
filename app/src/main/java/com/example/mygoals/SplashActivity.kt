package com.example.mygoals

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.TextView


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val appNameText: TextView = findViewById(R.id.tv_app_name)
        val appNameFont: Typeface = Typeface.createFromAsset(assets,"LittleLordFontleroyNF.ttf")
        appNameText.typeface = appNameFont

        Handler(Looper.getMainLooper()).postDelayed(
            {

                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            },
            2500
        )

    }
}