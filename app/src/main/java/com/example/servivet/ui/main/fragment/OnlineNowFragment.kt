package com.example.servivet.ui.main.fragment


import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.home.response.nearbyprovider.NearByProviderResponse
import com.example.servivet.data.model.home.response.nearbyprovider.Provider
import com.example.servivet.databinding.FragmentOnlineNowBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.NearByProviderAdapter
import com.example.servivet.ui.main.view_model.OnlineNowViewModel
import com.example.servivet.utils.Session
import com.example.servivet.utils.SocketManager
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OnlineNowFragment :
    BaseFragment<FragmentOnlineNowBinding, OnlineNowViewModel>(R.layout.fragment_online_now) {
    override val binding: FragmentOnlineNowBinding by viewBinding(FragmentOnlineNowBinding::bind)
    override val mViewModel: OnlineNowViewModel by viewModels()
    private val type = 2
    private var providerList = ArrayList<Provider>()
    private var isLoading = false
    private lateinit var socket: Socket

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
        // setPagination()
        initSocket()
        binding.nearByProviderRecycle.isVisible = true
    }

    private fun initSocket() {
        try {
            SocketManager.connect()
            socket = SocketManager.getSocket()
            Log.e("TAG", "initSocketCheckConnect: ${socket.connected()}")
            Log.e("TAG", "checkLocationInfo: ${Session.saveLocationInfo}")
            Log.e("TAG", "initSocket:${socket.connected()} ")

            val data = JSONObject()
            data.put("userId", Session.userDetails._id)
            data.put("latitude", Session.saveLocationInfo?.latitude?.toDouble() ?: "")
            data.put("longitude", Session.saveLocationInfo?.longitude?.toDouble() ?: "")
            data.put("page", 1)
            data.put("limit", 1000)

            socket.emit("nearByProvider", data)
            socket.on("nearByProvider", fun(args: Array<Any?>) {
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        val providerData = args[0] as JSONObject
                        try {
                          val  nearByProviderResponse = Gson().fromJson(
                                JSONArray().put(providerData)[0].toString(),
                                NearByProviderResponse::class.java
                            )
                            Log.e(
                                "TAG",
                                "nearByProviderResponse:${Gson().toJson(nearByProviderResponse)} ",
                            )
                            providerList.clear()
                            nearByProviderResponse.result?.providerList?.let {
                                if (isAdded) {
                                    providerList.addAll(it)
                                    setAdapter()
                                }
                            }
                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                    }
                }
            })

        } catch (ex: Exception) {

        }


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

//    private fun getArgumentData() {
//        if (argumentData.from == getString(R.string.home_fr)) {
//            val data = Gson().fromJson(argumentData.data, NearByProviderResponse::class.java)
//            providerList.clear()
//            providerList.addAll(data.result?.providerList?: emptyList())
//            setAdapter()
//        }
//    }

    fun setAdapter() {
        binding.adapter = NearByProviderAdapter(requireContext(), providerList, onItemClick, type)
    }

    override fun setupObservers() {
    }

    private val onItemClick: (String, String) -> Unit = { identifire, data ->
        findNavController().navigate(
            OnlineNowFragmentDirections.actionOnlineNowFragmentToProviderProfileFragment(
                data,
                getString(R.string.onlinefr)
            )
        )
    }
}