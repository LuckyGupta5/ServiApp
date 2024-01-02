package com.example.servivet.ui.main.view_model

import androidx.lifecycle.viewModelScope
import com.example.servivet.data.model.splash.SplashModel
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.SingleLiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel(){


    companion object
    {
        var isLogout=false
    }

    private var liveData: SingleLiveEvent<SplashModel> = SingleLiveEvent()

    init {
        initSplashScreen()
    }

    private fun initSplashScreen()
    {


        if (isLogout) {
            viewModelScope.launch {
                delay(10)
                updateLiveData()
            }
        }
        else
        {
            viewModelScope.launch {
                delay(2000)
                updateLiveData()
            }
        }
    }

    private fun updateLiveData() {
        liveData.setValue(SplashModel())
    }
    fun getLiveData(): SingleLiveEvent<SplashModel> {
        return liveData
    }
}