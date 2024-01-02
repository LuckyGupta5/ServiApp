package com.example.servivet.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBookingDetailsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.bottom_sheet.ReScheduleBookingBottomSheet
import com.example.servivet.ui.main.bottom_sheet.ReasonForCancelBottomsheet
import com.example.servivet.ui.main.view_model.BookingDetailsViewModel
import kotlin.math.log

class BookingDetailsFragment : BaseFragment<FragmentBookingDetailsBinding,BookingDetailsViewModel>(R.layout.fragment_booking_details) {
    override val binding: FragmentBookingDetailsBinding by viewBinding(FragmentBookingDetailsBinding::bind)
    override val mViewModel: BookingDetailsViewModel by viewModels()
    private var typeBooking: String?=""
    private var typeReschdule:String?=""

    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()

        }
        typeBooking=arguments?.getString("type")
        typeReschdule=requireArguments().getString("istype")
        Log.d("TAG", "setupViewModelreschudle: "+typeReschdule+typeBooking)
        Log.d("TAG", "setupViewModelhgfd: "+typeBooking)
        gotobottomsheet()
        gotocancelBottomsheet()

        setview()

    }


    override fun setupViewModel() {



    }

    fun setview(){
        if(typeBooking.equals("0")){
            binding.reasonCancelLayout.isVisible=false
            binding.bookingagain.isVisible=false
            binding.markAsCompleted.isVisible=true

        }
        else if(typeBooking.equals("1")||typeReschdule.equals("1"))
        {
            binding.markAsCompleted.isVisible=false
            binding.reasonCancelLayout.isVisible=false
            binding.bookingagain.isVisible=true

            if(typeReschdule.equals("1")){

                binding.markAsCompleted.isVisible=false
                binding.reasonCancelLayout.isVisible=false
                binding.bookingagain.isVisible=false
                binding.reScheduleLayout.isVisible=true
            }
            else{
                binding.reScheduleLayout.isVisible=false
            }

        }
        else{
            binding.markAsCompleted.isVisible=false
            binding.bottomLayout.isVisible=false
            binding.bookingagain.isVisible=false

        }




    }
    fun gotobottomsheet(){
        binding.reSchedule.setOnClickListener(View.OnClickListener {
            val fragment= ReScheduleBookingBottomSheet()
            fragment.show(childFragmentManager,"InterestBottomSheetFragment")

        })
    }
    fun gotocancelBottomsheet(){
        binding.cancelButton.setOnClickListener(View.OnClickListener {
            val fragment=ReasonForCancelBottomsheet()
            fragment.show(childFragmentManager,"CancelBottomSheetFragment")
        })
    }


    override fun setupObservers() {
    }

}