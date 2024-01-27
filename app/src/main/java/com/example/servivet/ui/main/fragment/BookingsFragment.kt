package com.example.servivet.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.databinding.FragmentBookingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.BookingAdapter
import com.example.servivet.ui.main.adapter.BookingListAdapter
import com.example.servivet.ui.main.bottom_sheet.FragmentRatingUsBottomSheet
import com.example.servivet.ui.main.view_model.BookingViewModel
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson


class BookingsFragment :
    BaseFragment<FragmentBookingBinding, BookingViewModel>(R.layout.fragment_booking){
    private var tabPosition: TabLayout.Tab? = null
    override val binding: FragmentBookingBinding by viewBinding(FragmentBookingBinding::bind)
    override val mViewModel: BookingViewModel by viewModels()
    var type: Int = 0
    lateinit var adapter: BookingAdapter
    private var list = ArrayList<MyBooking>()
    private var bookingList = ArrayList<MyBooking>()
    var currentPage = 1
    var isLoading: Boolean = false
    var typeReschdule: Int = 0
    private var myBookingStatus = 1

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: call", )
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(requireContext(), binding)
        }

        type = 0
        typeReschdule = 0


        if (Session.type == "1") {
            mViewModel.hitBookingListAPI(myBookingStatus, 1, 10)
            Constants.TYPEOFUSERS = "Bought"
         //   setPagination()
        } else if (Session.type == "2") {
            list.clear()
            binding.soldOut.isVisible = true
            Constants.TYPEOFUSERS = "sold"
            mViewModel.hitSoldBookingListAPI(myBookingStatus, 1, 10)
         //   setPagination()
        }

        setBack()
        settablayout()
    }

    override fun setupViewModel() {

    }

    fun settablayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.pending)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.upcoming))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.completed))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.cancelled))
        binding.tabLayout.getTabAt(0)?.select()
        // val tabLayout = TabLayout(requireContext())
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (binding.tabLayout.selectedTabPosition == 0) {
                    tabPosition = tab
                    type = 0
                    mViewModel.bookingStatus = 1
                    typeReschdule = 0
                    list.clear()
                    if (Session.type == "1") {
                        mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)
                       // setPagination()
                    } else if (Session.type == "2") {
                        if (Constants.TYPEOFUSERS== "bought") {
                            mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)

                        } else {
                            mViewModel.hitSoldBookingListAPI(mViewModel.bookingStatus, 1, 10)
                        }
                       // setPagination()
                    }
//                    mViewModel.hitBookingListAPI(1,1,10)
//                    setPagination()

                } else if (binding.tabLayout.selectedTabPosition == 1) {
                    type = 1
                    typeReschdule = 1
                    mViewModel.bookingStatus = 2
                    list.clear()
                    if (Session.type == "1") {
                        mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)
                       // setPagination()
                    } else if (Session.type == "2") {
                        if (Constants.TYPEOFUSERS == "bought") {
                            mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)

                        } else {
                            mViewModel.hitSoldBookingListAPI(mViewModel.bookingStatus, 1, 10)
                        }
                  //      setPagination()
                    }
//                    mViewModel.hitBookingListAPI(2,1,10)
//                    setPagination()

                } else if (binding.tabLayout.selectedTabPosition == 2) {
                    type = 2
                    mViewModel.bookingStatus = 3
                    typeReschdule = 2
                    list.clear()
                    if (Session.type == "1") {
                        mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)
                       // setPagination()
                    } else if (Session.type == "2") {
                        if (Constants.TYPEOFUSERS == "bought") {
                            mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)

                        } else {
                            mViewModel.hitSoldBookingListAPI(mViewModel.bookingStatus, 1, 10)
                        }
   //                     setPagination()
                    }
//                    mViewModel.hitBookingListAPI(3,1,10)
//                    setPagination()
                } else if (binding.tabLayout.selectedTabPosition == 3) {
                    type = 3
                    typeReschdule = 3
                    mViewModel.bookingStatus = 0
                    list.clear()
                    if (Session.type == "1") {
                        mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)
  //                      setPagination()
                    } else if (Session.type == "2") {
                        list.clear()
                        if (Constants.TYPEOFUSERS == "bought") {
                            mViewModel.hitBookingListAPI(mViewModel.bookingStatus, 1, 10)
                        } else {
                            mViewModel.hitSoldBookingListAPI(mViewModel.bookingStatus, 1, 10)
                        }
  //                      setPagination()
                    }
//                    mViewModel.hitBookingListAPI(0,1,10)
//                    setPagination()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun setPagination() {
        binding.idBookingRecycle.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (layoutManager!!.findLastVisibleItemPosition() == adapter.itemCount - 3) {
                    if (isLoading) {
                        currentPage++
                        mViewModel.hitBookingListAPI(type, currentPage, 10)
                    }
                    isLoading = false
                }
            }
        })
      //  setBookingAdapter()
    }

    fun setBookingAdapter() {
        if (list != null && list.isNotEmpty()) {
           // adapter = BookingAdapter(requireContext(), type, typeReschdule, ArrayList(), , mViewModel.typeOfUser)
          //  val layoutManager = LinearLayoutManager(requireContext())
           // binding.idBookingRecycle.layoutManager = layoutManager
            binding.idBookingRecycle.itemAnimator = null
            binding.idBookingRecycle.adapter = adapter
            binding.idBookingRecycle.visibility = View.VISIBLE
            binding.noDataLayout.visibility = View.GONE


        } else {
            binding.idBookingRecycle.visibility = View.GONE
            binding.noDataLayout.visibility = View.VISIBLE
        }

    }


    override fun setupObservers() {
        mViewModel.bookingListResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            bookingList.clear()
                            bookingList.addAll(it.data.result.myBookingList)
                            Log.e("TAG", "setupObserversbooknng: ${Gson().toJson(it.data.result.myBookingList.size)}",)

                            setMyBookingAdapter()
//                            if (it.data.result.myBookingList != null && it.data.result.myBookingList.isNotEmpty()) {
////                                mViewModel.list.clear
//                                isLoading = true
//
//                                if (currentPage == 1)
//                                    list = ArrayList()
//
//                                list = it.data.result.myBookingList
//
//                                if (currentPage == 1 && list.size > 0) {
//                                    Log.e("TAG", "setupObservers: ${Gson().toJson(list)}")
//                                    adapter = BookingAdapter(requireContext(), type, typeReschdule, list, this,mViewModel.typeOfUser)
//                                    binding.recyclercategry.adapter = adapter
//                                } else {
//                                    adapter.updateList(list)
//                                }
//
//                                if (list.isNotEmpty()) {
//                                    binding.recyclercategry.visibility = View.VISIBLE
//                                    binding.noDataLayout.visibility = View.GONE
//                                } else {
//                                    binding.recyclercategry.visibility = View.GONE
//                                    binding.noDataLayout.visibility = View.VISIBLE
//                                }
//                            }
////                            else {
////                                list.clear()
////                                binding.recyclercategry.adapter?.notifyDataSetChanged()
////
////                            }
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

                else -> {}
            }
        }
        mViewModel.soldBookingListResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Log.e(
                                "TAG", "setupObservers: ${Gson().toJson(it.data.result.mySoldBookingList)}",
                            )

                            bookingList.clear()
                            bookingList.addAll(it.data.result.mySoldBookingList)
                            setMyBookingAdapter()

                          /*  if (it.data.result.mySoldBookingList != null && it.data.result.mySoldBookingList.isNotEmpty()) {
//                                mViewModel.list.cle
                                isLoading = true

                                if (currentPage == 1)
                                    list = ArrayList()

                                list = it.data.result.mySoldBookingList

                                if (currentPage == 1 && list.size > 0) {
                                    adapter = BookingAdapter(requireContext(), type, typeReschdule, list, this,mViewModel.typeOfUser)
                                    binding.idBookingRecycle.adapter = adapter
                                } else {
                                    adapter.updateList(list)
                                }

                                if (list.isNotEmpty()) {
                                    binding.idBookingRecycle.visibility = View.VISIBLE
                                    binding.noDataLayout.visibility = View.GONE
                                } else {
                                    binding.idBookingRecycle.visibility = View.GONE
                                    binding.noDataLayout.visibility = View.VISIBLE
                                }
                            }*/
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

                else -> {}
            }
        }

        mViewModel.cancelBookingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            binding.tabLayout.getTabAt(3)!!.select()

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

                else -> {}
            }
        }

        mViewModel.acceptBookingResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()

                    when (it.data!!.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showSnackBar(it.data.message)
                            binding.tabLayout.getTabAt(1)!!.select()

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

                else -> {}
            }
        }

    }

    private fun setMyBookingAdapter() {
        binding.bookingAdapter = BookingListAdapter(requireContext(),bookingList,onItemClick,type,findNavController(),Constants.TYPEOFUSERS)
    }

    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_bookingsFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

//    override fun onCallback(type: String) {
//        val fragment = FragmentRatingUsBottomSheet()
//        fragment.show(childFragmentManager, "InterestBottomSheetFragment")
//    }
//
//    override fun rejectBooking(id: String) {
//        mViewModel.cancelBookingRequest.bookingId = id
//        mViewModel.cancelBookingRequest.reason = "I don't need this service now"
//        mViewModel.cancelBookingRequest.cancelledBy = "user"
//        mViewModel.hitCancelBookingApi()
//    }
//
//    override fun acceptBooking(id: String) {
//        mViewModel.acceptBookingRequest.bookingId = id
//        mViewModel.hitAcceptBookingApi()
//
//    }

    private val onItemClick: (String, String, String) -> Unit = { position, name, data ->
        when (name) {

        }
    }

}