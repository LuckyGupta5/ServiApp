package com.example.servivet.ui.main.fragment

import android.content.Intent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.booking_list.response.MyBooking
import com.example.servivet.data.model.service_list.response.ServiceList
import com.example.servivet.databinding.FragmentBookingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.BookingAdapter
import com.example.servivet.ui.main.adapter.MyServiceAdapter
import com.example.servivet.ui.main.adapter.SoldBookingAdapter
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


class BookingsFragment : BaseFragment<FragmentBookingBinding,BookingViewModel>(R.layout.fragment_booking), BookingAdapter.Callback {
    private var tabPosition: TabLayout.Tab?=null
    override val binding: FragmentBookingBinding by viewBinding(FragmentBookingBinding::bind)
    override val mViewModel: BookingViewModel by viewModels()
     var type:Int=0
    lateinit var adapter: BookingAdapter
    private var list = ArrayList<MyBooking>()
    var currentPage = 1
    var isLoading: Boolean = false
    var typeReschdule:Int=0

    override fun isNetworkAvailable(boolean: Boolean) {
    }
    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction(requireContext(),binding)

        }

        type=0
        typeReschdule=0
        if(Session.type=="1"){
            mViewModel.hitBookingListAPI(1,1,10)
            setPagination()
        }else  if(Session.type=="2"){
            list.clear()
            mViewModel.hitSoldBookingListAPI(1,1,10)
            setPagination()
        }

        setBack()
        settablayout()
    }

    override fun setupViewModel() {

    }

    fun settablayout(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.pending)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.upcoming))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.completed))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.cancelled))
        binding.tabLayout.getTabAt(0)?.select()
       // val tabLayout = TabLayout(requireContext())
       binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(binding.tabLayout.selectedTabPosition == 0){
                   tabPosition=tab
                    type=0
                    typeReschdule=0
                    list.clear()
                    if(Session.type=="1"){
                        mViewModel.hitBookingListAPI(1,1,10)
                        setPagination()
                    }else  if(Session.type=="2"){
                        list.clear()
                        mViewModel.hitSoldBookingListAPI(1,1,10)
                        setPagination()
                    }
//                    mViewModel.hitBookingListAPI(1,1,10)
//                    setPagination()

                }
                else if(binding.tabLayout.selectedTabPosition == 1){
                    type=1
                    typeReschdule=1
                    list.clear()
                    if(Session.type=="1"){
                        mViewModel.hitBookingListAPI(1,1,10)
                        setPagination()
                    }else  if(Session.type=="2"){
                        list.clear()
                        mViewModel.hitSoldBookingListAPI(1,1,10)
                        setPagination()
                    }
//                    mViewModel.hitBookingListAPI(2,1,10)
//                    setPagination()

                }
                else if(binding.tabLayout.selectedTabPosition==2){
                    type=2
                    typeReschdule=2
                    list.clear()
                    if(Session.type=="1"){
                        mViewModel.hitBookingListAPI(1,1,10)
                        setPagination()
                    }else  if(Session.type=="2"){
                        list.clear()
                        mViewModel.hitSoldBookingListAPI(1,1,10)
                        setPagination()
                    }
//                    mViewModel.hitBookingListAPI(3,1,10)
//                    setPagination()
                }
                else if(binding.tabLayout.selectedTabPosition==3){
                    type=3
                    typeReschdule=3
                    list.clear()
                    if(Session.type=="1"){
                        mViewModel.hitBookingListAPI(1,1,10)
                        setPagination()
                    }else  if(Session.type=="2"){
                        list.clear()
                        mViewModel.hitSoldBookingListAPI(1,1,10)
                        setPagination()
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
        binding.recyclercategry.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (layoutManager!!.findLastVisibleItemPosition() == adapter.itemCount - 3) {
                    if (isLoading) {
                        currentPage++
                        mViewModel.hitBookingListAPI(type,currentPage,10)
                    }
                    isLoading = false
                }
            }
        })
        setBookingAdapter()
    }

   fun setBookingAdapter(){
       if (list != null && list.isNotEmpty()) {
           adapter = BookingAdapter(requireContext(),type,typeReschdule, ArrayList(),this)
           val layoutManager = LinearLayoutManager(requireContext())
           binding.recyclercategry.layoutManager = layoutManager
           binding.recyclercategry.itemAnimator = null
           binding.recyclercategry.adapter = adapter
           binding.recyclercategry.visibility = View.VISIBLE
           binding.noDataLayout.visibility = View.GONE


       } else {
           binding.recyclercategry.visibility = View.GONE
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
                          if (it.data.result.myBookingList!=null && it.data.result.myBookingList.isNotEmpty()) {
//                                mViewModel.list.cle
                              isLoading = true

                              if (currentPage == 1)
                                  list = ArrayList()

                              list = it.data.result.myBookingList

                              if (currentPage == 1 && list.size > 0) {
                                  adapter = BookingAdapter(requireContext(),type,typeReschdule,list,this)
                                  binding.recyclercategry.adapter = adapter
                              } else {
                                  adapter.updateList(list)
                              }

                              if (list.isNotEmpty()) {
                                  binding.recyclercategry.visibility = View.VISIBLE
                                  binding.noDataLayout.visibility = View.GONE
                              } else {
                                  binding.recyclercategry.visibility = View.GONE
                                  binding.noDataLayout.visibility = View.VISIBLE
                              }
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

              else -> {}
          }
      }
      mViewModel.soldBookingListResponse.observe(viewLifecycleOwner) {
          when (it.status) {
              Status.SUCCESS -> {
                  ProcessDialog.dismissDialog()

                  when (it.data!!.code) {
                      StatusCode.STATUS_CODE_SUCCESS -> {
                          if (it.data.result.mySoldBookingList!=null && it.data.result.mySoldBookingList.isNotEmpty()) {
//                                mViewModel.list.cle
                              isLoading = true

                              if (currentPage == 1)
                                  list = ArrayList()

                              list = it.data.result.mySoldBookingList

                              if (currentPage == 1 && list.size > 0) {
                                  adapter = BookingAdapter(requireContext(),type,typeReschdule,list,this)
                                  binding.recyclercategry.adapter = adapter
                              } else {
                                  adapter.updateList(list)
                              }

                              if (list.isNotEmpty()) {
                                  binding.recyclercategry.visibility = View.VISIBLE
                                  binding.noDataLayout.visibility = View.GONE
                              } else {
                                  binding.recyclercategry.visibility = View.GONE
                                  binding.noDataLayout.visibility = View.VISIBLE
                              }
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
    private fun setBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_bookingsFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onCallback(type: String) {
        val fragment=FragmentRatingUsBottomSheet()
        fragment.show(childFragmentManager,"InterestBottomSheetFragment")
    }

    override fun rejectBooking(id: String) {
        mViewModel.cancelBookingRequest.bookingId=id
        mViewModel.cancelBookingRequest.reason="I don't need this service now"
        mViewModel.cancelBookingRequest.cancelledBy="user"
        mViewModel.hitCancelBookingApi()

    }

    override fun acceptBooking(id: String) {
        mViewModel.acceptBookingRequest.bookingId=id
        mViewModel.hitAcceptBookingApi()

    }

}