package com.example.servivet.ui.main.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.connection.connection_list.responnse.MyConnection
import com.example.servivet.databinding.FragmentProfileBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.adapter.ProfileConnectionAdapter
import com.example.servivet.ui.main.view_model.MyConnectionModelView
import com.example.servivet.ui.main.view_model.ProfileViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants.COME_FROM
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {
    override val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)
    override val mViewModel: ProfileViewModel by activityViewModels()
    private val connectionModel: MyConnectionModelView by viewModels()
    private val connectionList = ArrayList<MyConnection>()

    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViewModel() {
        if (isAdded)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewMOdel = mViewModel
                click = mViewModel.ClickAction(requireActivity(), requireContext())
                setBack()
            }
        if (Session.userDetails != null)
            mViewModel.hitUserProfileApi(
                Session.userDetails._id,
                Session.type.toInt(),
                requireContext(),
                requireActivity(),
                requireActivity().isFinishing
            )
        initMyConnectionModel()
        if (arguments?.getString(COME_FROM)?.isNotEmpty() == true) {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
            arguments = null
        }
    }


    private fun setServiceAdapter(type: String, myContact: List<Any>) {
        if (myContact != null && myContact.isNotEmpty()) {
            //   binding.serviceRecycler.visibility=View.VISIBLE
            // binding.noDataLayout.visibility=View.GONE
//            binding.serviceRecycler.adapter = HomeServiceAdapter(requireContext(), type, ArrayList())
        } else {
            //   binding.serviceRecycler.visibility=View.GONE
            //    binding.noDataLayout.visibility=View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation =
            requireActivity().findViewById<BottomNavigationView>(R.id.navigation_bar)
        val menu = bottomNavigation.menu
        val languageSelectInPreference =
            Session.language   // viewModel.preference.retrieveLanguage(languageKey)
        if (languageSelectInPreference != null) {
            val titleMap = changeLocale(requireContext(), languageSelectInPreference)
            // Update titles for each menu item based on the selected language
            titleMap.forEach { (itemId, title) ->
                menu.findItem(itemId).title = title
            }
        }
    }


    fun changeLocale(context: Context, lang: String?): Map<Int, String?> {
        Session.language
        // viewModel.preference.retrieveLanguage(languageKey)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.locale = locale
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        // Return a map of menu item IDs to their localized titles
        return mapOf(
            R.id.homeFragment to getString(R.string.home),
            R.id.bookingsFragment to getString(R.string.bookings),
            R.id.chatFragment to getString(R.string.chats),
            R.id.profileFragment to getString(R.string.profile)
            // Add more items as needed
        )
    }

    override fun setupViews() {}

    override fun setupObservers() {
        mViewModel.userProfileResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            binding.data = it.data.result.profile
                            Session.saveUserProfile(it.data.result.profile)
                            Log.e("TAG", "setupObservers: ${it.data.result.profile}")
                            if (Session.userDetails?.businessType != null) {
                                if (Session.userDetails.businessType == "3")
                                    binding.businessType.setText(R.string.individual)
                                else if (Session.userDetails.businessType == "4")
                                    binding.businessType.setText(R.string.institutional)
                            }
                            if (it.data.result.profile.role == 2) {
                                binding.editProfile.text = getString(R.string.my_services)
                                binding.institutionalLayoutInfo.visibility = View.VISIBLE
                                binding.businessType.visibility = View.VISIBLE

                            } else {
                                binding.editProfile.text = getString(R.string.edit_profile)
                                binding.institutionalLayoutInfo.visibility = View.GONE
                                binding.businessType.visibility = View.GONE
                            }
                            if (it.data.result.profile.description.isEmpty()) {
                                binding.description.text =
                                    getString(R.string.welcome_to_servivet_we_are_passionate_about_creating_meaningful_connections_and_facilitating_seamless_interactions_our_mission_is_to_provide_a_platform_that_fosters_community_encourages_collaboration_and_sparks_engaging_conversations_among_users)
                            } else {
                                binding.description.text = it.data.result.profile.description
                            }
                            setServiceAdapter("3", it.data.result.profile.myContact)
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

    private fun initMyConnectionModel() {
        connectionModel.getConnectionListRequest()
        connectionModel.getConnectionData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            connectionList.clear()
                            connectionList.addAll(it.data.result.myConnectionList)
                            initConnectionAdapter()
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

    private fun initConnectionAdapter() {
        binding.connectionAdapter =
            ProfileConnectionAdapter(requireContext(), connectionList, onItemClick)
    }


    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private val onItemClick: (Int, String) -> Unit = { identifire, data ->
        when (identifire) {
            0 -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ProcessDialog.dismissDialog()
    }

}