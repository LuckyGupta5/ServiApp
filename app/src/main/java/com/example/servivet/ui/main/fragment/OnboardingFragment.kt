package com.example.servivet.ui.main.fragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.TutorialModel
import com.example.servivet.data.model.view_pager_model.ViewPagerTutorialModel
import com.example.servivet.databinding.FragmentOnboardingBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.OnBoardingAdapter
import com.example.servivet.ui.main.adapter.TutorialServiceAdapter
import com.example.servivet.ui.main.view_model.OnboardingViewModel
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle


class OnboardingFragment :
    BaseFragment<FragmentOnboardingBinding, OnboardingViewModel>(R.layout.fragment_onboarding) {

    override val binding: FragmentOnboardingBinding by viewBinding(FragmentOnboardingBinding::bind)
    override val mViewModel: OnboardingViewModel by viewModels()

    var serviceList=ArrayList<TutorialModel>()




    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        if (isAdded)
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = mViewModel
                click = mViewModel.ClickAction(requireContext(), binding)
            }
      //  setViewPagerAdapter()
       // setViewPagerConditions()
        setAdapterItems()
        onBackCall()
    }



    private fun setAdapterItems() {
        serviceList.clear()
        serviceList.addAll(listOf(
            TutorialModel(R.drawable.wellness_2, getString(R.string.wellness)),
            TutorialModel(R.drawable.household, getString(R.string.household)),
            TutorialModel(R.drawable.electronics, getString(R.string.electronic)),
            TutorialModel(R.drawable.finance_2, getString(R.string.finance)),
            TutorialModel(R.drawable.vedios_photos_2, getString(R.string.videophoto)),
            TutorialModel(R.drawable.enterteainment, getString(R.string.showbiz)),
            TutorialModel(R.drawable.realstate, getString(R.string.real_estate)),
            TutorialModel(R.drawable.spa_n_beauty, getString(R.string.spa_beauty)),
            TutorialModel(R.drawable.software, getString(R.string.security)),
            TutorialModel(R.drawable.software, getString(R.string.software)),
            TutorialModel(R.drawable.legal, getString(R.string.legal)),
            TutorialModel(R.drawable.pet_services, getString(R.string.pet_service)),
            TutorialModel(R.drawable.travels, getString(R.string.travel)),
            TutorialModel(R.drawable.others, getString(R.string.other))))
        binding.idRecycle.adapter= TutorialServiceAdapter(requireContext(),serviceList)
        binding.idBackPress.setOnClickListener { findNavController().popBackStack() }

    }




    override fun onResume() {
        super.onResume()
        binding.indicatorView.setupWithViewPager(binding.viewPager)
    }

    private fun setViewPagerConditions() {
        /*  if(binding.viewPager.currentItem<mViewModel.list.size-1) {
              binding.viewPager.currentItem=mViewModel.count+1
              binding.next.visibility=View.VISIBLE
              binding.skip.visibility=View.VISIBLE
              binding.getStart.visibility=View.GONE
              if(binding.viewPager.currentItem==2){
                  binding.next.visibility=View.GONE
                  binding.skip.visibility=View.GONE
                  binding.getStart.visibility=View.VISIBLE
              }
              mViewModel.count++
              Log.e("TAG", "handleOnBackPressed: "+mViewModel.count )

          }*/
        if (binding.viewPager.currentItem == 2) {
            binding.getStart.visibility = View.VISIBLE
            binding.next.visibility = View.GONE
            binding.skip.visibility = View.GONE
        } else {
            binding.getStart.visibility = View.GONE
            binding.next.visibility = View.VISIBLE
            binding.skip.visibility = View.VISIBLE
        }
    }

    private fun onBackCall() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                @SuppressLint("SetTextI18n")
                override fun handleOnBackPressed() {
                    if (mViewModel.count > 0) {
                        mViewModel.count--
                        binding.viewPager.currentItem = mViewModel.count
                        if (binding.viewPager.currentItem == 2) {
                            binding.getStart.visibility = View.VISIBLE
                            binding.next.visibility = View.GONE
                            binding.skip.visibility = View.GONE
                        } else {
                            binding.getStart.visibility = View.GONE
                            binding.next.visibility = View.VISIBLE
                            binding.skip.visibility = View.VISIBLE
                        }
                        Log.e("TAG", "handleOnBackPressed: " + mViewModel.count)
                    } else {
                        findNavController().popBackStack()
                        Log.e("TAG", "handleOnBackPressed: " + mViewModel.count)

                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setViewPagerAdapter() {
        mViewModel.list.clear()
        mViewModel.list.add(
            ViewPagerTutorialModel(
                R.drawable.onboarding_img1,
                "Enjoy a hassle-free\n experience",
                "Discover, Schedule, and get your\n service done."
            )
        )
        mViewModel.list.add(
            ViewPagerTutorialModel(
                R.drawable.onboarding_img2,
                "Lets Explore newly\n introduced features",
                "Discover, Schedule, and get your\n service done."
            )
        )
        mViewModel.list.add(
            ViewPagerTutorialModel(
                R.drawable.onboarding_img3,
                "AC servicing, electrician,\n plumber, or more?",
                "Discover, Schedule, and get your\n service done."
            )
        )
        binding.viewPager.adapter =    OnBoardingAdapter(requireContext(), mViewModel.list)
        binding.indicatorView.apply {
            setSliderWidth(55f)
            setSliderHeight(7f)
            setSlideMode(IndicatorSlideMode.NORMAL)
            setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            setPageSize(binding.viewPager.adapter!!.count)
            notifyDataChanged()
        }
        binding.indicatorView.setupWithViewPager(binding.viewPager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 2) {
                    binding.getStart.isVisible = true
                    binding.next.isVisible = false
                    binding.skip.isVisible = false
                    mViewModel.count = position
                } else {
                    binding.getStart.isVisible = false
                    binding.next.isVisible = true
                    binding.skip.isVisible = true
                    mViewModel.count = position
                }
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
//        TabLayoutMediator(binding.tablayoutIndicator, binding.viewPager) {_ ,_  -> }.attach()
    }

    override fun setupViews() {
    }

    override fun setupObservers() {
    }

}