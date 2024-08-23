package com.example.servivet.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseActivity
import com.example.servivet.ui.main.fragment.MyServiceFragment
import com.example.servivet.utils.Constants.COME_FROM
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity(), MyServiceFragment.CallBack1 {
    private lateinit var navigationBar: BottomNavigationView
    lateinit var view1: View
    lateinit var view2: View
    private lateinit var view3: View
    private lateinit var view4: View
    private lateinit var bottom: LinearLayout
    private var isProfileVisited: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        navigationBar = findViewById(R.id.navigation_bar)
        view1 = findViewById(R.id.view1)
        view2 = findViewById(R.id.view2)
        view3 = findViewById(R.id.view3)
        view4 = findViewById(R.id.view4)
        bottom = findViewById(R.id.bottom)
        val navController = this.findNavController(R.id.home_navigation)
        setUpBottomNavMenu(navController)
        ProcessDialog.dismissDialog()
        val notificationData = intent?.getStringExtra("notificationData")
        if (notificationData != null) {
            Session.saveNotificationData(notificationData)
        } else {
            Session.deleteNotificationData()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val notificationData = intent?.getStringExtra("notificationData")

        if (notificationData != null) {
            Session.saveNotificationData(notificationData)
        } else {
            Session.deleteNotificationData()
        }
    }

    private fun setUpBottomNavMenu(navController: NavController) {
        val bottomNav = navigationBar
        if (intent?.getStringExtra(COME_FROM)?.isNotEmpty() == true && !isProfileVisited) {
            bottomNav.selectedItemId = R.id.profileFragment
            navController.navigate(
                R.id.profileFragment, bundleOf(
                    COME_FROM to "home"
                )
            )
            this.intent?.removeExtra(COME_FROM)
            this.intent = null
            isProfileVisited = true
        }
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }

                R.id.bookingsFragment -> {
                    navController.navigate(R.id.bookingsFragment)
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }

                R.id.chatFragment -> {
                    navController.navigate(R.id.chatFragment)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    bottom.visibility = View.VISIBLE
                    view1.visibility = View.VISIBLE
                    view2.visibility = View.INVISIBLE
                    view3.visibility = View.INVISIBLE
                    view4.visibility = View.INVISIBLE
                }

                R.id.chatFragment -> {
                    bottom.visibility = View.VISIBLE
                    view1.visibility = View.INVISIBLE
                    view2.visibility = View.INVISIBLE
                    view3.visibility = View.VISIBLE
                    view4.visibility = View.INVISIBLE
                }

                R.id.profileFragment -> {
                    bottom.visibility = View.VISIBLE
                    view1.visibility = View.INVISIBLE
                    view2.visibility = View.INVISIBLE
                    view3.visibility = View.INVISIBLE
                    view4.visibility = View.VISIBLE
                }

                R.id.bookingsFragment -> {
                    bottom.visibility = View.VISIBLE
                    view1.visibility = View.INVISIBLE
                    view2.visibility = View.VISIBLE
                    view3.visibility = View.INVISIBLE
                    view4.visibility = View.INVISIBLE
                }

                else ->
                    bottom.isVisible = false
            }
        }
    }

    companion object {
        var isProfileShow = false
        var CONNECTIVITY_SERVICE: String = ""
    }

    override fun callBack() {
//        binding.navigationBar.selectedItemId = R.id.profileFragment
    }
}