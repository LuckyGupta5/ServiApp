package com.example.servivet.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.example.servivet.R
import com.example.servivet.databinding.HomeViewPagerLayoutBinding

class HomeViewPagerAdapter(var context: Context
    //  var list: ArrayList<HomeBanner>
) :
    PagerAdapter() {


    override fun getCount(): Int {
        return 3 /*list.size*/
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var binding:HomeViewPagerLayoutBinding=DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.home_view_pager_layout, container, false)
        container.addView(binding.root)

        return  binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object`as View)
    }

}