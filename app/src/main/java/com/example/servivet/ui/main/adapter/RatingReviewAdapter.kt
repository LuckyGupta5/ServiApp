package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.review_ratinng.ReviewRating
import com.example.servivet.databinding.CustomReviewsLayoutBinding
import com.example.servivet.ui.base.BaseAdapter

class RatingReviewAdapter(var context: Context, val list: ArrayList<ReviewRating>, var onItemClick: (String, String) -> Unit):
BaseAdapter<CustomReviewsLayoutBinding,ReviewRating>(list)
{

    override fun getItemCount(): Int {
        return list.size
    }

    override val layoutId: Int= R.layout.custom_reviews_layout
    override fun bind(binding: CustomReviewsLayoutBinding, item: ReviewRating?, position: Int) {
        binding.data = item
        binding.idReportImage.setOnClickListener{onItemClick(context.getString(R.string.report),"")}
    }





}