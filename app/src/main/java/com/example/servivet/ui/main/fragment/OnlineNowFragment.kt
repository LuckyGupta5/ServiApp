package com.example.servivet.ui.main.fragment


import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.response.nearbyprovider.NearByProviderResponse
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.databinding.FragmentOnlineNowBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.HomeOnlineNowAdapter
import com.example.servivet.ui.main.adapter.NearByProviderAdapter
import com.example.servivet.ui.main.view_model.OnlineNowViewModel
import com.google.gson.Gson

class OnlineNowFragment :
    BaseFragment<FragmentOnlineNowBinding, OnlineNowViewModel>(R.layout.fragment_online_now) {
    override val binding: FragmentOnlineNowBinding by viewBinding(FragmentOnlineNowBinding::bind)
    override val mViewModel: OnlineNowViewModel by viewModels()
    private val argumentData: OnlineNowFragmentArgs by navArgs()
    private val type = 2
    private var providerList = ArrayList<Provider>()
    private var isLoading = false


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
        getArgumentData()
       // setPagination()
        binding.nearByProviderRecycle.isVisible = true
    }

    private fun setPagination() {

        binding.nearByProviderRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                  //  loadMoreItems()
                    isLoading = true
                }
            }
        })
    }

    private fun getArgumentData() {
        if (argumentData.from == getString(R.string.home_fr)) {
            val data = Gson().fromJson(argumentData.data, NearByProviderResponse::class.java)
            providerList.clear()
            providerList.addAll(data.result.providerList)
            setAdapter()

        }
    }

    fun setAdapter() {
        binding.adapter = NearByProviderAdapter(requireContext(), providerList, onItemClick, type)

    }

    override fun setupObservers() {
    }

    private val onItemClick: (String, String) -> Unit = { identifire, data ->
        findNavController().navigate(OnlineNowFragmentDirections.actionOnlineNowFragmentToProviderProfileFragment(data, getString(R.string.onlinefr)))
    }
}