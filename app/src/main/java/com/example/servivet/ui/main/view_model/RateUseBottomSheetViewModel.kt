package com.example.servivet.ui.main.view_model

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.servivet.databinding.FragmentRatingUsBottomSheetBinding
import com.example.servivet.ui.base.BaseViewModel

class RateUseBottomSheetViewModel: BaseViewModel(){
    val about= MutableLiveData(false)
    private var abouttext=""

    inner class ClickAction(var binding: FragmentRatingUsBottomSheetBinding){
        fun onAboutTextChanged(text: CharSequence) {
            binding.wordCount.text = "(" + text.toString().length.toString() + "/100 Words)"
            about.value=text.isNotEmpty()
            abouttext=text.toString().trim()
        }



    }
}