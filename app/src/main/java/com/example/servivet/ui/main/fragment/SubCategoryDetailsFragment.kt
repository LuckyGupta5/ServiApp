package com.example.servivet.ui.main.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.review_ratinng.ReviewRating
import com.example.servivet.data.model.service_category_details.response.RatingReviews
import com.example.servivet.data.model.service_category_details.response.ServiceDetail
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.FragmentSubCategoryDetailsBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.RatingAdapter
import com.example.servivet.ui.main.adapter.RatingReviewAdapter
import com.example.servivet.ui.main.adapter.ServiceDetailsImgAdapter
import com.example.servivet.ui.main.view_model.sub_category_models.RatingReportViewModel
import com.example.servivet.ui.main.view_model.sub_category_models.RatingReviewViewModel
import com.example.servivet.ui.main.view_model.sub_category_models.SubCategoryDetailsViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.gson.Gson
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
class SubCategoryDetailsFragment :
    BaseFragment<FragmentSubCategoryDetailsBinding, SubCategoryDetailsViewModel>(R.layout.fragment_sub_category_details) {
    override val binding: FragmentSubCategoryDetailsBinding by viewBinding(
        FragmentSubCategoryDetailsBinding::bind
    )
    override val mViewModel: SubCategoryDetailsViewModel by viewModels()
    private val reportViewModel: RatingReportViewModel by viewModels()
    private val reviewViewModel: RatingReviewViewModel by viewModels()
    private val ratingList = ArrayList<ReviewRating>()
    private lateinit var reviews: RatingReviews
    private lateinit var smallest: String
    private lateinit var largest: String
    var serviceData: ServiceList? = null
    private lateinit var serviceDetails: ServiceDetail
    private var mediaList = ArrayList<String>()
    private var serviceId: String? = null
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {}
    private fun initRatingAdapter() {
        binding.ratingAdapter = RatingAdapter(requireContext(), ArrayList(), onItemClick, reviews)
    }

    override fun setupViews() {
        serviceId = arguments?.getString("serviceId") ?: ""
        mViewModel.serviceCategoryDetailsRequest.serviceId = serviceId
        Log.e("TAG", "setupViews11: $serviceId")

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireActivity(), binding, serviceId = serviceId ?: "")
            clickEvents = ::onClick
        }


        mViewModel.hitServiceDetailsAPI(
            requireContext(),
            requireActivity(),
            requireActivity().isFinishing
        )
        initReviewViewModel()
        initReportViewModel()
    }

    private fun onClick(type: Int) {
        when (type) {
            0 -> {
                findNavController().navigate(
                    SubCategoryDetailsFragmentDirections.actionSubCategoryDetailsFragmentToProviderProfileFragment(
                        serviceDetails.createdBy?._id ?: "", ""
                    )
                )
            }

        }
    }

    override fun setupObservers() {
        mViewModel.serviceCategoryDetailsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            serviceDetails = it.data.result!!.serviceDetail!!
                            binding.data = serviceDetails
                            reviews = it.data.result.serviceDetail?.ratingReview!!
                            mediaList.clear()
                            mediaList.addAll(it.data.result.serviceDetail.images!!)
                            setImageAdapter(mediaList)
                            serviceDetails?.serviceMode?.let { serviceMode ->
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
                                binding.smallest.text = "ZAR " +"("+smallest.toDouble()
                                binding.largest.text = largest.toDouble().toString()+")"
                            }
//                            checkVisibility()
                            mediaList?.let {
                                Glide.with(requireContext())
                                    .load(mediaList[0])
                                    .into(binding.image2)
                            }
                            initRatingAdapter()

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

    private fun checkVisibility() {
        binding.smallest.isVisible = smallest != "0.0"
        binding.idView.isVisible = smallest != "0.0"
        binding.largest.isVisible = largest != "0.0"
    }

    private fun initReviewViewModel() {
        serviceId?.let { reviewViewModel.getReviewRequest(it) }
        reviewViewModel.getReviewData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            // showSnackBar(it.data.message!!)
                            ratingList.clear()
                            ratingList.addAll(it.data.result.ratingList)
                            initReviewAdapter()

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

    private fun initReviewAdapter() {
        binding.reviewAdapter = RatingReviewAdapter(requireContext(), ratingList, onItemClick)
    }

    private fun initReportViewModel() {
        reportViewModel.getReportRatingData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message!!)

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
        when (identifier) {
            getString(R.string.report) -> {
                val bundle = Bundle()
                bundle.putString("id", data)
                findNavController().navigate(
                    R.id.action_subCategoryDetailsFragment_to_ratingReportBottomSheetFragment,
                    bundle
                )
                // reportViewModel.getReportRatingRequest(data)
            }

            getString(R.string.openmedia) -> {
                findNavController().navigate(
                    SubCategoryDetailsFragmentDirections.actionSubCategoryDetailsFragmentToImageVideoViewFragment(
                        Gson().toJson(mediaList), getString(R.string.servicedetails), position
                    )
                )
            }
        }

    }
}