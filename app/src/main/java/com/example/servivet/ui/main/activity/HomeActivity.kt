package com.example.servivet.ui.main.activity

import BottomNavigationHelper
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.servivet.R
import com.example.servivet.databinding.ActivityHomeBinding
import com.example.servivet.ui.base.BaseActivity
import com.example.servivet.ui.main.fragment.HomeFragment
import com.example.servivet.ui.main.fragment.MyServiceFragment
import com.example.servivet.ui.main.view_model.MyServiceViewModel
import com.example.servivet.ui.main.view_model.SharedViewModel
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout


class HomeActivity : BaseActivity(),MyServiceFragment.CallBack1{
    lateinit var navigationBar:BottomNavigationView
    lateinit var view1: View
    lateinit var view2: View
    lateinit var view3: View
    lateinit var view4: View
    lateinit var bottom: LinearLayout
    val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        navigationBar=findViewById(R.id.navigation_bar)
        view1=findViewById(R.id.view1)
        view2=findViewById(R.id.view2)
        view3=findViewById(R.id.view3)
        view4=findViewById(R.id.view4)
        bottom=findViewById(R.id.bottom)
        val navController = this.findNavController(R.id.home_navigation)
        setUpBottomNavMenu(navController)
        ProcessDialog.dismissDialog()

    }

    private fun setUpBottomNavMenu(navController: NavController) {
        val bottomNav = navigationBar
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
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

                }else->
                  bottom.isVisible = false
            }
        }

        if(isProfileShow){

        }

    }

    companion object{
        var isProfileShow=false
        var  CONNECTIVITY_SERVICE: String=""

    }

    override fun callBack() {
        
//        binding.navigationBar.selectedItemId = R.id.profileFragment
    }



}