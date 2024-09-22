package com.example.servivet.ui.main.fragment

import PaginationScrollListener
import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.home.response.HomeSubCategory
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.FragmentServicesTypeListingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.ServiceListAdapter
import com.example.servivet.ui.main.adapter.ServiceSubCatAdapter
import com.example.servivet.ui.main.view_model.ServicesTypeViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson


@Suppress("DEPRECATION")
class ServicesTypeListingFragment :
    BaseFragment<FragmentServicesTypeListingBinding, ServicesTypeViewModel>(
        R.layout.fragment_services_type_listing
    ),
    ServiceSubCatAdapter.SubCategoryAdapterInterFace {
    private var tabPosition: Int = 0
    var isFirst = true
    var subCatList = ArrayList<HomeSubCategory>()
    override val binding: FragmentServicesTypeListingBinding by viewBinding(
        FragmentServicesTypeListingBinding::bind
    )
    override val mViewModel: ServicesTypeViewModel by viewModels()
    lateinit var adapter: ServiceListAdapter
    private var list = ArrayList<ServiceList>()
    var currentPage = 1
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var data: HomeServiceCategory? = null
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            binding.searchTab.setOnClickListener {
                binding.idSearchLayout.isVisible = true
                binding.idTopLayout.isVisible = false
            }
            binding.closeSearch.setOnClickListener {
                binding.idSearchLayout.isVisible = false
                binding.idTopLayout.isVisible = true
                mViewModel.hitServiceListAPI(
                    requireContext(),
                    requireActivity(),
                    requireActivity().isFinishing
                )

            }
            click = mViewModel.ClickAction(requireContext(), binding)
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
        data = arguments?.getSerializable(Constants.DATA) as HomeServiceCategory

        mViewModel.serviceListRequest.category = data!!.id
        mViewModel.serviceListRequest.isMyService = 0
        mViewModel.serviceListRequest.providerId = ""
        mViewModel.serviceListRequest.bussinessType = 3
        mViewModel.serviceListRequest.limit = 10
        mViewModel.serviceListRequest.search = ""
        mViewModel.serviceListRequest.page = currentPage
        binding.catName.text = data!!.name
        tabPosition = 0
        /*  if (data!!.subCategory != null && data!!.subCategory!!.isNotEmpty()) {
              mViewModel.serviceListRequest.subCategory = data!!.subCategory!![0].id*/
        initEditText()
        mViewModel.hitServiceListAPI(
            requireContext(),
            requireActivity(),
            requireActivity().isFinishing
        )

        tabSelect()
        for (i in data!!.subCategory!!.indices) {
            if (data!!.subCategory!![i].status == 1) {
                subCatList.clear()

                subCatList.addAll(data!!.subCategory!!)
            }
        }
        Log.e("TAG", "setupViews: " + Gson().toJson(subCatList))
        setSubCatAdapter(subCatList)
    }

    private fun initEditText() {
        binding.idSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapter.isInitialized) {
                    if (adapter.filter(s.toString()).isNotEmpty()) {
                        binding.noDataLayout.visibility = View.GONE
                        binding.serviceRecycler.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    } else {
                        //show no data found
                        binding.serviceRecycler.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun setSubCatAdapter(list: ArrayList<HomeSubCategory>) {
        list.add(0, HomeSubCategory("", "", "", "", "All", 0))
        if (list.isNotEmpty())
            binding.serviceSubCatRecycler.adapter =
                ServiceSubCatAdapter(requireContext(), list, this)
    }


    private fun tabSelect() {
        setAdapter(0)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.individual)))
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.institutional))
        )
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabPosition = tab.position
                when (tab.position) {
                    0 -> {
                        setAdapter(0)
                        mViewModel.serviceListRequest.bussinessType = 3
                        mViewModel.serviceListRequest.page = 1
                        mViewModel.hitServiceListAPI(
                            requireContext(),
                            requireActivity(),
                            requireActivity().isFinishing
                        )
                    }

                    1 -> {
                        setAdapter(1)
                        mViewModel.serviceListRequest.bussinessType = 4
                        mViewModel.serviceListRequest.page = 1
                        mViewModel.hitServiceListAPI(
                            requireContext(),
                            requireActivity(),
                            requireActivity().isFinishing
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun setAdapter(tabPosition: Int) {
        if (list != null && list.isNotEmpty()) {
            adapter = ServiceListAdapter(requireContext(), tabPosition, ArrayList())
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
                    if (tabPosition == 0) {
                        mViewModel.serviceListRequest.bussinessType = 3
                        mViewModel.serviceListRequest.page = currentPage
                        mViewModel.hitServiceListAPI(
                            requireContext(),
                            requireActivity(),
                            requireActivity().isFinishing
                        )
                    } else if (tabPosition == 1) {
                        mViewModel.serviceListRequest.bussinessType = 4
                        mViewModel.serviceListRequest.page = currentPage
                        mViewModel.hitServiceListAPI(
                            requireContext(),
                            requireActivity(),
                            requireActivity().isFinishing
                        )
                    }
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
                                Log.e(
                                    "TAG",
                                    "setupObservers1234321: ${Gson().toJson(it.data.result.service)}",
                                )
                                isLoading = true
                                if (currentPage == 1)
                                    list = ArrayList()
                                list = it.data.result.service
                                if (currentPage == 1 && list.size > 0) {
                                    adapter =
                                        ServiceListAdapter(requireContext(), tabPosition, list)
                                    binding.serviceRecycler.adapter = adapter
                                } else {
                                    adapter.updateList(list)
                                }
                                binding.serviceRecycler.visibility = View.VISIBLE
                                binding.noDataLayout.visibility = View.GONE

                            } else {
                                binding.serviceRecycler.visibility = View.GONE
                                binding.noDataLayout.visibility = View.VISIBLE
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

    override fun onSubCatSelected(id: String) {
        mViewModel.serviceListRequest.subCategory = id
        mViewModel.serviceListRequest.page = 1
        mViewModel.serviceListRequest.providerId = ""
        setAdapter(tabPosition)
        mViewModel.hitServiceListAPI(
            requireContext(),
            requireActivity(),
            requireActivity().isFinishing
        )
    }
}