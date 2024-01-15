package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.CommonUtils

class SettingsViewModel:BaseViewModel() {
    inner class ClickAction(var frgmentActivity: Activity){
        fun backbtn(view:View){
            view.findNavController().popBackStack()
        }
        fun gotoMyWallet(view: View){
            view.findNavController().navigate(R.id.action_settingsFragment_to_myWalletFragment)
        }
        fun goLogout(view: View){
            CommonUtils.customalertdialog(frgmentActivity,frgmentActivity.getString(R.string.are_you_sure_you_want_to_logout),1)
        }


    }
}