package com.example.servivet.ui.main.view_model

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.model.language_model.LanguageModel
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.activity.MainActivity
import com.example.servivet.utils.Session
import com.example.servivet.utils.setLocal
import io.agora.base.internal.video.EglBase10.Context

class ChooseLanguageViewModel:BaseViewModel()
{
    public var saveLanguage=LanguageModel()
    var savePosition:Int=Session.position
    var english=MutableLiveData(true)
    var swahii=MutableLiveData(false)
    var zulu=MutableLiveData(false)
    inner class ClickAction(var context:android.content.Context,var activity: Activity)
    {




        fun continueBtn(view: View){
            Log.d("TAG", "continueBtn:${saveLanguage.tag} ")
            if(saveLanguage.tag.isNotEmpty()||Session.language=="language"){
                Session.saveIsLanguage(saveLanguage.tag)
                Session.savePosition(savePosition)
                activity.setLocal(Session.language,2)
            }else{
                activity.setLocal("en",2)
                Session.saveIsLanguage("en")
            }
            if(SplashViewModel.isLogout==true){
                view.findNavController().navigate(R.id.action_choosePreferredLanguageFragment_to_loginFragment)
            }else{
              //  view.findNavController().navigate(R.id.action_choosePreferredLanguageFragment_to_loginFragment)
                view.findNavController().navigate(R.id.action_choosePreferredLanguageFragment_to_onboardingFragment)
            }
        }


       

    }

}