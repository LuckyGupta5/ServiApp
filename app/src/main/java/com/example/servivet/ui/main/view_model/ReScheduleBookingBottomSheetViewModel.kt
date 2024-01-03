package com.example.servivet.ui.main.view_model

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import androidx.navigation.NavController
import com.example.servivet.R
import com.example.servivet.databinding.FragmentReScheduleBookingBottomSheetBinding
import com.example.servivet.ui.base.BaseViewModel
import java.util.Calendar

class ReScheduleBookingBottomSheetViewModel:BaseViewModel()
{
    var binding:FragmentReScheduleBookingBottomSheetBinding?=null
    var context:Context?=null
    private lateinit var navController: NavController
    fun setnavcontroller(navController: NavController){
        this.navController=navController
        navController.navigate(R.id.action_bookingDetailsFragment_to_changedateFragment)

    }
    inner class ClickAction(var binding:FragmentReScheduleBookingBottomSheetBinding,var context: Context)
    {

        private var mDay = 0
        private var mMonth = 0
        private var mYear = 0
        private var date: String? = null


        fun gotochangeDate(view:View){
        }



    }



}