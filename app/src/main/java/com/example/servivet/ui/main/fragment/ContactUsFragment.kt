package com.example.servivet.ui.main.fragment

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentContactUsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.ContactUsViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class ContactUsFragment : BaseFragment<FragmentContactUsBinding, ContactUsViewModel>(R.layout.fragment_contact_us) {
    override val binding: FragmentContactUsBinding by viewBinding(FragmentContactUsBinding::bind)
    override val mViewModel: ContactUsViewModel by viewModels()

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
        setClick()
    }

    private fun setClick() {
        binding.callLayout.setOnClickListener { callNumber(Session.masterData.adminContactNumber) }
        binding.emailLayout.setOnClickListener { sendEmail(Session.masterData.adminEmail    ) }
    }

    private fun callNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phoneNumber}")
        startActivity(intent)
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        startActivity(Intent.createChooser(intent, "Email via..."))
    }

    override fun setupObservers() {
        mViewModel.contactUsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showToast(it.data.message?:"Something went wrong")
                            findNavController().popBackStack()
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message?:"Something went wrong")
                        }

                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }
            }
        }

    }
}