package com.example.servivet.ui.main.fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.review_ratinng.ReviewRating
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
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class SubCategoryDetailsFragment : BaseFragment<FragmentSubCategoryDetailsBinding, SubCategoryDetailsViewModel>(R.layout.fragment_sub_category_details) {
    override val binding: FragmentSubCategoryDetailsBinding by viewBinding(FragmentSubCategoryDetailsBinding::bind)
    override val mViewModel: SubCategoryDetailsViewModel by viewModels()
    private val reportViewModel:RatingReportViewModel by viewModels()
    private val reviewViewModel:RatingReviewViewModel by viewModels()
    private val ratingList = ArrayList<ReviewRating>()
    var serviceData: ServiceList?=null
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {


    }




    private fun initRatingAdapter() {
        binding.ratingAdapter = RatingAdapter(requireContext(), ArrayList(),onItemClick)

    }

    override fun setupViews() {

        serviceData= arguments?.getSerializable(Constants.DATA) as ServiceList?
        mViewModel.serviceCategoryDetailsRequest.serviceId=serviceData!!._id

        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireActivity(),binding,serviceData!!._id!!)
        }


        mViewModel.hitServiceDetailsAPI(requireContext(),requireActivity(),requireActivity().isFinishing)
        initRatingAdapter()
        initReviewViewModel()
        initReportViewModel()
    }

    override fun setupObservers() {
        mViewModel.serviceCategoryDetailsResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            binding.data=it.data.result!!.serviceDetail
                            setImageAdapter(it.data.result.serviceDetail!!.images!!)
                            val smallest: String = min(it.data.result.serviceDetail.atCenterPrice?:0.0, it.data.result.serviceDetail.atHomePrice?:0.0).toString()
                            val largest: String = max(it.data.result.serviceDetail.atCenterPrice?:0.0,it.data.result.serviceDetail.atHomePrice?:0.0).toString()
                            binding.smallest.text=commaSaparator(smallest.toDouble()).toString()
                            binding.largest.text=commaSaparator(largest.toDouble()).toString()
                            if(it.data.result.serviceDetail.images!!.isNotEmpty()&&it.data.result.serviceDetail.images!=null)
                                 Glide.with(requireContext()).load(it.data.result.serviceDetail!!.images!![0]).into(binding.image2)
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

    private fun initReviewViewModel() {
        serviceData!!._id?.let { reviewViewModel.getReviewRequest(it) }
        reviewViewModel.getReviewData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                           // showSnackBar(it.data.message!!)
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
                    CommonUtils.logoutAlert(requireContext(), "Session Expired", "Unauthorized User", requireActivity())
                }
            }
        }    }

    private fun initReviewAdapter() {
        binding.reviewAdapter = RatingReviewAdapter(requireContext(),ratingList,onItemClick)
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
                    CommonUtils.logoutAlert(requireContext(), "Session Expired", "Unauthorized User", requireActivity())
                }
            }
        }
    }


    private fun setImageAdapter(list: ArrayList<String>) {
        if (list != null && list.isNotEmpty()) {
            binding.imageRecycler.visibility = View.VISIBLE
            binding.imageRecycler.adapter=ServiceDetailsImgAdapter(requireContext(),list)
        } else {
            binding.imageRecycler.visibility = View.GONE
        }
    }
    fun commaSaparator(number: Double?): String? {
        val formatter = DecimalFormat("#,###,###")
        return formatter.format(number)
    }

    private val onItemClick:(String, String)->Unit = { identifier, data->
        when(identifier){
            getString(R.string.report)->{
                findNavController().navigate(R.id.action_subCategoryDetailsFragment_to_ratingReportBottomSheetFragment)
               // reportViewModel.getReportRatingRequest(data)
            }
        }

    }
}