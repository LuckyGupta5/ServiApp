package com.example.servivet.ui.main.activity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.servivet.R
import com.example.servivet.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.WHITE
        setContentView(R.layout.activity_main)

        if (Constants.SWITCH_ACC) {
            Constants.SWITCH_ACC = false

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navigation) as NavHostFragment

            val navController: NavController = navHostFragment.navController
            val currentGraph = navController.navInflater.inflate(R.navigation.login_nav)
            currentGraph.setStartDestination(R.id.business_Verification_Fragment)
            navController.graph = currentGraph
        }
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