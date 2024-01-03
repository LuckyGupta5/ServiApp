package com.example.servivet.ui.main.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeBanner
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.databinding.FragmentHomeBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.HomeNearByAdapter
import com.example.servivet.ui.main.adapter.HomeOnlineNowAdapter
import com.example.servivet.ui.main.adapter.HomePagerAdapter
import com.example.servivet.ui.main.adapter.HomeServiceAdapter
import com.example.servivet.ui.main.view_model.HomeViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)
    override val mViewModel: HomeViewModel by activityViewModels()
    private lateinit var mContext: Context
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun isNetworkAvailable(boolean: Boolean) {}

    override fun setupViewModel() {
        if (isAdded)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = mViewModel
                click = mViewModel.ClickAction()
                setBack()

                activity?.let { mViewModel.hitHomeApi(mContext, it, activity?.isFinishing==true) }
                setOnlineAdapter("1")
                setNearBYAdapter("1")
                setClick()
            }
    }

    private fun setNearBYAdapter(type: String) {
        binding.nearByRecycler.adapter=HomeNearByAdapter(type,requireContext(), ArrayList())

    }

    private fun setOnlineAdapter(type: String) {
        binding.onlineNowRecycler.adapter=HomeOnlineNowAdapter(type,requireContext(),ArrayList())
    }

    private fun setClick() {
        binding.viewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_servicesFragment)
        }
        binding.addservice.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addServiceFragment)
        }
        binding.locationLayout.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchLocationFragment)
        }
    }


    private fun setServiceAdapter(type: String, list: List<HomeServiceCategory>) {
        binding.serviceRecycler.adapter = HomeServiceAdapter(requireContext(), type, list)

    }

    private fun setViewPagerAdapter(banner: List<HomeBanner>) {
        binding.viewPager.adapter = HomePagerAdapter(requireContext(), banner)
        binding.viewPager.pageMargin = 20
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun setupViews() {

    }

    override fun setupObservers() {
        mViewModel.currentResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Session.saveUserDetails( it.data.result.user)
                            Session.type = it.data.result.user.role.toString()
                            setVisibility(it.data.result.user.role)
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

        mViewModel.homeResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            //set service adapter
                            if (it.data.result.serviceCategory != null && it.data.result.serviceCategory!!.isNotEmpty()) {
                                setServiceAdapter("1", it.data.result.serviceCategory)
                                binding.viewPagerLayout.visibility = View.VISIBLE
                                binding.singleBannerImage.visibility = View.GONE
                            } else {
                                binding.viewPagerLayout.visibility = View.GONE
                                binding.singleBannerImage.visibility = View.VISIBLE
                            }
                            //set banner viewpager
                            if (it.data.result.banner != null && it.data.result.banner!!.isNotEmpty()) {
                                setViewPagerAdapter(it.data.result.banner)
                            }
                            activity?.let { activity -> mViewModel.hitCurrentApi(requireContext(), activity, activity?.isFinishing==true) }

                            Session.saveCategory(it.data.result.serviceCategory)

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
                    activity?.let { activity ->
                        CommonUtils.logoutAlert(
                            requireContext(),
                            "Session Expired",
                            "Unauthorized User",
                            activity
                        )
                    }
                }
            }
        }
    }

    private fun setVisibility(role: Int) {
        if (role == 1) {
            binding.onlinenowLayout.visibility = View.VISIBLE
            binding.neaRBYLyout.visibility = View.GONE
            binding.addServiceLayout.visibility = View.GONE

        } else {
            binding.onlinenowLayout.visibility = View.GONE
            binding.neaRBYLyout.visibility = View.VISIBLE
            binding.addServiceLayout.visibility = View.VISIBLE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Toast.makeText(context, "testing", Toast.LENGTH_SHORT).show()
    }

    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setMessage(mContext.getString(R.string.are_you_logout))
                    dialog.setTitle(mContext.resources.getString(R.string.app_name))
                    dialog.setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        activity?.finishAffinity()
                    }
                    dialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
       activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

}