package com.example.servivet.ui.main.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.DeleteAccountBottomSheetBinding
import com.example.servivet.databinding.FragmentSettingsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.SettingsViewModel
import com.example.servivet.ui.main.view_model.wallet.MyWalletBottomsheetViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.Constants.CHECK_BCK
import com.example.servivet.utils.Constants.SWITCH_ACC
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale


class SettingsFragment :
    BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings) {
    override val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    override val mViewModel: SettingsViewModel by viewModels()
    var myWalletBottomsheetViewModel: MyWalletBottomsheetViewModel? = null
    private var deleteUserBottomSheetDialog: BottomSheetDialog? = null

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myWalletBottomsheetViewModel =
            ViewModelProvider(requireActivity())[MyWalletBottomsheetViewModel::class.java]

        // Observe language change flag

        // Observe language change flag
        myWalletBottomsheetViewModel!!.getLanguageChanged()
            .observe(getViewLifecycleOwner()) { value ->
                if (value!!) {
                    val fragment: SettingsFragment =
                        requireActivity().getSupportFragmentManager().findFragmentById(
                            R.id.home_navigation
                        ) as SettingsFragment
                    if (fragment != null) {
                        fragment.myWalletBottomsheetViewModel?.getLanguageChanged()
                    }
                }
            }
    }

    override fun setupViewModel() {}

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireActivity())
            clickEvents = ::onClick
        }
        setBack()
        checkSwitchProfileVisiblity()
        initObserver()
        bottomSheetCallBack()

        if (Session.notificationStatus != null) {
            if (Session.notificationStatus == true) {
                binding.switchBtn.isChecked = true
            } else {
                binding.switchBtn.isChecked = false
            }
        }
        setClick()
    }


    fun initObserver() {
        mViewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("YourFragment", "Received data: $data")
        }
    }


    /*  override fun onResume() {
          super.onResume()
          val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.navigation_bar)
          val menu = bottomNavigation.menu
          val languageSelectInPreference = Session.language   // viewModel.preference.retrieveLanguage(languageKey)
          if (languageSelectInPreference != null) {
              val titleMap =   changeLocale(requireContext(),languageSelectInPreference)
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
  */

    private fun checkSwitchProfileVisiblity() {
        binding.idSwitchBusiness.isVisible = Session.userDetails.businessType != "4"
    }

    private fun openBottomSheetForDelete() {
        deleteUserBottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: DeleteAccountBottomSheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.delete_account_bottom_sheet, null, false
        )
        deleteUserBottomSheetDialog!!.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.noBtn.setOnClickListener { deleteUserBottomSheetDialog!!.dismiss() }
        bottomSheetBinding.yesBtn.setOnClickListener {
            mViewModel.hitDeleteAccountApi()

        }
        deleteUserBottomSheetDialog!!.show()
    }

    var isFragmentAdded: Boolean = false

    private fun bottomSheetCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("comeFrom")
            ?.observe(viewLifecycleOwner) {
                Log.d("TAG", "bottomsheetCallBack: $it")
                changeLocale(requireContext(), Session.language)
            }
    }

    private fun changeLocale(context: Context, lang: String?): Map<Int, String?> {
        Session.language
        // viewModel.preference.retrieveLanguage(languageKey)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.locale = locale
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        refreshFragment(this)

        // Return a map of menu item IDs to their localized titles
        return mapOf(
            R.id.homeFragment to getString(R.string.home),
            R.id.bookingsFragment to getString(R.string.bookings),
            R.id.chatFragment to getString(R.string.chats),
            R.id.profileFragment to getString(R.string.profile)
            // Add more items as needed
        )
    }

    fun refreshFragment(fragment: Fragment) {
        if (isFragmentAdded == false) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.action_settingsFragment_self)
            isFragmentAdded = true
        }
    }

    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.profileFragment, false)
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                findNavController().navigate(R.id.action_settingsFragment_to_changeLanguageBottomSheet)
            }

            1 -> {
                SWITCH_ACC = true
                CHECK_BCK = false
                Constants.COUNTRY_CODE = Session.userDetails.countryCode
                Constants.MOBILE_NUMBER = Session.userDetails.mobile
                findNavController().navigate(R.id.action_settingsFragment_to_mainActivity3)
            }

            2 -> {
                findNavController().navigate(R.id.action_settingsFragment_to_contactUsFragment)
            }
        }
    }

    private fun setClick() {
        binding.switchBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mViewModel.notificationRequest.notificationStatus = true
                mViewModel.hitNotificationStatusApi()
                Session.saveNotificationStatus(true)
            } else {
                mViewModel.notificationRequest.notificationStatus = false
                mViewModel.hitNotificationStatusApi()
                Session.saveNotificationStatus(false)
            }
        }



        binding.deleteAccount.setOnClickListener { openBottomSheetForDelete() }
    }

    override fun setupObservers() {
        mViewModel.logoutResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showToast(it.data.message)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
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

        mViewModel.notificationResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
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


        mViewModel.deleteResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            deleteUserBottomSheetDialog!!.dismiss()
                            showToast(it.data.message)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showToast(it.data.message)
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