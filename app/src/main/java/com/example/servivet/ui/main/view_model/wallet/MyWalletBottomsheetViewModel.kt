package com.example.servivet.ui.main.view_model.wallet

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.servivet.ui.base.BaseViewModel
import com.example.servivet.ui.main.bottom_sheet.BookingCancelledBottomSheet


class MyWalletBottomsheetViewModel: BaseViewModel(){
    inner class ClickAction(var context: Context){
       fun dismiss(view: View){
           val fragment= BookingCancelledBottomSheet()
           fragment.show((context as AppCompatActivity).supportFragmentManager,"childfragment")
          // (context as AppCompatActivity).supportFragmentManager

       }

    }

}