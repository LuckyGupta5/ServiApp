package com.example.servivet.ui.main.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.servivet.databinding.FragmentBookingBinding
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.utils.Session

class BookingViewModel:BaseViewModel() {

    var upcoming=MutableLiveData(true)
    var completed=MutableLiveData(false)
    var cancelled=MutableLiveData(false)
    inner class ClickAction(var context: Context,var binding:FragmentBookingBinding){
      /*  fun ClickUpComing(view:View){
            Session.saveType("1")
            upcoming.postValue(true)
            completed.postValue(false)
            cancelled.postValue(false)


        }
        fun ClickComplete(view:View)
        {
            Session.saveType("2")
            upcoming.postValue(false)
            completed.postValue(true)
            cancelled.postValue(false)
        }
        fun ClickCancel(view:View)
        {
            Session.saveType("3")
            upcoming.postValue(false)
            completed.postValue(false)
            cancelled.postValue(true)

        }
*/
    }
}