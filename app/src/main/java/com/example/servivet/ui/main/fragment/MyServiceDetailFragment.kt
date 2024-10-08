package com.example.servivet.ui.main.fragment

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.service_category_details.response.ServiceDetail
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.FragmentMyServiceDetailBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ServiceDetailsImgAdapter
import com.example.servivet.ui.main.view_model.MyServiceDetailViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
class MyServiceDetailFragment :
    BaseFragment<FragmentMyServiceDetailBinding, MyServiceDetailViewModel>(R.layout.fragment_my_service_detail) {
    override val binding: FragmentMyServiceDetailBinding by viewBinding(
        FragmentMyServiceDetailBinding::bind
    )
    override val mViewModel: MyServiceDetailViewModel by viewModels()
    var data: ServiceList? = null
    private var mediaList = ArrayList<String>()
    private lateinit var serviceDetails: ServiceDetail
    var smallest = ""
    var largest = ""
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireActivity(), binding, requireActivity())

        }

        data = arguments?.getSerializable(Constants.DATA) as ServiceList?
        mViewModel.serviceCategoryDetailsRequest.serviceId = data!!._id
        mViewModel.hitServiceDetailsAPI(
            requireContext(),
            requireActivity(),
            requireActivity().isFinishing
        )
    }

    private fun checkVisibility(binding: FragmentMyServiceDetailBinding) {
        // Show the smallest value only if it's greater than 0.0
        binding.smallest.isVisible = smallest != "0.0" && smallest != largest
        binding.idView.isVisible =
            binding.smallest.isVisible // Divider visibility based on smallest
        // Always show the largest value
        binding.largest.isVisible = largest != "0.0"
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
        mViewModel.serviceCategoryDetailsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            serviceDetails = it.data.result!!.serviceDetail!!
                            serviceDetails.localImage = serviceDetails.images?.get(0) ?: ""
                            binding.data = serviceDetails
                            mViewModel.data = it.data.result.serviceDetail
                            mediaList.clear()
                            mediaList.addAll(it.data.result.serviceDetail!!.images!!)
                            setImageAdapter(mediaList)

                            serviceDetails.serviceMode?.let { serviceMode ->
                                when {
                                    serviceMode.atHome == true && serviceMode.atCenter == true -> {
                                        // Both atHome and atCenter are true, show both
                                        binding.smallest.visibility = View.VISIBLE
                                        binding.idView.visibility = View.VISIBLE
                                        binding.largest.visibility = View.VISIBLE
                                    }

                                    serviceMode.atHome == true -> {
                                        // Only atHome is true
                                        binding.smallest.visibility = View.GONE
                                        binding.idView.visibility = View.GONE
                                        binding.largest.visibility = View.VISIBLE
                                    }

                                    serviceMode.atCenter == true -> {
                                        // Only atCenter is true
                                        binding.smallest.visibility = View.VISIBLE
                                        binding.idView.visibility = View.GONE
                                        binding.largest.visibility = View.GONE
                                    }

                                    else -> {
                                        // Neither atHome nor atCenter is true, show default
                                        binding.smallest.visibility = View.VISIBLE
                                        binding.idView.visibility = View.VISIBLE
                                        binding.largest.visibility = View.VISIBLE
                                    }
                                }
                            }
                            if (serviceDetails.atHomePrice != 0.0 && serviceDetails.atCenterPrice != 0.0) {
                                smallest = min(serviceDetails.atHomePrice ?: 0.0, serviceDetails.atCenterPrice ?: 0.0).toString()
                                largest = max(serviceDetails.atHomePrice ?: 0.0, serviceDetails.atCenterPrice ?: 0.0).toString()
                            }
                            if (serviceDetails.serviceMode?.atHome == true && serviceDetails.serviceMode?.atCenter == false) {
                                binding.largest.text = "ZAR " + serviceDetails.atHomePrice
                            } else if (serviceDetails?.serviceMode?.atCenter == true && serviceDetails.serviceMode?.atHome == false) {
                                binding.smallest.text = "ZAR " + serviceDetails.atCenterPrice
                            } else {
                                // Update UI with prices
                                binding.smallest.text = "ZAR " +smallest.toDouble()
                                binding.largest.text = "ZAR " + largest.toDouble()
                            }
                            // Handle visibility
//                            checkVisibility(binding)
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


    private fun setImageAdapter(list: ArrayList<String>) {
        if (list != null && list.isNotEmpty()) {
            binding.imageRecycler.visibility = View.VISIBLE
            binding.imageRecycler.adapter =
                ServiceDetailsImgAdapter(requireContext(), list, onItemClick)


        } else {
            binding.imageRecycler.visibility = View.GONE
        }
    }

    fun commaSaparator(number: Double?): String? {
        val formatter = DecimalFormat("#,###,###")
        return formatter.format(number)
    }

    private val onItemClick: (String, String, Int) -> Unit = { identifier, data, position ->
        findNavController().navigate(
            MyServiceDetailFragmentDirections.actionMyServiceDetailFragmentToImageVideoViewFragment(
                Gson().toJson(mediaList),
                getString(R.string.servicedetails),
                position
            )
        )
    }
}