package com.example.servivet.ui.main.fragment

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_module.my_wallet.MyWalletMainResponse
import com.example.servivet.data.model.booking_module.wallte_transaction.response.WalletTransaction
import com.example.servivet.data.model.booking_module.wallte_transaction.response.WalletTransationResponse
import com.example.servivet.databinding.FragmentMyWalletBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.TransactionAdapter
import com.example.servivet.ui.main.view_model.wallet.MyWalletViewModel
import com.example.servivet.utils.AESHelper
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson

class MyWalletFragment :
    BaseFragment<FragmentMyWalletBinding, MyWalletViewModel>(R.layout.fragment_my_wallet) {
    override val binding: FragmentMyWalletBinding by viewBinding(FragmentMyWalletBinding::bind)
    override val mViewModel: MyWalletViewModel by viewModels()
    lateinit var adapter: TransactionAdapter
    private var list = ArrayList<WalletTransaction>()
    var currentPage = 1
    var isLoading: Boolean = false
    private var isBought = true
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {


    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction()

        }
        initTabLayout()

        Constants.SECURE_HEADER = "secure"
        mViewModel.hitWalletApi()

        setPagination()

    }

    private fun initTabLayout() {

        binding.idTabLayout.addTab(binding.idTabLayout.newTab().setText("Bought"))
        if(Session.type =="2") {
            binding.idTabLayout.addTab(binding.idTabLayout.newTab().setText("Sold"))
        }


        binding.idTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        isBought = true
                        initHistoryViewModel()

                    }

                    1 -> {
                        isBought = false
                        initHistoryViewModel()


                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setPagination() {
        binding.transitionRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (layoutManager!!.findLastVisibleItemPosition() == adapter.itemCount - 3) {
                    if (isLoading) {
                        currentPage++
                        Constants.SECURE_HEADER = "secure"
                        mViewModel.historyRequest.limit = 10
                        mViewModel.historyRequest.page = currentPage
                        mViewModel.historyRequest.isBought = true
                        mViewModel.request.servivet_user_req = AESHelper.encrypt(
                            Constants.SECURITY_KEY,
                            Gson().toJson(mViewModel.historyRequest)
                        )
                        mViewModel.hitWalletHistoryApi()
                    }
                    isLoading = false
                }
            }
        })
        setadapter()
    }

    fun setadapter() {
        if (list != null && list.isNotEmpty()) {
            adapter = TransactionAdapter(ArrayList(), requireContext())
            val layoutManager = LinearLayoutManager(requireContext())
            binding.transitionRecycler.layoutManager = layoutManager
            binding.transitionRecycler.itemAnimator = null
            binding.transitionRecycler.adapter = adapter
            binding.transitionRecycler.visibility = View.VISIBLE
            binding.noDataLayout.visibility = View.GONE
        } else {
            binding.transitionRecycler.visibility = View.GONE
            binding.noDataLayout.visibility = View.VISIBLE
        }
    }

    override fun setupObservers() {
        initHistoryViewModel()
        mViewModel.getWalletData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data),
                        MyWalletMainResponse::class.java
                    )
                    Log.e("TAG", "setupObservers: " + Gson().toJson(data))
                    when (data.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {

                            binding.apiData = data.result.myWallet
                            Constants.SECURE_HEADER = " "
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(data.message)
                            Constants.SECURE_HEADER = " "
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
                        Constants.SECURE_HEADER = " "

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

    private fun initHistoryViewModel() {

        mViewModel.getHistoryRequest(isBought)
        mViewModel.getWalletHistoryData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    val data = Gson().fromJson(
                        AESHelper.decrypt(Constants.SECURITY_KEY, it.data),
                        WalletTransationResponse::class.java
                    )
                    when (data.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            if (data.result.walletTransaction != null && data.result.walletTransaction.isNotEmpty()) {
//                                mViewModel.list.cle
                                binding.transitionRecycler.isVisible = true

                                isLoading = true
                                if (currentPage == 1)
                                    list = ArrayList()

                                list = data.result.walletTransaction

                                if (currentPage == 1 && list.size > 0) {
                                    adapter = TransactionAdapter(list, requireContext())
                                    binding.transitionRecycler.adapter = adapter
                                } else {
                                    adapter.updateList(list)
                                }

                                if (list.isNotEmpty()) {
                                    binding.transitionRecycler.visibility = View.VISIBLE
                                    binding.noDataLayout.visibility = View.GONE
                                } else {
                                    binding.transitionRecycler.visibility = View.GONE
                                    binding.noDataLayout.visibility = View.VISIBLE
                                }
                            }else{
                               binding.transitionRecycler.isVisible = false

                            }

                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(data.message)
                            Constants.SECURE_HEADER = " "
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
                        Constants.SECURE_HEADER = " "

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

}