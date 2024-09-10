package com.example.servivet.ui.main.fragment.settings_module

import android.text.Html
import android.text.SpannableString
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentAboutUsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.AboutUsViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode


class AboutUsFragment :
    BaseFragment<FragmentAboutUsBinding, AboutUsViewModel>(R.layout.fragment_about_us) {
    override val binding: FragmentAboutUsBinding by viewBinding(FragmentAboutUsBinding::bind)
    override val mViewModel: AboutUsViewModel by viewModels()
    var type=""

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()
        }
        type=arguments?.getString("type").toString()
        mViewModel.hitCmsApi(type)
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
        mViewModel.cmsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            if (type == "1") {
                                binding.idHeading.setText(getString(R.string.about_us))
                            } else if (type == "2") {
                                binding.idHeading.setText(getString(R.string.term_and_condition))
                            } else if (type == "3") {
                                binding.idHeading.setText(getString(R.string.privacy_policy))
                            }

                            // Check if the desc field is null before passing it to Html.fromHtml
                            val description = it.data.result?.desc
                            binding.idText.text = if (description != null) {
                                Html.fromHtml(description)
                            } else {
                                // Handle null case, either show a default message or empty string
                                SpannableString("Lorem")
                            }
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data?.message ?: "An error occurred")
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.dismissDialog()
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let { showSnackBar(it) }
                }

                Status.UNAUTHORIZED -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let { showSnackBar(it) }
                }
            }
        }
    }



}