package com.example.servivet.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DownloadManager
import android.app.TimePickerDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.ui.main.activity.MainActivity
import com.example.servivet.ui.main.view_model.SplashViewModel
import com.example.servivet.utils.soundservices.SoundService
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.*
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
object CommonUtils {

    private lateinit var pickerForm: DatePickerDialog
    private lateinit var fromDateValue: String
    private var fromDay: Int = 0
    private var fromMonth: Int = 0
    var cameraPhotoPath: String? = null
    private var fromYear: Int = 0
    val initialYear = 2022 // Set your desired initial year
    val initialMonth = Calendar.JANUARY // Month is zero-based, so January is 0
    val initialDay = 1 // Set

    fun selectFromDate(context: Context, date: String, listener: (String) -> Unit) {
        var selectedDate: Date
        val calendar = Calendar.getInstance()
        fromDay = calendar.get(Calendar.DAY_OF_MONTH)
        fromMonth = calendar.get(Calendar.MONTH)
        fromYear = calendar.get(Calendar.YEAR)
        if (date.isNotEmpty()) {
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            calendar.time = selectedDate
        }
        pickerForm = DatePickerDialog(
            context, { datePicker: DatePicker?, years: Int, monthOfYear: Int, dayOfMonth: Int ->

                val month1 = monthOfYear + 1
                var formattedMonth = "$month1"
                var formattedDayOfMonth = "$dayOfMonth"
                if (month1 < 10) formattedMonth = "0$month1"
                if (dayOfMonth < 10) formattedDayOfMonth = "0$dayOfMonth"
                fromDay = dayOfMonth
                fromMonth = monthOfYear
                fromYear = years
                fromDateValue = "$years-$formattedMonth-$formattedDayOfMonth"
                listener(fromDateValue)
            }, fromYear, fromMonth, fromDay
        )
        //   calendar.add(Calendar.YEAR, -10)
        pickerForm.datePicker.minDate = calendar.timeInMillis
        pickerForm.show()
    }

    @JvmStatic
    @BindingAdapter("android:circularImage")
    fun loadCircularImage(view: View?, image_url: String?) {
        val imageView = view as ImageView?
        Glide.with(view!!).load(image_url)/*.error(R.mipmap.circular_image)*/.into(imageView!!)
    }

    fun requestPermissions(
        activity: Activity, PERMISSION_REQUEST_CODE: Int, permissions: Array<String?>
    ): Boolean {
        val isAllPermissionGranted = booleanArrayOf(false) // Initialize with false

        Dexter.withContext(activity).withPermissions(*permissions)
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        isAllPermissionGranted[0] = true
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(activity, PERMISSION_REQUEST_CODE)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>, token: PermissionToken
                ) {
                    // Handle the rationale message and re-request the permission
                    showRationaleDialog(activity, token)
                }
            }).withErrorListener {
                Log.e("TAG", "requestPermissions: " + it)
            }.onSameThread().check()

        return isAllPermissionGranted[0]
    }

    private fun showRationaleDialog(activity: Activity, token: PermissionToken) {
        val dialog = AlertDialog.Builder(activity)
        dialog.setMessage("Allow Servivet to access the photos and media on your device")
        dialog.setTitle(activity.resources.getString(R.string.app_name))
        dialog.setPositiveButton("Allow") { _, _ ->
            token.continuePermissionRequest()
        }
        dialog.setNegativeButton("Deny") { dialog, _ ->
            dialog.dismiss()
            showSettingsDialog(activity, 100)
            // Handle denial, if needed
        }
        dialog.show()
    }

    fun getPostalCodeByCoordinates(
        placeSelectionListener: PlaceSelectionListener, lat: Double, lon: Double, context: Context
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var zipCode: String? = null
        var address: Address? = null

        if (geocoder != null) {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 5)!!
            if (addresses != null && addresses.isNotEmpty()) {
                for (i in addresses.indices) {
                    address = addresses[i]
                    if (address.postalCode != null) {
                        zipCode = address.postalCode
                        Log.d("TAG", "Postal code: " + address.postalCode)
                        break
                    }
                }
                return zipCode.toString()
            }

        }
        return null.toString()
    }

    private fun showSettingsDialog(activity: Activity, PERMISSION_REQUEST_CODE: Int) {
        val dialog = AlertDialog.Builder(activity)
        dialog.setMessage(activity.getString(R.string.servivet_require_this_permission))
        dialog.setTitle(activity.resources.getString(R.string.app_name))
        dialog.setPositiveButton("Ok") { _, _ ->
            openSettings(activity, PERMISSION_REQUEST_CODE)
        }
        dialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            // Handle denial, if needed
        }
        dialog.show()
    }

    private fun openSettings(activity: Activity, PERMISSION_REQUEST_CODE: Int) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, PERMISSION_REQUEST_CODE)
    }

    fun isCurrentDate(date: String): Boolean {
        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateToCheck = sdfInput.parse(date)
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentDateStr = sdf.format(currentDate)
        val dateToCheckStr = sdf.format(dateToCheck)

        return currentDateStr == dateToCheckStr
    }

    private fun getDayinMonth(currentmonth: Int, getcurrentYear: Int): List<String> {
        var calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, getcurrentYear)
        calender.set(Calendar.MONTH, currentmonth)
        calender.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = mutableListOf<String>()

        while (calender.get(Calendar.MONTH) == currentmonth) {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            daysInMonth.add(dateFormat.format(calender.time))
            calender.add(Calendar.DAY_OF_MONTH, 1)
        }
        return daysInMonth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateFromToday(monthCount: Int): Array<String?> {
        var currentDate: LocalDate
        var currentDate1: LocalDate = LocalDate.now()
        val dayOfMonth = currentDate1.dayOfMonth - 1
        var monthDaysCount = getDaysInCurrentMonth(monthCount)
        if (monthCount > 0) {
            currentDate =
                LocalDate.now().plusMonths(monthCount.toLong()).minusDays(dayOfMonth.toLong())
        } else {
            currentDate = LocalDate.now()
            monthDaysCount -= dayOfMonth
        }

        val dates = arrayOfNulls<String>(monthDaysCount)
        for (i in 0..<monthDaysCount) {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, yyyy-MM-dd")
            dates[i] = currentDate.format(formatter)
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }

    private fun getDaysInCurrentMonth(monthCount: Int): Int {
        val calendar = Calendar.getInstance()
        // Set the calendar to the first day of the month
        calendar.add(Calendar.MONTH, monthCount)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Get the maximum value for the day of the month, which gives the number of days in the month
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }


    fun monthYearFromDate(input: String?): String? {
        val inFormat = SimpleDateFormat("EEE, yyyy-MM-dd", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = inFormat.parse(input)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dateFormat = SimpleDateFormat("MMM yyyy")
        return dateFormat.format(date)
    }


    fun dayMonthYearFromDate(input: String?): String? {
        val inputFormat = SimpleDateFormat("EEE, yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        var dateTimeStamp: Date? = null
        try {
            dateTimeStamp = inputFormat.parse(input)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formattedDate = outputFormat.format(dateTimeStamp)
        return formattedDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeek(): String {
        val dayOfWeek: DayOfWeek = LocalDate.now().dayOfWeek
        val dayName: String = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return dayName
    }

    fun dayFromDate(input: String?): String? {
//        Log.i("TAG", "dayFromDate: "+ input);
        val inFormat = SimpleDateFormat("EEE, yyyy-MM-dd", Locale.getDefault())
        var date: Date? = null
        try {
            date = inFormat.parse(input)
            val dayFormat = SimpleDateFormat("EEE")
            return dayFormat.format(date)
        } catch (e: ParseException) {
            Log.d("TAG", "dayFromDate: ${e.message}")
            return ""
        }
    }

    fun dateFromDate(input: String?): String? {
        val inFormat = SimpleDateFormat("EEE, yyyy-MM-dd", Locale.getDefault())
        var date: Date? = null
        try {
            date = inFormat.parse(input)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dateFormat = SimpleDateFormat("dd")
        return dateFormat.format(date)
    }


    fun getUriFromBitmap(src: Bitmap, context: Context): Uri? {
        val bytes = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.contentResolver, src, "Title" + System.currentTimeMillis(), null
        )
        return Uri.parse(path)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fullDayName(date: String?): String {
        val dateString = date
        val formatter = DateTimeFormatter.ofPattern("EEE, yyyy-MM-dd", Locale.getDefault())
        val date = LocalDate.parse(dateString, formatter)

        val dayOfWeek = date.dayOfWeek.toString()
        val fullDayName = dayOfWeek.substring(0, 1).uppercase() + dayOfWeek.substring(1).lowercase()

        return fullDayName
    }

    fun findListIndex(index: Int): Int {
        if (index != -1) {
            return index
        } else {
            return -1
        }
    }

    fun getRealPathFromURI(activity: Activity, uri: Uri?): String? {
        var filePath = ""

        val p = Pattern.compile("(\\d+)$")
        val m = p.matcher(uri.toString())
        if (!m.find()) {
            return filePath
        }
        val imgId = m.group()

        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor: Cursor = activity.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf<String>(imgId), null
        )!!

        val columnIndex = cursor.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()

        return filePath
    }

    fun createFileSmall(filePath: String?, context: Activity?): File? {
        val bitmap: Bitmap = ImageUtils.instant!!.getCompressedBitmap(filePath)
        val realPath = getRealPathFromURI(context!!, getImageUri(context!!, bitmap))
        return realPath?.let { File(it) }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver, inImage, "Title" + System.currentTimeMillis(), null
        )
        return Uri.parse(path)
    }

    fun createFileSmall(context: Context, filePath: String): String? {
        val bitmap: Bitmap = ImageUtils.instant!!.getCompressedBitmap(filePath)
        val realPath: String =
            getRealPathFromDocumentUri(context, getUriFromBitmap(bitmap, context)!!)!!
        return realPath
    }

    fun checkDates(d1: String, d2: String): Boolean {
        // val dfDate = SimpleDateFormat("hh:mm a")
        val dfDate = SimpleDateFormat("HH:mm")
        var b = false
        try {
            if (dfDate.parse(d1).before(dfDate.parse(d2))) {
                b = true
            } else if (dfDate.parse(d1).equals(dfDate.parse(d2))) {
                b = true
            } else {
                b = false
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return b
    }


    fun selectTime(context: Context, textView: TextView, callback: (data: String) -> Unit): String {
        var mSelectedTime = ""
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, minute)
            mSelectedTime = SimpleDateFormat("HH:mm ").format(c.time)
            callback(mSelectedTime)
            textView.text = mSelectedTime
        }
        val timePickerDialog = TimePickerDialog(
            context, AlertDialog.THEME_HOLO_LIGHT, timeSetListener, hour, minute, true
        )
        timePickerDialog.show()
        return mSelectedTime
    }


    /*  fun showSettingsDialog(activity: Activity, PERMISSION_REQUEST_CODE: Int) {
          val dialog = AlertDialog.Builder(activity)
          dialog.setMessage(activity.getString(R.string.servivet_require_this_permission))
          dialog.setTitle(activity.resources.getString(R.string.app_name))
          dialog.setPositiveButton("Ok") { dialog, which ->
              dialog.dismiss()
              val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
              val uri = Uri.fromParts("package", activity.packageName, null)
              intent.data = uri
              activity.startActivityForResult(intent, PERMISSION_REQUEST_CODE)
          }
      }*/
    @JvmStatic
    @BindingAdapter("android:imageViewUrl")
    fun loadNormalImage(view: View?, image_url: String?) {
        val imageView = view as ImageView?
        Glide.with(view!!).load(image_url).error(R.drawable.userprofile).into(imageView!!)
    }

    @JvmStatic
    @BindingAdapter("android:bannerUrl")
    fun loadBannerImage(view: View?, image_url: String?) {
        val imageView = view as ImageView?
        Glide.with(view!!).load(image_url).error(R.drawable.disable_button).into(imageView!!)
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateTimeStampConvert(timestamp: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        // inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        val date = inputFormat.parse(timestamp)
        return outputFormat.format(date)
    }

    fun getDateTimeStampConvertConectionPage(timestamp: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        val date = inputFormat.parse(timestamp)
        return outputFormat.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateFromTimeStamp(date: String?): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputDate = SimpleDateFormat("dd MMM yyyy")
        val outputTime = SimpleDateFormat("hh:mm a")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var dateTimeStamp: Date? = Date()
        try {
            dateTimeStamp = sdf.parse(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputTime.format(dateTimeStamp!!)
    }

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS


    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun formatDate(date: String?, initDateFormat: String?, endDateFormat: String?): String? {
        val initDate = SimpleDateFormat(initDateFormat).parse(date!!)
        val formatter = SimpleDateFormat(endDateFormat)
        return formatter.format(initDate!!)
    }


    fun getRealPathFromDocumentUri(context: Context, uri: Uri): String? {
        uri ?: return null
        uri.path ?: return null

        var newUriString = uri.toString()
        newUriString = newUriString.replace(
            "content://com.android.providers.downloads.documents/",
            "content://com.android.providers.media.documents/"
        )
        newUriString = newUriString.replace(
            "/msf%3A", "/image%3A"
        )
        val newUri = Uri.parse(newUriString)

        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (newUri.path?.contains("/document/image:") == true) {
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(newUri).split(":")[1])
        } else {
            databaseUri = newUri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri, projection, selection, selectionArgs, null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.i("GetFileUri Exception:", e.message ?: "")
        }
        val path = realPath.ifEmpty {
            when {
                newUri.path?.contains("/document/raw:") == true -> newUri.path?.replace(
                    "/document/raw:", ""
                )

                newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                    "/document/primary:", "/storage/emulated/0/"
                )

                else -> return null
            }
        }
        return if (path.isNullOrEmpty()) null
        else File(path).toString()

    }

    fun Fragment.showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    fun Fragment.showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun formatDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) return ""
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = sdf.parse(inputDate) ?: return ""

        val calendar = Calendar.getInstance()
        calendar.time = date

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return when {
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(
                Calendar.DAY_OF_YEAR
            ) -> "Recent"

            calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(
                Calendar.DAY_OF_YEAR
            ) -> "Yesterday"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun sendMessageTime(date: String?): String? {
        if (date.isNullOrEmpty()) return ""

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("hh:mm a")
        outputFormat.timeZone = TimeZone.getDefault()

        return try {
            val dateTimeStamp = inputFormat.parse(date)
            outputFormat.format(dateTimeStamp!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    fun emailValidator(email: String?): Boolean {
        val pattern: Pattern
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }


    @JvmStatic
    @BindingAdapter("android:setBitmapImage")
    fun loadBitMapImage(view: View?, image_url: Bitmap?) {
        val imageView = view as ImageView?
        imageView!!.setImageBitmap(image_url)
    }

    fun alert(context: Context?, msg: String?, requireActivity: Activity) {


        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.alert_dialog_design)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.all_border_round_10_app_them)
        val title = dialog.findViewById<TextView>(R.id.title)
        title.setText(R.string.app_name)
        val message = dialog.findViewById<TextView>(R.id.message)
        message.text = msg
        val view = dialog.findViewById<View>(R.id.viewCenter)
        view.visibility = View.GONE
        val noBtn = dialog.findViewById<TextView>(R.id.no)
        val yesBtn = dialog.findViewById<TextView>(R.id.yes)

        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        yesBtn.setOnClickListener {
            dialog.dismiss()
            Session.logout()
            SplashViewModel.isLogout = true
            requireActivity.startActivity(Intent(requireActivity, MainActivity::class.java))
            requireActivity.finish()
            Toast.makeText(requireActivity, "Log out successfully !", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    fun logoutAlert(
        context: Context,
        title: String,
        message: String,
        requireActivity: Activity,
    ) {
        val alert: AlertDialog = AlertDialog.Builder(context).create()
        alert.setTitle(title)
        alert.setMessage(message)
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)


        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { dialog, _ ->
            SplashViewModel.isLogout = true
            Session.logout()
            ContextCompat.startActivity(
                context, Intent(requireActivity, MainActivity::class.java), null
            )
            requireActivity.finish()
        }

        alert.show()
    }

    fun customalertdialog(context: Activity?, msg: String?, from: Int) {

        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.alert_dialog_custom)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.all_border_round_10dp_apptheme)
        val title = dialog.findViewById<TextView>(R.id.title)
        title.setText(R.string.app_name)
        val message = dialog.findViewById<TextView>(R.id.message)
        message.text = msg

        val dialogButton = dialog.findViewById<TextView>(R.id.yes)
        val dialogno = dialog.findViewById<TextView>(R.id.no)
        //dialogButton.gravity = Gravity.CENTER_HORIZONTAL
        dialogno.setOnClickListener { v: View? ->
            dialogno.isEnabled.and(true)
            dialog.dismiss()
        }
        dialogButton.setOnClickListener { v: View? ->
            Session.logout()
            SplashViewModel.isLogout = false
            Session.saveIsLogin(false)
            context.finish()
            var intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            dialogButton.isEnabled.and(false)
            dialog.dismiss()
        }
        dialog.show()
    }


    fun screenshot(view: View, filename: String): Bitmap? {

        try {
            // Initialising the directory of storage
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun openShareBottomSheet(context: Activity, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        context.startActivity(intent)
    }

    fun getAddressFromLatLng(context: Context?, latitude: Double, longitude: Double): Address? {
        //Set Address
        try {
            val geocoder = Geocoder(context!!, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: String =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val city: String = addresses[0].locality
                val state: String = addresses[0].adminArea
                val country: String = addresses[0].countryName
                val postalCode: String = addresses[0].postalCode
                val phone = addresses[0].phone
                val premises = addresses[0].premises
                val subLocality = addresses[0].subLocality
                val extras = addresses[0].extras
                val knownName: String =
                    addresses[0].featureName // Only if available else return NULL
                Log.d("TAG", "getAddress:  address: $address")
                Log.d("TAG", "getAddress:  country: $country")
                Log.d("TAG", "getAddress:  city: $city")
                Log.d("TAG", "getAddress:  state: $state")
                Log.d("TAG", "getAddress:  postalCode: $phone")
                Log.d("TAG", "getAddress:  knownName: $premises")
                Log.d("TAG", "getAddress:  knownName: $postalCode")
                return addresses[0]
            }
        } catch (e: Exception) {
            Log.e("Exception:", "getAddressFromLatLng: ${e.localizedMessage}")
            Log.e("Exception:", "getAddressFromLatLng: ${e.message}")
        }
        return null
    }

    fun navigateToGoogleMap(context: Context, marker: Marker) {

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=${marker.position.latitude},${marker.position.longitude}")
        )
        context.startActivity(intent)

    }


    fun utcToLocal(dateStr: String): String {
        val df = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(dateStr)
        df.timeZone = TimeZone.getDefault()
        val formattedDate = df.format(date)
        return formattedDate
    }

    fun pointHistorydate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("IST")

        try {
            val dateParse = inputFormat.parse(date)

            val dateF = dateFormat.format(dateParse)
            val timeF = timeFormat.format(dateParse)
            return "${timeAgo(date)}"

        } catch (e: Exception) {
            Log.e("Exception: ", "notificationDate: ${e.localizedMessage}")
        }
        return ""
    }

    fun timeAgo(dataDate: String?): String? {
        var convTime: String? = null
        val suffix = "Ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val pasTime = dateFormat.parse(dataDate)
            dateFormat.timeZone = TimeZone.getTimeZone("IST")
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

            if (second < 60) {
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " Years " + suffix
                } else if (day > 30) {
                    (day / 30).toString() + " Months " + suffix
                } else {
                    (day / 7).toString() + " Week " + suffix
                }
            } else if (day < 7) {
                convTime = "$day Days $suffix"
            }

            /* if (day.toDouble() == 0.0) {
                 return "Today"
             } else*/ if (day.toDouble() == 1.0) {
                return "Yesterday"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }

        return convTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Define your desired date format
        val currentDateAsString = currentDate.format(formatter)
        return currentDateAsString
    }

    fun setYesterdayDate(): String {
        var dateFormat = SimpleDateFormat("dd MMM yyyy")
        var cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return dateFormat.format(cal.time)
    }

    fun callNumber(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phoneNumber}")
        context.startActivity(intent)
    }

    fun questionType(type: Int): CharSequence {
        when (type) {

            1 -> {
                return "Before entering the apartment"
            }

            2 -> {
                return "After entering the apartment"
            }

            3 -> {
                return "Under process"
            }

            4 -> {
                return "Before leaving the apartment"
            }

            5 -> {
                return "After leaving the apartment"
            }
        }
        return ""
    }

}

fun generateMonthStrings(): Array<String> {
    val calendar = Calendar.getInstance()

    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    val currentMonth = dateFormat.format(calendar.time)

    // Move to the next month
    calendar.add(Calendar.MONTH, 1)
    val nextMonth = dateFormat.format(calendar.time)

    return arrayOf(currentMonth, nextMonth)
}

@BindingAdapter("android:setUtcToLocalTimeStamp")
fun setUtcToLocalTimeStamp(textView: AppCompatTextView, dateStr: String) {
    val df = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(dateStr)
    df.timeZone = TimeZone.getDefault()
    val formattedDate = df.format(date)
    textView.text = formattedDate
}

fun commaSaparator(number: Double?): String? {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(number)
}

fun getLastWordFromUrl(url: String): String {
    // Split the URL using "/"
    val parts = url.split("/")

    // Get the last part (word) from the split URL
    return if (parts.isNotEmpty()) {
        parts.last()
    } else {
        ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun updateButtonState(exceedTime: String): Boolean {
//    val startTime = LocalTime.parse(currentTime(), DateTimeFormatter.ofPattern("hh:mm a"))
//    val endTime = LocalTime.parse(exceedTime, DateTimeFormatter.ofPattern("hh:mm a"))
    val startTime =
        LocalTime.parse(currentTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
    val endTime =
        LocalTime.parse(exceedTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
    val timeDifference = calculateTimeDifference(startTime, endTime)
    return timeDifference < TimeUnit.HOURS.toMillis(24)

}

fun compareDateDifferenceInHours(date1: String, date2: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val parsedDate1 = dateFormat.parse(date1) ?: Date()
    val parsedDate2 = dateFormat.parse(date2) ?: Date()

    val differenceInMillis = parsedDate2.time - parsedDate1.time
    return TimeUnit.MILLISECONDS.toHours(differenceInMillis)
}

fun getCurreentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault() // Set the timezone to UTC
    val currentDate = Date(System.currentTimeMillis())
    return dateFormat.format(currentDate)
}


@RequiresApi(Build.VERSION_CODES.O)
fun currentTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    Log.e("TAG", "manageMarkAsCompleteView32132: ${dateFormat.format(Date())}")
    return dateFormat.format(Date())

}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateTimeDifference(startTime: LocalTime, endTime: LocalTime): Long {
    val startMillis = startTime.toSecondOfDay() * 1000L
    val endMillis = endTime.toSecondOfDay() * 1000L
    return endMillis - startMillis
}

fun convertTo24HourFormat(time12Hour: String): String {
    val inputFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    try {
        val date = inputFormat.parse(time12Hour)
        return outputFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return "" // Return an empty string if there's an error
}

fun getCurrentDateTimeInISO8601Format(myDate: String): String {
//    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
//    dateFormat.timeZone = TimeZone.getDefault() // Set the timezone to UTC
//    val currentDate = Date(System.currentTimeMillis())
//    return dateFormat.format(currentDate)

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = inputFormat.parse(myDate)

    val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputDateStr = outputFormat.format(date)
    return outputDateStr

}


@SuppressLint("SimpleDateFormat")
fun compareTwoDates(startDate: String, endDate: String, myDate: String): Boolean {
    val dates = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val start: Date = dates.parse(startDate)!!
    val end: Date = dates.parse(endDate)!!
    val currentDate: Date = dates.parse(getCurrentDateTimeInISO8601Format(myDate))!!
    Log.e("TAG", "compareTwoDates: ${start},${end},${currentDate}")
    return currentDate in start..end
}


@RequiresApi(Build.VERSION_CODES.O)
fun isTimeGapGreaterThan24Hours(dateTime1: String, dateTime2: String, hoursGap: Int): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val parsedDateTime1 = LocalDateTime.parse(dateTime1, formatter)
    val parsedDateTime2 = LocalDateTime.parse(dateTime2, formatter)

    val gapInHours = ChronoUnit.HOURS.between(parsedDateTime1, parsedDateTime2)
    Log.e("TAG", "isTimeGapGreaterThan24Hours: $gapInHours")

    return gapInHours > hoursGap
}

fun getCurrentTimeInFormat(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(Date())
}


fun convertDateTimeFormat(inputDateTime: String): String {
    // Define input and output date-time formatters
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    // Parse the input date-time string
    val parsedDateTime = LocalDateTime.parse(inputDateTime, inputFormatter)

    // Format the parsed date-time into the desired output format
    return parsedDateTime.format(outputFormatter)
}

fun getVideoPathFromUri(context: Context, videoUri: Uri): String? {
    var path: String? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
            context, videoUri
        )
    ) {
        // Document URI (e.g., content://com.android.providers.media.documents/document/video:12345)
        val documentId = DocumentsContract.getDocumentId(videoUri)
        val id = documentId.split(":")[1]

        val selection = MediaStore.Video.Media._ID + "=?"
        val selectionArgs = arrayOf(id)

        val column = "_data"
        val projection = arrayOf(column)

        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? =
            context.contentResolver.query(contentUri, projection, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(column)
                path = it.getString(columnIndex)
            }
        }
    } else {
        // MediaStore URI
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(videoUri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
    }

    return path
}


fun getFileExtensionFromUrl(url: String): String? {
    val lastDotIndex = url.lastIndexOf('.')
    return if (lastDotIndex != -1) {
        url.substring(lastDotIndex + 1)
    } else {
        null
    }
}

fun checkVideoFileSize(videoFilePath: String): Long {
    val videoFile = File(videoFilePath)

    // Check if the file exists
    if (!videoFile.exists()) {
        // Handle the case when the file doesn't exist
        return -1
    }

    // Get the file size in bytes
    val fileSizeInBytes = videoFile.length()

    // Convert bytes to megabytes (1 MB = 1024 KB)
    val fileSizeInMB = fileSizeInBytes / (1024 * 1024)

    // Log or print the file size
    println("Video File Size: $fileSizeInMB MB")
    Log.e("TAG", "checkVideoFileSize: $fileSizeInMB")

    return fileSizeInMB
}

fun formatDecimalNumber(inputNumber: Double): String {
    val decimalFormat = DecimalFormat("#.#")
    return decimalFormat.format(inputNumber)
}

fun isMiUi(): Boolean {
    return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
}

fun getSystemProperty(propName: String): String? {
    val line: String
    var input: BufferedReader? = null
    try {
        val p = Runtime.getRuntime().exec("getprop $propName")
        input = BufferedReader(InputStreamReader(p.inputStream), 1024)
        line = input.readLine()
        input.close()
    } catch (ex: IOException) {
        return null
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return line
}

fun stopBackgroundMusicService(context: Context) {
    val svc = Intent(context, SoundService::class.java)
    context.stopService(svc)
}

fun startBackgroundMusicService(context: Context) {
    try {
        val svc = Intent(context, SoundService::class.java)
        context.startService(svc)
    } catch (e: Exception) {
        e.printStackTrace()
    }


}

fun isBluetoothConnectd(): Boolean {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    return (bluetoothAdapter != null && BluetoothAdapter.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(
        BluetoothProfile.HEADSET
    ))
}

fun isAppOnForeground(context: Context): Boolean {
    val appPackageName = context.packageName.toString()
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return false
    for (appProcess in appProcesses) {
        if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == appPackageName) {
            return true
        }
    }
    return false
}


fun setSpannable(data1: String?, data2: String): Spannable {
    val fullText = "$data1 $data2"
    val spannable = SpannableString(fullText)
    spannable.setSpan(
        StyleSpan(Typeface.BOLD), 0, data1!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spannable.setSpan(
        StyleSpan(Typeface.NORMAL),
        data1.length + 1,
        fullText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}


fun checkMediaPermission(requireActivity: FragmentActivity): Boolean {
    val permission: Array<String?> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    return CommonUtils.requestPermissions(requireActivity, 100, permission)
}


fun <A : Activity> Context.launchActivityWithBundle(activity: Class<A>, bundle: Bundle) {
    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).also {
        it.putExtra("bundle", bundle)
        it.setClassName("com.example.servivet", activity.name)
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        it.flags = Intent.FLAG_FROM_BACKGROUND
        it.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(it)
    }
}

fun downloadFile(
    context: Context,
    URL: String?,
    fileName: String?,
    fileNameExtension: String?,
) {
    Log.w("URL-->", URL!!)
    val downloadmanager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = Uri.parse(URL)
    val request = DownloadManager.Request(uri)
    request.setTitle(fileName)
    request.setDescription("Downloading...")
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setAllowedOverRoaming(false).setTitle(fileName)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileNameExtension)
    downloadmanager.enqueue(request)
    //  Snackbar.make(requireContext, "Downloading File...", Snackbar.LENGTH_SHORT).show()


    //openPdfBottom(childFragmentManager,URL)
}


fun requestMultiplePermissions(
    context: Context, permissions: Array<String>, msg: String
): Boolean {
    var isAllowed: Boolean = false
    Dexter.withContext(context as Activity).withPermissions(*permissions)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                isAllowed = true
                // check if all permissions are granted
                if (!report.areAllPermissionsGranted()) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", "com.example.servivet", null)
                    intent.data = uri
                    showDialog(context, msg, intent)
                }


                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied) {
                    // show alert dialog navigating to Settings
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest?>?, token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).withErrorListener {

        }.onSameThread().check()
    Log.e("TAG", "requestMultiplePermissions: $isAllowed")
    return isAllowed
}

fun showDialog(context: Context, msg: String?, intent: Intent?) {
    val dialog = Dialog(context)
    (dialog.window!!.decorView as ViewGroup).getChildAt(0)
        .startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
    dialog.setCancelable(true)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setContentView(R.layout.alert_dialog)
    dialog.window!!.setBackgroundDrawableResource(R.drawable.all_fill_white_greyborder_8dp)
    val title = dialog.findViewById<TextView>(R.id.title)
    title.setText(R.string.app_name)
    val message = dialog.findViewById<TextView>(R.id.message)
    message.text = msg
    val view = dialog.findViewById<View>(R.id.viewCenter)
    view.visibility = View.GONE
    val dialogButton = dialog.findViewById<TextView>(R.id.okCenter)
    dialogButton.visibility = View.VISIBLE
    dialogButton.gravity = Gravity.CENTER_HORIZONTAL
    dialogButton.setOnClickListener { v: View? ->
        context.startActivity(intent)
        dialog.dismiss()
    }
    dialog.show()
}

fun Activity.setLocal(languageName: String, types: Int) {
    val local = Locale(languageName)
    val configuration = resources.configuration
    val displayMatrix = resources.displayMetrics
    configuration.setLocale(local)
    resources.updateConfiguration(configuration, displayMatrix)
}

fun changeLanguageAndRestartActivity(context: Context, lang: String) {
    val updatedContext = updateLocale(context, lang)
    val intent = Intent(updatedContext, context::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    (context as Activity).finish()
}

fun updateLocale(context: Context, lang: String): Context {
    val locale = Locale(lang)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return context.createConfigurationContext(config)
}




