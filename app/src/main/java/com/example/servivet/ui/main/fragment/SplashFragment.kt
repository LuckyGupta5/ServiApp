package com.example.servivet.ui.main.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSplashBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.view_model.SplashViewModel
import com.example.servivet.utils.Session
import com.example.servivet.utils.isMiUi
import com.example.servivet.utils.setLocal


class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>(R.layout.fragment_splash) {
    override val binding: FragmentSplashBinding by viewBinding(FragmentSplashBinding::bind)
    override val mViewModel: SplashViewModel by viewModels()
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        if (isAdded)
            binding.apply {
                if (isAdded)
                    lifecycleOwner = viewLifecycleOwner
                    viewModel = mViewModel
            }
    }

    override fun onStart() {
        super.onStart()
        if (isMiUi() && !Settings.canDrawOverlays(requireContext())) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", "com.example.servivet")
            startActivityForResult(intent,101)
        } else if (!isMiUi() && !Settings.canDrawOverlays(requireContext())){
            requireActivity().intent=  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${"com.example.servivet"}"))
            startActivityForResult(requireActivity().intent,103)
            return
        } else if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            requestPermissionForCall()
        else
          //  versionAPI()
//            goFurtherInApp()
            initObserver()
         //   Log.e("TAG", "onStart: ", )Dhairya



    }

    private fun requestPermissionForCall() {
        val permission = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.BLUETOOTH, android.Manifest.permission.MODIFY_AUDIO_SETTINGS, android.Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(requireActivity(),permission,100)
        initObserver()

//        toast(getString(R.string.conveyr_requires_permission))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty())
            initObserver()
           // versionAPI()

    }

    override fun setupObservers() {
       // initObserver()

    }

    private fun initObserver() {
        mViewModel.getLiveData().observe(viewLifecycleOwner)
        {
            Log.i("TAG", "initObserver12345: "+Session.language)
            if (!Session.language.isNullOrEmpty() /*|| Session.language=="language"*/ ){
                context.let {requireActivity().setLocal(Session.language,2)}
            }else{
                Session.saveIsLanguage("en")
                context.let { requireActivity().setLocal("en",2)}
            }

            if (Session.isLogin) {
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()
            } else{
                findNavController().navigate(R.id.action_splashFragment_to_choosePreferredLanguageFragment)
            }


        }
    }
}