package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.ui.base.BaseViewModel

class ChooseLanguageViewModel:BaseViewModel()
{
    var english=MutableLiveData(true)
    var swahii=MutableLiveData(false)
    var zulu=MutableLiveData(false)
    inner class ClickAction{
        fun englishClick(view:View){
            english.postValue(true)
            swahii.postValue(false)
            zulu.postValue(false)
        }

        fun swahiiClick(view:View){
            swahii.postValue(true)
            english.postValue(false)
            zulu.postValue(false)

        }
        fun zuluClick(view:View){
            zulu.postValue(true)
            english.postValue(false)
            swahii.postValue(false)
        }

        fun continueBtn(view: View){
            if(SplashViewModel.isLogout==true){
                view.findNavController().navigate(R.id.action_choosePreferredLanguageFragment_to_loginFragment)
            }else{
              //  view.findNavController().navigate(R.id.action_choosePreferredLanguageFragment_to_loginFragment)

                view.findNavController().navigate(R.id.action_choosePreferredLanguageFragment_to_onboardingFragment)
            }
        }
    }

}