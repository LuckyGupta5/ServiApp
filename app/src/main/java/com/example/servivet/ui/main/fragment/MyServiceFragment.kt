package com.example.servivet.ui.main.fragment

import PaginationScrollListener
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.FragmentMyServiceBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.adapter.MyServiceCatAdapter
import com.example.servivet.ui.main.adapter.MyServiceAdapter
import com.example.servivet.ui.main.adapter.ServiceSubCatAdapter
import com.example.servivet.ui.main.view_model.MyServiceViewModel
import com.example.servivet.ui.main.view_model.booking_models.CloseServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
@RequiresApi(Build.VERSION_CODES.O)
@Suppress("DEPRECATION")
class MyServiceFragment :
    BaseFragment<FragmentMyServiceBinding, MyServiceViewModel>(R.layout.fragment_my_service),
    ServiceSubCatAdapter.SubCategoryAdapterInterFace,
    MyServiceCatAdapter.SubCategoryAdapterInterFace {
    private var tabPosition: Int = 0
    override val binding: FragmentMyServiceBinding by viewBinding(FragmentMyServiceBinding::bind)
    override val mViewModel: MyServiceViewModel by viewModels()
    private val closeServiceModel: CloseServiceViewModel by viewModels()
    private val argumentData: MyServiceFragmentArgs by navArgs()
    lateinit var adapter: MyServiceAdapter
    private var list = ArrayList<ServiceList>()
    var currentPage = 1
    private lateinit var listN: CallBack1
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    private var isBook = false
    var data: ArrayList<HomeServiceCategory>? = null
    override fun isNetworkAvailable(boolean: Boolean) {
    }


    override fun setupViewModel() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun bottomSheetCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(getString(R.string.leave))
            ?.observe(viewLifecycleOwner) {

                if(!it){
                    binding.idCloseService.text = getString(R.string.start_service)
                    binding.idCloseService.isEnabled = true

                }else{
                    binding.idCloseService.isEnabled = true

                }


            }
    }

    override fun setupViews() {
        listN = requireActivity() as CallBack1
        binding.apply {
            click = mViewModel.ClickAction(requireContext(), binding)
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            clickEvent = ::onClick
        }
        data = Session.category


        if (argumentData.from == getString(R.string.provider_profile)) {
            binding.catName.text = "Services"
            mViewModel.serviceListRequest.category = data!![0].id
            mViewModel.serviceListRequest.isMyService = 0
            mViewModel.serviceListRequest.providerId = argumentData.data
            binding.idServiceLayout.isVisible = false
            isBook = true
        } else {
            mViewModel.serviceListRequest.category = data!![0].id
            mViewModel.serviceListRequest.isMyService = 1
            binding.idServiceLayout.isVisible = true
            isBook = false
        }

        mViewModel.serviceListRequest.bussinessType = 3
        mViewModel.serviceListRequest.limit = 10
        mViewModel.serviceListRequest.search = ""
        mViewModel.serviceListRequest.page = currentPage
        tabPosition = 0
        if (data != null && data!!.isNotEmpty()) {
            mViewModel.hitServiceListAPI(
                requireContext(),
                requireActivity(),
                requireActivity().isFinishing
            )
        }
        initEditText()
        initCloseServiceModel()
        setSubCatAdapter(data!!)
        onBackCall()
        bottomSheetCallBack()

        checkServiceAvaliable()
    }


    private fun checkServiceAvaliable() {
        if (Session.userDetails.isServiceEnable) {
            binding.idCloseService.text = getString(R.string.close_service)
        } else {
            binding.idCloseService.text = getString(R.string.start_service)

        }
    }

    private fun initEditText() {
        binding.idSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setSubCatAdapter(list: ArrayList<HomeServiceCategory>) {
        if (list.isNotEmpty())
            binding.serviceSubCatRecycler.adapter =
                MyServiceCatAdapter(requireContext(), list, this)
    }

    private fun onBackCall() {
        binding.backBtn.setOnClickListener {
            requireActivity().finish()
            var intent = Intent(context, HomeActivity::class.java)
            requireActivity().startActivity(intent)
            listN.callBack()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                @SuppressLint("SetTextI18n")
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                    var intent = Intent(context, HomeActivity::class.java)
                    requireActivity().startActivity(intent)
                    listN.callBack()

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    interface CallBack1 {
        fun callBack()
    }


    private fun setAdapter(tabPosition: Int) {
        if (list != null && list.isNotEmpty()) {
            adapter = MyServiceAdapter(requireContext(), tabPosition, ArrayList(), isBook)
            val layoutManager = LinearLayoutManager(requireContext())
            binding.serviceRecycler.layoutManager = layoutManager
            binding.serviceRecycler.adapter = adapter
            binding.serviceRecycler.visibility = View.VISIBLE
            binding.noDataLayout.visibility = View.GONE
            binding.serviceRecycler.addOnScrollListener(object :
                PaginationScrollListener(layoutManager) {
                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun loadMoreItems() {
                    isLoading = true
                    currentPage++
                    mViewModel.hitServiceListAPI(
                        requireContext(),
                        requireActivity(),
                        requireActivity().isFinishing
                    )
                }
            })
        } else {
            binding.serviceRecycler.visibility = View.GONE
            binding.noDataLayout.visibility = View.VISIBLE
        }
    }




    override fun setupObservers() {
        mViewModel.serviceListResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            if (it.data.result.service.isNotEmpty()) {

                           //     binding.idServiceLayout.isVisible = true
                                isLoading = true
                                if (currentPage == 1)
                                    list = ArrayList()
                                list = it.data.result.service
                                if (currentPage == 1 && list.size > 0) {
                                    adapter = MyServiceAdapter(requireContext(), tabPosition, list,isBook)
                                    binding.serviceRecycler.adapter = adapter
                                } else {
                                    adapter.updateList(list)
                                }
                                binding.serviceRecycler.visibility = View.VISIBLE
                                binding.noDataLayout.visibility = View.GONE

                            } else {
                                binding.serviceRecycler.visibility = View.GONE
                                binding.noDataLayout.visibility = View.VISIBLE
                                binding.idServiceLayout.isVisible = false
                            }
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

    private fun initCloseServiceModel() {
        closeServiceModel.getMarkAsCompleteData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            binding.idCloseService.text = getString(R.string.close_service)

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            Log.e("TAG", "setupObservers: botDonne")

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

    override fun onSubCatSelected(id: String) {
        mViewModel.serviceListRequest.category = id
        mViewModel.serviceListRequest.page = 1
        setAdapter(tabPosition)
        mViewModel.hitServiceListAPI(requireContext(), requireActivity(), requireActivity().isFinishing)
    }

    private fun onClick(type: String) {
        when (type) {
            getString(R.string.add_service) -> {
                findNavController().navigate(R.id.action_myServiceFragment_to_addServiceFragment)
            }

            getString(R.string.close_service) -> {
                if (binding.idCloseService.text.toString() == getString(R.string.close_service)) {
                    binding.idCloseService.isEnabled = false
                    findNavController().navigate(R.id.action_myServiceFragment_to_closeServiceBottomFragment)
                } else {
                    closeServiceModel.getMarkAsCompleteRequest("", "", true)
                }
            }

        }
    }

}