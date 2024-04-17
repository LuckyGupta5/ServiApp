package com.example.servivet.ui.main.adapter

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.example.servivet.R
import com.example.servivet.data.model.service_category_details.response.RatingReviews
import com.example.servivet.databinding.CustomRatingbarLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class RatingAdapter(
    var context: Context,
    var list: ArrayList<ListAdapterItem>,
    onItemClick: (String, String,Int) -> Unit,
    val reviews: RatingReviews
) :
    BaseAdapter<CustomRatingbarLayoutBinding, ListAdapterItem>(list) {
    var isSelect: Int = 0
    var count = ""

    inner class ClickAction {

    }

    override fun getItemCount(): Int {
        return 5
    }

    override val layoutId: Int = R.layout.custom_ratingbar_layout
    override fun bind(
        binding: CustomRatingbarLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {
        binding.idSeekBar.isEnabled = false
        count = (5 - position).toString()
        binding.ratingData = reviews
        binding.count = count.toInt()
        binding.idRatingCount.text = count
        when (count.toInt()) {
            5 -> {
                binding.idSeekBar.progress = reviews.five ?: 0
                binding.idSeekBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.malachite))
            }
            4 -> {
                binding.idSeekBar.progress = reviews.four ?: 0
                binding.idSeekBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.spring_green))

            }
            3 -> {
                binding.idSeekBar.progress = reviews.three ?: 0
                binding.idSeekBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange))

            }
            2 -> {
                binding.idSeekBar.progress = reviews.two ?: 0
                binding.idSeekBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.jaffa))

            }
            1 -> {
                binding.idSeekBar.progress = reviews.one ?: 0
                binding.idSeekBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.carnation))

            }
        }

    }


}