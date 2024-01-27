package com.example.servivet.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.booking_detail.response.BookingDetail
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.databinding.FragmentBookingDetailsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.bottom_sheet.ReasonForCancelBottomsheet
import com.example.servivet.ui.main.view_model.BookingDetailsViewModel
import com.example.servivet.ui.main.view_model.BookingViewModel
import com.example.servivet.ui.main.view_model.booking_models.MarkAsCompleteViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.getCurrentTimeInFormat
import com.example.servivet.utils.isTimeGapGreaterThan24Hours
import com.example.servivet.utils.updateButtonState
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BookingDetailsFragment :
    BaseFragment<FragmentBookingDetailsBinding, BookingDetailsViewModel>(R.layout.fragment_booking_details) {
    private var bookingId: String? = ""
    override val binding: FragmentBookingDetailsBinding by viewBinding(FragmentBookingDetailsBinding::bind)
    override val mViewModel: BookingDetailsViewModel by viewModels()
    private val argumentData: BookingDetailsFragmentArgs by navArgs()
    private lateinit var bookingData: MyBooking
    private var typeBooking: String? = ""
    private var typeReschdule: String? = ""
    private lateinit var userType: String
    private lateinit var bookingDetail: BookingDetail
    private val bookingViewModel: BookingViewModel by viewModels()
    private val markAsCompleteModel: MarkAsCompleteViewModel by viewModels()


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupViews() {
        getBookingData()

        if (Session.type == "1") {
            userType = "user"
            binding.checkUserType = Constants.TYPEOFUSERS
            Constants.TYPEOFUSERS = "bought"

        } else {
            userType = "provider"
            binding.checkUserType = Constants.TYPEOFUSERS
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            loginAs = Session.type.toInt()
            click = mViewModel.ClickAction()
            clickEvent = ::onClick
        }

        gotobottomsheet()
        gotocancelBottomsheet()
        initBookingViewModel()
        initMarkAsCompleteModel()
    }


    private fun getBookingData() {
        if (argumentData.from == getString(R.string.bookinglist)) {
            bookingData = Gson().fromJson(argumentData.data, MyBooking::class.java)
            binding.type = argumentData.type
            Constants.TYPEOFUSERS = argumentData.typeUsers

        }
    }

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.accept) -> {
                bookingViewModel.acceptBookingRequest.bookingId = bookingData._id
                bookingViewModel.hitAcceptBookingApi()

            }

            getString(R.string.reject) -> {
                bookingViewModel.cancelBookingRequest.bookingId = bookingData._id
                bookingViewModel.cancelBookingRequest.reason = "accept"
                bookingViewModel.cancelBookingRequest.cancelledBy = userType
                Log.e(
                    "TAG",
                    "checkBookingReq: ${Gson().toJson(bookingViewModel.cancelBookingRequest)}",
                )
                bookingViewModel.hitCancelBookingApi()
            }

            getString(R.string.reschedule) -> {
                findNavController().navigate(
                    BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingSummaryFragment(
                        Gson().toJson(bookingDetail), getString(R.string.reschedule)
                    )
                )

            }

            getString(R.string.book_again) -> {
                findNavController().navigate(
                    BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingSummaryFragment(
                        Gson().toJson(bookingDetail),
                        getString(R.string.booking_details)
                    )
                )
            }

            getString(R.string.mark_as_completed) -> {
                markAsCompleteModel.getMarkAsCompleteRequest(
                    bookingData._id,
                    Constants.TYPEOFUSERS
                )

            }
        }
    }


    override fun setupViewModel() {


    }


    fun gotobottomsheet() {
//        binding.reSchedule.setOnClickListener(View.OnClickListener {
//            val fragment = ReScheduleBookingBottomSheet()
//            fragment.show(childFragmentManager, "InterestBottomSheetFragment")
//
//        })
    }

    fun gotocancelBottomsheet() {

        binding.idCancelBooking.setOnClickListener(View.OnClickListener {
            findNavController().navigate(
                BookingDetailsFragmentDirections.actionBookingDetailsFragmentToReasonForCancelBottomsheet(
                    bookingData._id,
                    getString(R.string.booking_details)
                )
            )
        })
        binding.cancelButton.setOnClickListener(View.OnClickListener {
//            val fragment=ReasonForCancelBottomsheet()
//            fragment.show(childFragmentManager,"CancelBottomSheetFragment")
            findNavController().navigate(
                BookingDetailsFragmentDirections.actionBookingDetailsFragmentToReasonForCancelBottomsheet(
                    bookingData._id,
                    getString(R.string.booking_details)
                )
            )
        })
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupObservers() {
        mViewModel.getBookingDetailsRequest(bookingData._id)
        mViewModel.bookingDetailResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            bookingDetail = it.data.result.bookingDetail
                            binding.data = bookingDetail
                            Log.e("TAG", "setupObservers: ${Gson().toJson(bookingDetail)}")
                            //  setview()

                            manageMarkAsCompleteView()

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
                            Log.e(
                                "TAG",
                                "check24Hours:${Gson().toJson(it.data.result.bookingDetail.bookingDate)} ",
                            )


                            binding.dateAndTimeText.text =
                                it.data.result.bookingDetail.day + "," + CommonUtils.getDateTimeStampConvert(
                                    it.data.result.bookingDetail.startTime
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun manageMarkAsCompleteView() {
        when (argumentData.type) {
            0 -> {
                if (Session.type == "2") {
                    if (Constants.TYPEOFUSERS == getString(R.string.bought_small)) {
                        if (isTimeGapGreaterThan24Hours(
                                getCurrentTimeInFormat(),
                                bookingDetail.startTime,
                                24
                            )
                        ) {
                            binding.idCancelBooking.isVisible = true
                        } else {
                            binding.idNotCancellable.isVisible = true
                        }

                    } else {
                        binding.acceptRejectLayout.isVisible = true
                    }

                } else {
                    if (isTimeGapGreaterThan24Hours(
                            getCurrentTimeInFormat(),
                            bookingDetail.startTime,
                            24
                        )
                    ) {
                        binding.idCancelBooking.isVisible = true
                    } else {
                        binding.idNotCancellable.isVisible = true
                    }
                }

            }

            1 -> {

                if (isTimeGapGreaterThan24Hours(
                        getCurrentTimeInFormat(),
                        bookingDetail.startTime,
                        24
                    ) && Constants.TYPEOFUSERS == getString(R.string.bought_small)
                ) {
                    if (bookingDetail.isReschedule) {
                        binding.reSchedule.isEnabled = false
                        binding.reSchedule.alpha = 0.3f
                    } else {
                        binding.reSchedule.isEnabled = true
                        binding.reSchedule.alpha = 1.0f
                    }

                    if (bookingDetail.bookingCompleted != null) {
                        if (bookingDetail.bookingCompleted!!.provider?.isComplete!!) {
                            Log.e("TAG", "manageMarkAsCompleteView: true")
                            binding.markAsCompleted.isVisible = true
                            binding.reScheduleLayout.isVisible = false
                        } else {
                            Log.e("TAG", "manageMarkAsCompleteView: false")
                            binding.markAsCompleted.isVisible = false
                            binding.reScheduleLayout.isVisible = true
                        }
                    } else {
                        binding.markAsCompleted.isVisible = false
                        binding.reScheduleLayout.isVisible = true
                    }
                } else {
                    if (Session.type == "2" && Constants.TYPEOFUSERS == getString(R.string.sold_small)) {
                        binding.markAsCompleted.isVisible = true

                    } else {
                        if (bookingDetail.bookingCompleted != null) {
                            if (bookingDetail.bookingCompleted!!.provider?.isComplete!!) {
                                binding.markAsCompleted.isVisible = true
                            } else {
                                binding.markAsCompleted.isVisible = false
                                binding.reScheduleLayout.isVisible = false
                            }
                        }
                    }
                }
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

    private fun initMarkAsCompleteModel() {
        markAsCompleteModel.getMarkAsCompleteData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            findNavController().popBackStack()
                            Log.e("TAG", "initMarkAsCompleteModel1:${it.data.message} ")

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                            Log.e("TAG", "initMarkAsCompleteModel2:${it.data.message} ")

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
                        Log.e("TAG", "initMarkAsCompleteModel3:${it} ")

                    }
                }

                else -> {}
            }
        }
    }


}