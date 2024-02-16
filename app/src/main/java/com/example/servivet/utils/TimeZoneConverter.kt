package com.example.servivet.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

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
    inputFormat.timeZone = TimeZone.getDefault()
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