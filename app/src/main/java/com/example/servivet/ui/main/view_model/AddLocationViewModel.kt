package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.servivet.ui.base.BaseViewModel

class AddLocationViewModel:BaseViewModel() {
    var isCheck= MutableLiveData(false)
    var isClick=false
    inner class ClickAction(){
        fun backbtn(view:View){
            view.findNavController().popBackStack()

        }
            fun agree(view: View){
                isClick = if(!isClick) {
                    isCheck.postValue(true)
                    true
                }else{
                    isCheck.postValue(false)
                    false
                }
                    }

    }
}