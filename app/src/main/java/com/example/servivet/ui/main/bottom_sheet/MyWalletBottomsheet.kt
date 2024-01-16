package com.example.servivet.ui.main.bottom_sheet

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.my_wallet.MyWallet
import com.example.servivet.data.model.booking_module.my_wallet.MyWalletMainResponse
import com.example.servivet.data.model.payment.payment_amount.response.PayAmountResult
import com.example.servivet.data.model.payment.payment_amount.response.PaymentResponseMain
import com.example.servivet.databinding.FragmentMyWalletBottomsheetBinding
import com.example.servivet.ui.base.BaseBottomSheetDailogFragment
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson

class MyWalletBottomsheet : BaseBottomSheetDailogFragment<FragmentMyWalletBottomsheetBinding, MyWalletBottomsheetViewModel>(R.layout.fragment_my_wallet_bottomsheet) {
    override val mViewModel: MyWalletBottomsheetViewModel by viewModels()
    private lateinit var walletData:MyWallet
    private val bookingData:MyWalletBottomsheetArgs by navArgs()
    private lateinit var paymentAmountData: PayAmountResult

    override fun getLayout()=R.layout.fragment_my_wallet_bottomsheet
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext())

        }
        getBookingData()
        dismissbottomsheet()
    }

    private fun getBookingData() {
        paymentAmountData = Gson().fromJson(bookingData.data,PayAmountResult::class.java)
    }

    fun dismissbottomsheet(){
     /*   binding.cancelButton.setOnClickListener(View.OnClickListener {
            val fragment=BookingCancelledBottomSheet()
            fragment.show(childFragmentManager,"jhgfds")
        })
        binding.paybutton.setOnClickListener(View.OnClickListener {
            val fragment=BookingCancelledBottomSheet()
            fragment.show(childFragmentManager,"jhgfds")
        })
*/    }



    override fun setupObservers() {
        mViewModel.hitWalletApi()
        mViewModel.getWalletData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data), MyWalletMainResponse::class.java
                    )
                    when (data.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e("TAG", "setupObservers: ${data.result}", )
                            showSnackBar(data.message)
                            walletData = data.result.myWallet
                            binding.apiData= walletData
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
        if(walletData.amount.toFloat()>0){
            binding.idCheckBox.isChecked =true
            binding.idCheckBox.isEnabled = true
        }else{
            binding.idCheckBox.isChecked =false
            binding.idCheckBox.isEnabled = false
        }

        if(paymentAmountData.payableAmount?.toFloat()!! > walletData.amount.toFloat()) {
            binding.idPayButton.text = getString(R.string.pay)+(paymentAmountData.payableAmount?.toFloat()!! - walletData.amount.toFloat()).toString()
        }else{
            binding.idPayButton.text = getString(R.string.pay)+walletData.amount

        }

    }

}