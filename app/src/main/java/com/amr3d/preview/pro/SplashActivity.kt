package com.amr3d.preview.pro

import android.animation.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fullscreen — إخفاء nav bar و status bar تماماً
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // لو التطبيق اتفتح من واتساب أو مدير ملفات
        if (intent?.action == Intent.ACTION_VIEW && intent?.data != null) {
            val fileUri = intent.data
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = fileUri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(mainIntent)
            finish()
            return
        }

        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.splashLogo)
        val titleText = findViewById<TextView>(R.id.splashTitle)
        val devText = findViewById<TextView>(R.id.splashDev)
        val progressBar = findViewById<ProgressBar>(R.id.splashProgress)
        val percentText = findViewById<TextView>(R.id.splashPercent)
        val glowLine = findViewById<View>(R.id.splashGlowLine)
        val wireframeBg = findViewById<WireframeBackgroundView>(R.id.wireframeBg)

        // القيم الابتدائية
        logo.alpha = 0f
        logo.scaleX = 1.5f  // يبدأ كبير
        logo.scaleY = 1.5f  // يبدأ كبير
        logo.translationY = 0f // مش محتاجين حركة لفوق هنا
        titleText.alpha = 0f
        titleText.translationY = 40f
        devText.alpha = 0f
        devText.translationY = 30f
        progressBar.alpha = 0f
        percentText.alpha = 0f
        glowLine.scaleX = 0f

        wireframeBg.fadeIn()

        // اللوجو - Cinematic Zoom Out
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f).setDuration(600),
                ObjectAnimator.ofFloat(logo, "scaleX", 1.5f, 1f).setDuration(1000),
                ObjectAnimator.ofFloat(logo, "scaleY", 1.5f, 1f).setDuration(1000)
            )
            interpolator = DecelerateInterpolator(2f) // حركة ناعمة في الآخر
            startDelay = 300
        }.start()

        // الخط النيون
        ObjectAnimator.ofFloat(glowLine, "scaleX", 0f, 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 1100
        }.start()

        // النصوص
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(titleText, "alpha", 0f, 1f).setDuration(600),
                ObjectAnimator.ofFloat(titleText, "translationY", 40f, 0f).setDuration(600)
            )
            interpolator = OvershootInterpolator(1.2f)
            startDelay = 1300
        }.start()

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(devText, "alpha", 0f, 1f).setDuration(600),
                ObjectAnimator.ofFloat(devText, "translationY", 30f, 0f).setDuration(600)
            )
            interpolator = DecelerateInterpolator()
            startDelay = 1700
        }.start()

        // Progress Bar + النسبة المئوية
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f).setDuration(400),
                ObjectAnimator.ofFloat(percentText, "alpha", 0f, 1f).setDuration(400)
            )
            startDelay = 2000
        }.start()

        ValueAnimator.ofInt(0, 100).apply {
            duration = SPLASH_DURATION - 600
            startDelay = 2000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                progressBar.progress = value
                percentText.text = "$value%"
            }
        }.start()

        // الخروج
        Handler(Looper.getMainLooper()).postDelayed({
            val rootView = findViewById<View>(android.R.id.content)
            ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f).apply {
                duration = 400
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 400)
        }, SPLASH_DURATION)
    }
}
