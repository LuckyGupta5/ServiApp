package com.example.servivet.ui.main.fragment.bookinng_module

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.create_order.response.CreateOrderResult
import com.example.servivet.databinding.FragmentAddServiceBinding
import com.example.servivet.databinding.FragmentPaymentBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.wallet.PaymentViewModel
import com.example.servivet.utils.Constants
import com.example.servivet.utils.getLastWordFromUrl
import com.google.gson.Gson
import org.json.JSONObject


class PaymentFragment :
    BaseFragment<FragmentPaymentBinding, PaymentViewModel>(R.layout.fragment_payment) {
    private val paymentData: PaymentFragmentArgs by navArgs()
    private var callbackUrl = "http://13.235.137.221:3476/mobileApi/call-back"


    override val binding: FragmentPaymentBinding by viewBinding(FragmentPaymentBinding::bind)

    override val mViewModel: PaymentViewModel by viewModels()


    override fun isNetworkAvailable(boolean: Boolean) {}

    override fun setupViewModel() {}

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(
                requireContext(),
                binding,
                requireActivity(),
                requireActivity().isFinishing
            )
        }

        getData()

    }

    private fun getData() {
        val data = Gson().fromJson(paymentData.payUrl, CreateOrderResult::class.java)
        binding.idWebView.settings.javaScriptEnabled = true
        binding.idWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true // Enable DOM storage API for the webview
            useWideViewPort = true // Enable viewport meta tag handling
            loadWithOverviewMode = true
            allowFileAccess = true
            allowContentAccess = true
        }
        // Set a WebViewClient to handle redirection and links within the WebView
        binding.idWebView.webViewClient = MyWebViewClient()
        binding.idWebView.loadUrl(data.authorization_url)
        Log.e("TAG", "getData: ${data.authorization_url}")
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            // This method will be called when the URL is about to be loaded
            // You can intercept the URL here and perform custom actions

            // Get the new URL
            val url = request?.url
            Log.e("TAG", "shouldOverrideUrlLoading: ${url?.host}")
            Log.e("TAG", "shouldOverrideUrlLoading: ${url.toString()}")

            val reponse = getLastWordFromUrl(url.toString())
            Log.e("TAG", "shouldOverrideUrlLoading: ${reponse}")



            if (reponse.equals("Success", ignoreCase = true)) {
                findNavController().navigate(R.id.action_paymentFragment_to_homeFragment)
                Constants.APPLIED_COUPON = ""


            } else if (url.toString() == "https://standard.paystack.co/close") {
                // finish()
                Log.e("TAG", "shouldOverrideUrlLoading: ${url?.host}")
                return false
            }

            // Log or handle the new URL as needed
            // For example, you might want to load the URL in the WebView
            //   binding.idWebView.loadUrl(url)

            // Return true to indicate that the WebView should not load the URL automatically
            // Return false if you want the WebView to handle the URL automatically
            return super.shouldOverrideUrlLoading(view, request)
        }
    }


    override fun setupObservers() {

    }


}