package com.example.servivet.ui.main.activity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.servivet.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.WHITE
        setContentView(R.layout.activity_main)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation)
        val backStackEntryCount = navHostFragment?.childFragmentManager?.backStackEntryCount
        if (backStackEntryCount != null) {
            if (backStackEntryCount == 0) {
                this.finishAffinity()
            }
        }
    }
}