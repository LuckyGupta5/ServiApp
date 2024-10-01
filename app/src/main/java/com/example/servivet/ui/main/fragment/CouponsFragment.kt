package com.example.servivet.ui.main.fragment

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.booking_module.coupon.response.CouponList
import com.example.servivet.databinding.FragmentCouponsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.CouponAdapter
import com.example.servivet.ui.main.view_model.SharedViewModel
import com.example.servivet.ui.main.view_model.booking_models.BookingCouponViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson

class CouponsFragment :
    BaseFragment<FragmentCouponsBinding, BookingCouponViewModel>(R.layout.fragment_coupons) {

    override val binding: FragmentCouponsBinding by viewBinding(FragmentCouponsBinding::bind)
    override val mViewModel: BookingCouponViewModel by viewModels()
    private val bookingData: CouponsFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var providerId: ServiceDetail
    private var couponList = ArrayList<CouponList>()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {

            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()


        }
        getBookingData()

    }


    private fun getBookingData() {
        when (getString(bookingData.from)) {
            getString(R.string.payment) -> {
                providerId = Gson().fromJson(bookingData.data, ServiceDetail::class.java)
                mViewModel.getCouponRequest(providerId.createdBy?._id ?: "")
            }
        }
    }

    override fun setupObservers() {
        mViewModel.getCouponData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "checkCouponData: ${Gson().toJson(it.data.result)}")
                            couponList.clear()
                            couponList.addAll(it.data.result.coupon)
                            setAdapter()

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message!!)
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

                Status.UNAUTHORIZED -> {
                    CommonUtils.logoutAlert(
                        requireContext(),
                        "Session Expired",
                        "Unauthorized User",
                        requireActivity()
                    )
                }
            }
        }
    }

    fun setAdapter() {
        if (couponList.size > 0) {
            binding.idNoDataFound.root.isVisible = false
            binding.idNestedScroll.isVisible = true

        } else {
            binding.idNoDataFound.root.isVisible = true
            binding.idNestedScroll.isVisible = false
        }
        binding.recyclerview.adapter = CouponAdapter(
            requireContext(),
            couponList,
            onItemClick,
            bookingData.paymentData.toFloat()
        )
    }

    private val onItemClick: (Int, String) -> Unit = { id, data ->
        when (id) {


            1 -> {
                sharedViewModel.setData(data)
                findNavController().popBackStack()
            }
        }

    }
}