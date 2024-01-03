package com.example.servivet.ui.main.fragment

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentBookingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.BookingAdapter
import com.example.servivet.ui.main.bottom_sheet.FragmentRatingUsBottomSheet
import com.example.servivet.ui.main.view_model.BookingViewModel
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class BookingsFragment : BaseFragment<FragmentBookingBinding,BookingViewModel>(R.layout.fragment_booking), BookingAdapter.Callback {
    override val binding: FragmentBookingBinding by viewBinding(FragmentBookingBinding::bind)
    override val mViewModel: BookingViewModel by viewModels()
     var type:Int=0
    var typeReschdule:Int=0

    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext(),binding)

        }
        ProcessDialog.dismissDialog()
        settablayout()
        type=0
        typeReschdule=0
        setBookingAdapter()
        setBack()

    }

    override fun setupViewModel() {

    }

    fun settablayout(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.upcoming))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.completed))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.cancelled))
        binding.tabLayout.getTabAt(0)?.select()
       // val tabLayout = TabLayout(requireContext())
       binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(binding.tabLayout.selectedTabPosition == 0){
                    type=0
                    typeReschdule=0
                    setBookingAdapter()

                }
                else if(binding.tabLayout.selectedTabPosition==1){
                    type=1
                    typeReschdule=1
                    setBookingAdapter()
                }
                else if(binding.tabLayout.selectedTabPosition==2){
                    type=2
                    typeReschdule=2
                    setBookingAdapter()

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

   fun setBookingAdapter(){
       binding.recyclercategry.adapter=BookingAdapter(requireActivity(),type,typeReschdule,ArrayList(),this)
   }


  override fun setupObservers() {
    }
    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_bookingsFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onCallback(type: String) {
        val fragment=FragmentRatingUsBottomSheet()
        fragment.show(childFragmentManager,"InterestBottomSheetFragment")
    }

}