package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.servivet.ui.base.BaseViewModel

class ServicesViewModel: BaseViewModel() {
    inner class ClickAction{
       fun back(view: View){
           view.findNavController().popBackStack()
       }
    }
}