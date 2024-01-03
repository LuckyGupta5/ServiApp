package com.example.servivet.ui.main.view_model

import android.content.Context
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import com.example.servivet.R
import com.example.servivet.data.model.view_pager_model.ViewPagerTutorialModel
import com.example.servivet.databinding.FragmentOnboardingBinding
import com.example.servivet.ui.base.BaseViewModel

class OnboardingViewModel: BaseViewModel() {
     var count: Int=0
    val list=ArrayList<ViewPagerTutorialModel>()
    companion object{
        var isBack=false
    }
    inner class ClickAction(var requireContext: Context, var binding: FragmentOnboardingBinding)
    {

        fun nextBtn(view: View){
            if(count<list.size-1) {
                binding.viewPager.currentItem=count+1
                binding.next.visibility=View.VISIBLE
                binding.skip.visibility=View.VISIBLE
                binding.getStart.visibility=View.GONE
                if(binding.viewPager.currentItem==2){
                    binding.next.visibility=View.GONE
                    binding.skip.visibility=View.GONE
                    binding.getStart.visibility=View.VISIBLE
                }
                count++
                Log.e("TAG", "handleOnBackPressed: "+count )

            }
        }
        fun skipBtn(view: View){
            isBack=true
            view.findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }
        fun getStarted(view: View){
            isBack=true
            view.findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }
    }
}