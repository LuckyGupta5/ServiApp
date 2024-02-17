package com.example.servivet.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.servivet.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun convertTimeStampToTime(date: String?, output: Int, input: Int): String? {
    var outputFormat = SimpleDateFormat()
    var inputFormat = SimpleDateFormat()
    inputFormat = when (input) {
        0 -> SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        1 -> SimpleDateFormat("yyyy-MM-dd");
        2-> SimpleDateFormat("HH:mm")
        3-> SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        else -> {
            SimpleDateFormat("hh:mm a")
        }
    }
    outputFormat = when (output) {
        0 -> {
            SimpleDateFormat("E,dd MMMM yyyy")
        }

        1 -> {
            SimpleDateFormat("dd MMM yyyy")
        }

        2 -> {
            SimpleDateFormat("MM-dd-yyyy")
        }

        3 -> {
            SimpleDateFormat("dd MMMM yyyy")
        }

        4 -> {
            SimpleDateFormat("dd")
        }

        5 -> {
            SimpleDateFormat("MMM")
        }

        6 -> {
            SimpleDateFormat("dd MMM yyyy,hh:mm a")

        }
        7->{
            SimpleDateFormat("EEEE")

        }8->{
            SimpleDateFormat("dd MMM yyyy")

        }
        9->{
            SimpleDateFormat("yyyy-MM-dd")

        }
        10->{
            SimpleDateFormat("dd MMM,hh:mm a")

        }

        else -> {
            SimpleDateFormat("hh:mm a")
        }
    }
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    var dateTimeStamp: Date? = null
    try {
        dateTimeStamp = inputFormat.parse(date!!)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    var formattedDate: String? = null
    if (dateTimeStamp != null) {
        formattedDate = outputFormat.format(dateTimeStamp)
    }
    return formattedDate
}


fun dateDifferenceExample(requireContext: Context, oldtime: String?): String? {
    var day = 0
    var hh = 0
    var mm = 0
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("IST")
        val oldDate = dateFormat.parse(oldtime)
        val cDate = Date()
        val timeDiff = cDate.time - oldDate.time
        day = TimeUnit.MILLISECONDS.toDays(timeDiff).toInt()
        hh =
            ((TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day.toLong())).toInt())
        mm = ((TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(timeDiff)
        )).toInt())
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return if (day == 0 && hh <= 30) {
        requireContext.getString(R.string.today)

    } else if (day != 0 && day < 7) {
        "$day ${requireContext.getString(R.string.days)}"

    } else if (day >= 7 && day < 31) {
        val week = (day / 7).toString()
        "$week ${requireContext.getString(R.string.weeks)}"
    } else if (day >= 31 && day < 365) {
        val month = (day / 31).toString()
        "$month ${requireContext.getString(R.string.months)}"
    } else if (day >= 365) {
        val year = (day / 365).toString()
        "$year ${requireContext.getString(R.string.years)}"
    } else if (day == 0 && hh == 0) {
        "$mm ${requireContext.getString(R.string.min)}"
    } else if (day == 0 && hh != 0) {
        "$hh ${requireContext.getString(R.string.hour)}"
    } else {
        "$mm ${requireContext.getString(R.string.minute)}"
    }
}