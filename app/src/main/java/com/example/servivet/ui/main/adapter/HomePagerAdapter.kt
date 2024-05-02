package com.example.servivet.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.home.response.HomeBanner
import com.example.servivet.databinding.HomeViewPagerLayoutBinding


class HomePagerAdapter(
    var context: Context, var list: List<HomeBanner>
) :
    PagerAdapter() {


    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var binding:HomeViewPagerLayoutBinding=DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.home_view_pager_layout, container, false)
        container.addView(binding.root)
        Glide.with(context).load(list[position].bannerUrl).error(R.drawable.flower_img).into(binding.bannerImage)
        if(list[position].url!=null && list[position].url.isNotEmpty()){
            binding.bannerImageLayout.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(list[position].url))
                context.startActivity(browserIntent)
            }

        }
        return  binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object`as View)
    }

}