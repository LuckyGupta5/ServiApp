package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.example.servivet.R
import com.example.servivet.data.model.view_pager_model.ViewPagerTutorialModel
import com.example.servivet.databinding.OnboardingViewpagerRecyclerBinding


class OnBoardingAdapter(var context: Context, var list: ArrayList<ViewPagerTutorialModel>) :PagerAdapter() {


    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding:OnboardingViewpagerRecyclerBinding=DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.onboarding_viewpager_recycler, container, false)
        container.addView(binding.root)
        binding.data=list[position]
        binding.image.setBackgroundResource(list[position].imageView)
        return  binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object`as View)
    }
}