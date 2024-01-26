package com.example.servivet.ui.main.bottom_sheet

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.booking_summary.response.ServiceDetail
import com.example.servivet.data.model.booking_module.create_order.response.CreateOrderResponse
import com.example.servivet.data.model.booking_module.create_order.response.CreateOrderResult
import com.example.servivet.data.model.booking_module.my_wallet.MyWallet
import com.example.servivet.data.model.booking_module.my_wallet.MyWalletMainResponse
import com.example.servivet.data.model.payment.payment_amount.response.PayAmountResult
import com.example.servivet.databinding.FragmentMyWalletBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.wallet.CreateOrderViewModel
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson

class MyWalletBottomsheet :
    BaseBottomSheetDailogFragment<FragmentMyWalletBottomsheetBinding, MyWalletBottomsheetViewModel>(
        R.layout.fragment_my_wallet_bottomsheet
    ) {
    override val mViewModel: MyWalletBottomsheetViewModel by viewModels()
    private lateinit var walletData: MyWallet
    private val bookingData: MyWalletBottomsheetArgs by navArgs()
    private val createOderViewModel: CreateOrderViewModel by viewModels()
    private lateinit var paymentAmountData: PayAmountResult
    private lateinit var serviceData: ServiceDetail
    private lateinit var paymentUrl: CreateOrderResult
    private var walletAmount = 0.0
    private lateinit var payWith: String

    override fun getLayout() = R.layout.fragment_my_wallet_bottomsheet
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext())
            binding.clickEvent = ::onClick

        }
        getBookingData()
        dismissbottomsheet()


    }

    private fun onClick(value: Int) {
        when (value) {
            0 -> {

                if (binding.idCheckBox.isChecked) {
                    if (paymentAmountData.payableAmount!! > walletData.amount) {
                        walletAmount = walletData.amount
                        payWith = "walletWithPg"
                        createOderViewModel.orderRequest.isWalletAmountInclude = true
                    } else {
                        walletAmount = paymentAmountData.payableAmount!!
                        payWith = "wallet"
                        createOderViewModel.orderRequest.isWalletAmountInclude = true


                    }
                } else {
                    walletAmount = paymentAmountData.payableAmount!!
                    payWith = "paymentGateway"
                    createOderViewModel.orderRequest.isWalletAmountInclude = false

                }
                initOrderCreateViewModel()
            }

            1 -> {
                dialog?.dismiss()
            }
        }
    }

    private fun initOrderCreateViewModel() {
        createOderViewModel.getPaymentAmountRequest(
            paymentAmountData,
            serviceData,
            walletAmount,
            payWith
        )
        createOderViewModel.getOrderData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data),
                        CreateOrderResponse::class.java
                    )
                    when (data.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Constants.SECURE_HEADER = " "
                            Log.e("TAG", "initOrderCreateViewModel: ${Gson().toJson(data.result)}", )
                            if(data.result.authorization_url.isNullOrBlank()){
                                findNavController().navigate(R.id.action_myWalletBottomsheet_to_bookingsFragment)
                                dialog?.dismiss()
                            }else{
                                paymentUrl = data.result
                                //findNavController().navigate(R.id.action_myWalletBottomsheet_to_bookingsFragment)
                                findNavController().navigate(MyWalletBottomsheetDirections.actionMyWalletBottomsheetToPaymentFragment(Gson().toJson(paymentUrl), getString(R.string.paymeturl)))

                                // findNavController().previousBackStackEntry?.savedStateHandle?.set(getString(R.string.paymeturl), Gson().toJson(paymentUrl))
                                dialog?.dismiss()
                            }


                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT)
                                .show()
                            Constants.SECURE_HEADER = " "
                        }

                    }
                }

                Status.LOADING -> {
                    Constants.SECURE_HEADER = " "
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()

                    it.message?.let {
                        showSnackBar(it)
                        Log.e("TAG", "initOrderCreateViewModel: ${it}")

                        Constants.SECURE_HEADER = " "

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

    private fun getBookingData() {
        paymentAmountData = Gson().fromJson(bookingData.payAmountData, PayAmountResult::class.java)
        serviceData = Gson().fromJson(bookingData.serviceData, ServiceDetail::class.java)
    }

    fun dismissbottomsheet() {
        /*   binding.cancelButton.setOnClickListener(View.OnClickListener {
               val fragment=BookingCancelledBottomSheet()
               fragment.show(childFragmentManager,"jhgfds")
           })
           binding.paybutton.setOnClickListener(View.OnClickListener {
               val fragment=BookingCancelledBottomSheet()
               fragment.show(childFragmentManager,"jhgfds")
           })
   */
    }

    override fun setupObservers() {
        mViewModel.hitWalletApi()
        mViewModel.getWalletData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data),
                        MyWalletMainResponse::class.java
                    )
                    when (data.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "setupObservers: ${data.result}")
                            showSnackBar(data.message)
                            walletData = data.result.myWallet
                            binding.apiData = walletData
                            Constants.SECURE_HEADER = " "
                            setCheckBox()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(data.message)
                            Constants.SECURE_HEADER = " "
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
                        Constants.SECURE_HEADER = " "

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

    @SuppressLint("SetTextI18n")
    private fun setCheckBox() {
        if (walletData.amount.toFloat() > 0) {
            binding.idCheckBox.isChecked = true
            binding.idCheckBox.isEnabled = true
        } else {
            binding.idCheckBox.isChecked = false
            binding.idCheckBox.isEnabled = false
        }
        checkPayData()



        binding.idCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkPayData()
            } else {
                binding.idPayButton.text =
                    getString(R.string.pay) + (paymentAmountData.payableAmount?.toFloat())


            }

        }

    }

    private fun checkPayData() {
        if (paymentAmountData.payableAmount?.toFloat()!! > walletData.amount.toFloat()) {
            binding.idPayButton.text =
                getString(R.string.pay) + (paymentAmountData.payableAmount?.toFloat()!! - walletData.amount.toFloat()).toString()
        } else {

            binding.idPayButton.text = getString(R.string.pay) + "0"
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

}