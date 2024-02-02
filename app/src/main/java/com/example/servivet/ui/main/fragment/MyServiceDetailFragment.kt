package com.example.servivet.ui.main.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
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

class MyServiceDetailFragment :
    BaseFragment<FragmentMyServiceDetailBinding, MyServiceDetailViewModel>(R.layout.fragment_my_service_detail) {
    override val binding: FragmentMyServiceDetailBinding by viewBinding(
        FragmentMyServiceDetailBinding::bind
    )
    override val mViewModel: MyServiceDetailViewModel by viewModels()
    var data: ServiceList? = null
    private var mediaList = ArrayList<String>()
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

    override fun setupViews() {
    }

    override fun setupObservers() {
        mViewModel.serviceCategoryDetailsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            binding.data = it.data.result!!.serviceDetail
                            mViewModel.data = it.data.result.serviceDetail
                            mediaList.clear()
                            mediaList.addAll(it.data.result.serviceDetail!!.images!!)
                            setImageAdapter(mediaList)
                            val smallest: String = min(
                                it.data.result.serviceDetail.atCenterPrice!!,
                                it.data.result.serviceDetail.atHomePrice!!
                            ).toString()
                            val largest: String = max(
                                it.data.result.serviceDetail.atCenterPrice,
                                it.data.result.serviceDetail.atHomePrice!!
                            ).toString()
                            binding.smallest.text = commaSaparator(smallest.toDouble()).toString()
                            binding.largest.text = commaSaparator(largest.toDouble()).toString()
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
            binding.imageRecycler.adapter = ServiceDetailsImgAdapter(requireContext(), list, onItemClick)


        } else {
            binding.imageRecycler.visibility = View.GONE
        }
    }

    fun commaSaparator(number: Double?): String? {
        val formatter = DecimalFormat("#,###,###")
        return formatter.format(number)
    }

    private val onItemClick: (String, String,Int) -> Unit = { identifier, data, position->
        findNavController().navigate(
            MyServiceDetailFragmentDirections.actionMyServiceDetailFragmentToImageVideoViewFragment(Gson().toJson(mediaList), getString(R.string.servicedetails),position)
        )
    }


}