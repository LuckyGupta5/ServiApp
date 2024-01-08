package com.example.servivet.ui.main.adapter

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getFont
import com.example.servivet.R
import com.example.servivet.databinding.ConnectionRequestDesignRecyclerviewBinding
import com.example.servivet.utils.interfaces.ListAdapterItem

class ConnectionRequestAdapter(var context: Context, var list: ArrayList<ListAdapterItem>) :
    com.example.servivet.ui.base.BaseAdapter<ConnectionRequestDesignRecyclerviewBinding, ListAdapterItem>(
        list
    ) {
    override val layoutId: Int = R.layout.connection_request_design_recyclerview

    override fun bind(
        binding: ConnectionRequestDesignRecyclerviewBinding,
        item: ListAdapterItem?,
        position: Int,
    ) {
        setnameandConnection(binding, position)
        setnameandConnection(binding, position)
    }

    private fun setnameandConnection(
        binding: ConnectionRequestDesignRecyclerviewBinding,
        position: Int,
    ) {
        val ss =
            SpannableString(context.getText(R.string.ravi_bishnoi_sent_you_a_connections_request))
        var value = context.getString(R.string.ravi_bishnoi_sent_you_a_connections_request)
        var name = context.getString(R.string.ravi_bishnoi)

        val myTypeface = Typeface.create(getFont(context, R.font.poppins_bold), Typeface.BOLD)
        val string = SpannableString("Text with typeface span.")

     /*   ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)),
            0,
            name.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(AbsoluteSizeSpan(15, true), 0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)*/
        val clickableSpan:ClickableSpan=object :ClickableSpan(){
            override fun onClick(widget: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.typeface=Typeface.DEFAULT_BOLD
            }

        }
        ss.setSpan(clickableSpan, 0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun getItemCount(): Int {
        return 11
    }
}