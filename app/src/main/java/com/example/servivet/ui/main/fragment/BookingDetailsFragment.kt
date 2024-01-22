package com.example.servivet.ui.main.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_detail.response.BookingDetail
import com.example.servivet.databinding.FragmentBookingDetailsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.bottom_sheet.ReScheduleBookingBottomSheet
import com.example.servivet.ui.main.bottom_sheet.ReasonForCancelBottomsheet
import com.example.servivet.ui.main.view_model.BookingDetailsViewModel
import com.example.servivet.ui.main.view_model.BookingViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.updateButtonState
import com.google.gson.Gson
import kotlin.math.log

class BookingDetailsFragment :
    BaseFragment<FragmentBookingDetailsBinding, BookingDetailsViewModel>(R.layout.fragment_booking_details) {
    private var bookingId: String? = ""
    override val binding: FragmentBookingDetailsBinding by viewBinding(FragmentBookingDetailsBinding::bind)
    override val mViewModel: BookingDetailsViewModel by viewModels()
    private var typeBooking: String? = ""
    private var typeReschdule: String? = ""
    private lateinit var userType:String
    private lateinit var bookingDetail:BookingDetail
    private val bookingViewModel: BookingViewModel by viewModels()


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupViews() {

        if(Session.type == "1"){
            userType = "user"
        }else{
            userType = "Provider"
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()

        }
        typeBooking = arguments?.getString("type")
        bookingId = arguments?.getString("bookingId")
        typeReschdule = requireArguments().getString("istype")
        mViewModel.bookingDetailRequest.bookingId = bookingId.toString()
        mViewModel.hitBookingDetailApi()
        binding.clickEvent = ::onClick
        gotobottomsheet()
        gotocancelBottomsheet()
        initBookingViewModel()
    }

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.accept) -> {
                bookingViewModel.acceptBookingRequest.bookingId = bookingId!!
                bookingViewModel.hitAcceptBookingApi()

            }

            getString(R.string.reject) -> {
                bookingViewModel.cancelBookingRequest.bookingId=bookingId!!
                bookingViewModel.cancelBookingRequest.reason="accept"
                bookingViewModel.cancelBookingRequest.cancelledBy=userType
                bookingViewModel.hitCancelBookingApi()
            }
            getString(R.string.reschedule)->{
                findNavController().navigate(BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingSummaryFragment(
                    Gson().toJson(bookingDetail),getString(R.string.reschedule)))

            }
            getString(R.string.book_again)->{
                findNavController().navigate(BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingSummaryFragment(Gson().toJson(bookingDetail),getString(R.string.booking_details)))
            }
        }
    }


    override fun setupViewModel() {


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setview() {

        if(typeBooking.equals("0")){
            if (Session.type == "2") {
                binding.reasonCancelLayout.isVisible = false
                binding.bookingagain.isVisible = false
                binding.markAsCompleted.isVisible = false
                binding.idCancelBooking.isVisible = false
                binding.acceptRejectLayout.isVisible = true

            } else {
                binding.reasonCancelLayout.isVisible = false
                binding.bookingagain.isVisible = false
                binding.markAsCompleted.isVisible = false
                binding.idCancelBooking.isVisible = true
            }

        } else if (typeBooking.equals("1")) {
            binding.reasonCancelLayout.isVisible = false
            binding.bookingagain.isVisible = false
            if(updateButtonState(CommonUtils.getDateFromTimeStamp(bookingDetail.bookingDate)!!)){
                binding.reScheduleLayout.isVisible = true

            }else{
                binding.markAsCompleted.isVisible = true

            }

        } else if (typeBooking.equals("2") || typeReschdule.equals("1")) {
            binding.markAsCompleted.isVisible = false
            binding.reasonCancelLayout.isVisible = false
            binding.bookingagain.isVisible = true
            if (typeReschdule.equals("3")) {
                binding.markAsCompleted.isVisible = false
                binding.reasonCancelLayout.isVisible = false
                binding.bookingagain.isVisible = false
//                binding.reScheduleLayout.isVisible = true
            } else {
                binding.reScheduleLayout.isVisible = false
            }

        } else {
            binding.markAsCompleted.isVisible = false
            binding.bottomLayout.isVisible = false
            binding.bookingagain.isVisible = false
        }
    }

    fun gotobottomsheet() {
//        binding.reSchedule.setOnClickListener(View.OnClickListener {
//            val fragment = ReScheduleBookingBottomSheet()
//            fragment.show(childFragmentManager, "InterestBottomSheetFragment")
//
//        })
    }

    fun gotocancelBottomsheet() {
        binding.cancelButton.setOnClickListener(View.OnClickListener {
            val fragment = ReasonForCancelBottomsheet()
            fragment.show(childFragmentManager, "CancelBottomSheetFragment")
        })
        binding.idCancelBooking.setOnClickListener(View.OnClickListener {
//            val fragment=ReasonForCancelBottomsheet()
//            fragment.show(childFragmentManager,"CancelBottomSheetFragment")
            findNavController().navigate(
                BookingDetailsFragmentDirections.actionBookingDetailsFragmentToReasonForCancelBottomsheet(
                    bookingId!!,
                    getString(R.string.booking_details)
                )
            )
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObservers() {

        mViewModel.bookingDetailResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            bookingDetail = it.data.result.bookingDetail
                            binding.data = bookingDetail
                            setview()

                            for (i in Session.category.indices) {
                                for (j in Session.category[i].subCategory!!.indices) {
                                    if (it.data.result.bookingDetail.serviceDetail.subCategory == Session.category[i].subCategory!![j].id) {
                                        binding.subCatName.text =
                                            Session.category[i].subCategory!![j].name
                                        Glide.with(requireContext())
                                            .load("https://ride-chef-dev.s3.ap-south-1.amazonaws.com/" + Session.category[i].subCategory!![j].image)
                                            .into(binding.image2)
                                    }
                                }
                            }

                            if (it.data.result.bookingDetail.serviceDetail.serviceMode.atHome == true)
                                binding.serviceMode.text = "At Home"

                            if (it.data.result.bookingDetail.serviceDetail.serviceMode.atCenter == true)
                                binding.serviceMode.text = "At Center"

                            binding.dateAndTimeText.text =
                                it.data.result.bookingDetail.day + "," + CommonUtils.getDateTimeStampConvert(
                                    it.data.result.bookingDetail.bookingDate
                                )


                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                else -> {}
            }
        }
    }


    private fun initBookingViewModel() {
        bookingViewModel.cancelBookingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            findNavController().popBackStack()

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                else -> {}
            }
        }

        bookingViewModel.acceptBookingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            findNavController().popBackStack()


                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                else -> {}
            }
        }
    }

}