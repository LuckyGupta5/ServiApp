package com.example.servivet.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.ui.main.activity.MainActivity
import com.example.servivet.ui.main.view_model.SplashViewModel
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.*
import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


object CommonUtils
{


    @JvmStatic
    @BindingAdapter("android:circularImage")
    fun loadCircularImage(view: View?, image_url: String?) {
        val imageView = view as ImageView?
        Glide.with(view!!)
            .load(image_url)/*.error(R.mipmap.circular_image)*/
            .into(imageView!!)
    }

    fun requestPermissions(
        activity: Activity, PERMISSION_REQUEST_CODE: Int, permissions: Array<String?>
    ): Boolean {
        val isAllPermissionGranted = booleanArrayOf(false) // Initialize with false

        Dexter.withContext(activity)
            .withPermissions(*permissions)
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
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    // Handle the rationale message and re-request the permission
                    showRationaleDialog(activity, token)
                }
            })
            .withErrorListener {
                Log.e("TAG", "requestPermissions: " + it)
            }
            .onSameThread()
            .check()

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
            showSettingsDialog(activity,100)
            // Handle denial, if needed
        }
        dialog.show()
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

    private fun getDayinMonth(currentmonth: Int, getcurrentYear: Int):List<String> {
        var calender=Calendar.getInstance()
        calender.set(Calendar.YEAR,getcurrentYear)
        calender.set(Calendar.MONTH,currentmonth)
        calender.set(Calendar.DAY_OF_MONTH,1)
        val daysInMonth = mutableListOf<String>()

        while (calender.get(Calendar.MONTH) == currentmonth) {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            daysInMonth.add(dateFormat.format(calender.time))
            calender.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysInMonth

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateFromToday(): Array<String?> 
    {
        var currentDate1: LocalDate = LocalDate.now()

        val dayofMonthv= currentDate1.dayOfMonth-1
       // var currentDate: LocalDate = LocalDate.now().minusDays(dayofMonthv.toLong()-1)
        var currentDate: LocalDate = LocalDate.now()
        Log.e("TAG", "getDateFromToday1: ${getDaysInCurrentMonth()}", )

        val monthDaysCount = getDaysInCurrentMonth()-dayofMonthv
        val dates = arrayOfNulls<String>(monthDaysCount)

        for (i in 0..<monthDaysCount) {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, yyyy-MM-dd")
            dates[i] = currentDate.format(formatter)
            currentDate = currentDate.plusDays(1)
            Log.e("TAG", "getDateFromToday: $i", )
        }
        return dates
    }



    fun monthYearFromDate(input: String?): String? {
        val inFormat =
            SimpleDateFormat("EEE, yyyy-MM-dd", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = inFormat.parse(input)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dateFormat = SimpleDateFormat("MMM yyyy")
        return dateFormat.format(date)
    }

    fun getDaysInCurrentMonth(): Int {
        val calendar = Calendar.getInstance()

        // Set the calendar to the first day of the month
       // calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Get the maximum value for the day of the month, which gives the number of days in the month
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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
    
    fun dayFromDate(input: String?): String? {
//        Log.i("TAG", "dayFromDate: "+ input);
        val inFormat =
            SimpleDateFormat("EEE, yyyy-MM-dd", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = inFormat.parse(input)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dayFormat = SimpleDateFormat("EEE")
        return dayFormat.format(date)
    }
    fun dateFromDate(input: String?): String? {
        val inFormat =
            SimpleDateFormat("EEE, yyyy-MM-dd", Locale.ENGLISH)
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
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf<String>(imgId), null
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
            inContext.contentResolver,
            inImage,
            "Title" + System.currentTimeMillis(),
            null
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
        val dfDate = SimpleDateFormat("hh:mm a")
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


    fun selectTime(context: Context,textView: TextView ,callback:(data:String)->Unit): String {
        var mSelectedTime=""
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, minute)
            mSelectedTime = SimpleDateFormat("hh:mm a").format(c.time)
            callback(mSelectedTime)
            textView.text = mSelectedTime
        }
        val timePickerDialog = TimePickerDialog(
            context, AlertDialog.THEME_HOLO_LIGHT, timeSetListener, hour, minute,
            false
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
        Glide.with(view!!).load(image_url).error(R.drawable.flower_img).into(imageView!!)
    }

    fun datePickerNew2(context: Context?, selectedDate: String?, finalListener: CalenderResponseListener<Any?>) {
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 0)
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            context!!,
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                var monthOfYear = monthOfYear
                var day = ""
                var month = ""
                day = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    dayOfMonth.toString()
                }
                monthOfYear++
                month = if (monthOfYear < 10) {
                    "0$monthOfYear"
                } else {
                    monthOfYear.toString()
                }
                val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
                finalListener.onReceiveResponse("$day-$month-$year")
                val timestamp = Timestamp(calendar.timeInMillis)
                finalListener.onDateTimeStamp("$year-$month-$dayOfMonth")
            }, mYear, mMonth, mDay
        )
        if (selectedDate != null && selectedDate != "") {
            val items1 = selectedDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val date1 = items1[0]
            val month = items1[1]
            val year = items1[2]
            datePickerDialog.updateDate(year.toInt(), month.toInt() - 1, date1.toInt())
        }
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        // dp.setMinDate(new Date(System.currentTimeMillis() + 2*24*60*60*1000));

//        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show()
    }
    fun datePickerNew3(
        context: Context?,
        selectedDate: String?,
        finalListener: CalenderResponseListener<String?>
    ) {
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 0)
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            context!!,
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                var day = ""
                var month = ""
                day = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    dayOfMonth.toString()
                }
                var strMonth = monthOfYear
                strMonth++
                month = if (strMonth!! < 10) {
                    "0$strMonth"
                } else {
                    strMonth.toString()
                }
                val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
                finalListener.onReceiveResponse("$day-$month-$year")
                val timestamp = Timestamp(calendar.timeInMillis)
                finalListener.onDateTimeStamp("$year-$month-$dayOfMonth")
            }, mYear, mMonth, mDay
        )
        if (selectedDate != null && selectedDate != "") {
            val items1 = selectedDate.split("-").toTypedArray()
            val date1 = items1[0]
            val month = items1[1]
            val year = items1[2]
            datePickerDialog.updateDate(year.toInt(), month.toInt() - 1, date1.toInt())
        }
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateFromTimeStamp(date: String?): String? {
        val sdf =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputDate = SimpleDateFormat("dd MMM yyyy")
        val outputTime = SimpleDateFormat("HH:mm:aa")
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




    fun setDate(date: String?): String? {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd")
        @SuppressLint("SimpleDateFormat") val outputFormat = SimpleDateFormat("dd MMM yyyy")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var dateTimeStamp: Date? = Date()
        try {
            dateTimeStamp = sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(dateTimeStamp != null)
        return outputFormat.format(dateTimeStamp)
    }

    fun setDate2(date: String?): String? {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("dd-mm-yyyy")
        @SuppressLint("SimpleDateFormat") val outputFormat = SimpleDateFormat("yyyy-mm-dd")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var dateTimeStamp: Date? = Date()
        try {
            dateTimeStamp = sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        assert(dateTimeStamp != null)
        return outputFormat.format(dateTimeStamp)
    }

    fun convertDateFormate(date:String):String{
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")
        val date: Date = inputFormat.parse(date)
        val outputDateStr: String = outputFormat.format(date)
        return outputDateStr
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun formatDate(date: String?, initDateFormat: String?, endDateFormat: String?): String? {
        val initDate = SimpleDateFormat(initDateFormat).parse(date!!)
        val formatter = SimpleDateFormat(endDateFormat)
        return formatter.format(initDate!!)
    }

    interface CalenderResponseListener<T> {
        fun onReceiveResponse(response: T)
        fun onTimeResponse(response: T)
        fun onDateTimeStamp(response: T)
    }


    fun getRealPathFromDocumentUri(context: Context, uri: Uri): String? {
        /*        var filePath = ""
                val p = Pattern.compile("(\\d+)$")
                val m = p.matcher(uri.toString())
                if (!m.find()) {
                    return filePath
                }
                val imgId = m.group()
                val column = arrayOf(MediaStore.Images.Media.DATA)
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(imgId), null
                )
                val columnIndex = cursor!!.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
                return filePath*/

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
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
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
                    "/document/raw:",
                    ""
                )

                newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                    "/document/primary:",
                    "/storage/emulated/0/"
                )

                else -> return null
            }
        }
        return if (path.isNullOrEmpty())
            null
        else
            File(path).toString()

    }


    fun Fragment.showSnackBar(message: String) {

        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()

    }

    fun Fragment.showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
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

    fun alert(context: Context?, msg: String?,requireActivity: Activity) {


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
            Toast.makeText(requireActivity,"Log out successfully !", Toast.LENGTH_SHORT).show()
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
            ContextCompat.startActivity(context, Intent(requireActivity, MainActivity::class.java), null)
            requireActivity.finish()
        }

        alert.show()
    }
    fun customalertdialog(context: Context?, msg: String?,from:Int) {

        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.alert_dialog_custom)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.all_border_round_10dp_apptheme)
        val title = dialog.findViewById<TextView>(R.id.title)
        title.setText(R.string.app_name)
        val message = dialog.findViewById<TextView>(R.id.message)
        message.text = msg
        /* val view = dialog.findViewById<View>(R.id.viewCenter)
         view.visibility = View.GONE
        */  val dialogButton = dialog.findViewById<TextView>(R.id.yes)
        val dialogno=dialog.findViewById<TextView>(R.id.no)
        //dialogButton.gravity = Gravity.CENTER_HORIZONTAL
        dialogno.setOnClickListener { v:View?->
            dialogno.isEnabled.and(true)
            dialog.dismiss()}
        dialogButton.setOnClickListener { v: View? ->
            /*  Session.logout()
              SplashViewModel.isLogout
              Session.saveLogin("flase")
            */
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

    /*    fun getImageUri(inContext: Context, inImage: Bitmap): File {

            //create a file to write bitmap data
            val f = File(inContext.cacheDir, "filename.jpg")
            f.createNewFile()

            //Convert bitmap to byte array
            val bitmap = inImage
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            return f
        }*/

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

    fun setCurrentDate(): String {
        val currentDate: String =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        //Log.d("date",""+currentDate)
        return currentDate
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

@BindingAdapter("android:setUtcToLocalTimeStamp")
fun setUtcToLocalTimeStamp(textView: AppCompatTextView, dateStr: String) {
    val df = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(dateStr)
    df.timeZone = TimeZone.getDefault()
    val formattedDate = df.format(date)
    textView.text = formattedDate
}
