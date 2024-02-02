package com.example.servivet.ui.main.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.user_profile.response.UserProfile
import com.example.servivet.databinding.FragmentOnlineNowBinding
import com.example.servivet.databinding.FragmentProviderProfileBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.OnlineNowViewModel
import com.example.servivet.ui.main.view_model.ProfileViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson


class ProviderProfileFragment :
    BaseFragment<FragmentProviderProfileBinding, ProfileViewModel>(R.layout.fragment_provider_profile) {
    override val binding: FragmentProviderProfileBinding by viewBinding(
        FragmentProviderProfileBinding::bind
    )
    override val mViewModel: ProfileViewModel by viewModels()
    private val argumentData: ProviderProfileFragmentArgs by navArgs()
    private lateinit var profileData: UserProfile

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        getArgumentData()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireActivity(), requireContext())
            clickEvents = ::onClick
        }
    }


    private fun getArgumentData() {

    }

    override fun setupViews() {
    }


    private fun onClick(type: String) {
        when (type) {
            getString(R.string.back_press) -> {
                findNavController().popBackStack()
            }
            getString(R.string.services)->{
                findNavController().navigate(R.id.action_providerProfileFragment_to_myServiceFragment)
            }
        }
    }

    override fun setupObservers() {
        mViewModel.hitUserProfileApi(
            argumentData.data,
            0,
            requireContext(),
            requireActivity(),
            false
        )

        mViewModel.userProfileResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            profileData = it.data.result.profile
                            binding.detailData = profileData
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


}