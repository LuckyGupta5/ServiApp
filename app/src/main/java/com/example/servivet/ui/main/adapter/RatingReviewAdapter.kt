package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import com.example.servivet.R
import com.example.servivet.data.model.review_ratinng.ReviewRating
import com.example.servivet.databinding.CustomReviewsLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Session

class RatingReviewAdapter(
    var context: Context,
    val list: ArrayList<ReviewRating>,
    var onItemClick: (String, String,Int) -> Unit
) : BaseAdapter<CustomReviewsLayoutBinding, ReviewRating>(list) {

    override fun getItemCount(): Int {
        return list.size
    }

    override val layoutId: Int = R.layout.custom_reviews_layout
    override fun bind(binding: CustomReviewsLayoutBinding, item: ReviewRating?, position: Int) {
        binding.data = item

        binding.idReportImage.isVisible = list[position].user._id != Session.userDetails._id
        binding.idReportImage.setOnClickListener {
            onItemClick(context.getString(R.string.report), list[position].user._id,0)
        }
    }

}