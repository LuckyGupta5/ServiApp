/*
package com.ripenapps.conveyr.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.crashlytics.internal.common.CommonUtils
import com.ripenapps.conveyr.BuildConfig
import com.ripenapps.conveyr.R
import com.ripenapps.conveyr.base.BaseActivity2
import com.ripenapps.conveyr.databinding.ActivitySplashBinding
import com.ripenapps.conveyr.network.Resource
import com.ripenapps.conveyr.network.retrofit.ApiService
import com.ripenapps.conveyr.shared_pref.PrefEntity
import com.ripenapps.conveyr.shared_pref.Preference
import com.ripenapps.conveyr.ui.splash.SplashRepo
import com.ripenapps.conveyr.ui.splash.SplashViewModel
import com.ripenapps.conveyr.ui.story.add_story.AddStoryFragment
import com.ripenapps.conveyr.utils.*
import com.ripenapps.conveyr.utils.soundservices.OnClearFromRecentService
import io.agora.rtc2.internal.CommonUtility
import kotlin.system.exitProcess


@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashActivity : BaseActivity2<SplashViewModel, SplashRepo>() {

    private var mBinding: ActivitySplashBinding? = null
    private var isLogin: Boolean = false
    private var authToken: String? = null
    private var versionType: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (isAppOnForeground(this)) {
//            startService(Intent(this@SplashActivity, OnClearFromRecentService::class.java))
//        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        authToken = Preference.getPreference(this, PrefEntity.BEARER_TOKEN).toString()
        isLogin = Preference.getPreference_boolean(this, PrefEntity.isLogin)
        val uri = intent.data

        if (uri != null) {
            val params = uri.pathSegments
            val id = params[params.size - 1]
            Log.e(TAG, "onCreate: $id")
        }
*/
/*
        if (!Settings.canDrawOverlays(this)) {   //Android M Or Over
            intent=  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,103);
            return;
        }else{
            versionAPI()

        }
*//*




    }

    override fun onStart() {
        super.onStart()
        if (isMiUi() && !Settings.canDrawOverlays(this)) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", packageName)
            startActivityForResult(intent,101)
        } else if (!isMiUi() && !Settings.canDrawOverlays(this)){
            intent=  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent,103)
            return
        } else if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            requestPermissionForCall()
        else
            versionAPI()
//            goFurtherInApp()


    }

    private fun requestPermissionForCall() {
        val permission = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.BLUETOOTH, android.Manifest.permission.MODIFY_AUDIO_SETTINGS, android.Manifest.permission.CAMERA)
//        ActivityCompat.requestPermissions(this@SplashActivity,permission,100)
        if (requestPermissions(this@SplashActivity, 100, permission))
            versionAPI()
        toast(getString(R.string.conveyr_requires_permission))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty())
            versionAPI()
    }

    override fun getFragmentRepository(): SplashRepo = SplashRepo(retrofitClient.buildApi(ApiService::class.java, authToken))

    private fun versionAPI() {
        if (isNetworkAvailable(this)) viewModel.version()
        else toast(getString(R.string.no_internet))

        viewModel.versionResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    //   val versionName: String = BuildConfig.VERSION_NAME
                    val versionCode: String = BuildConfig.VERSION_CODE.toFloat().toString()
                    Log.i(TAG, "versionAPI: "+versionCode+"   => "+it.value.android.version)
                    //   val versionType = it.value.android.forceUpdate
//                    Log.d("MyVersion", "" + versionCode.toDouble() + "----->" + it.value.android.version)

                    if (it.value.android.version != versionCode) {
                        customAlertVersionUpdate(this, getString(R.string.update_version))
//                        goFurtherInApp()
                    } else {
                        goFurtherInApp()
                    }

                }
                else -> {
                    // toast(getString(R.string.something_wrong))
                }

            }

        }

    }

    private fun customAlertVersionUpdate(context: Context, textValue: String) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.versionlayout)
        val window = dialog.window
        window!!.setBackgroundDrawableResource(R.drawable.white_back_corner_8dp)

        val text = dialog.findViewById(R.id.alert_text) as TextView
        val appName = dialog.findViewById(R.id.alert_header) as TextView

        appName.text = context.resources.getString(R.string.app_name)
        text.text = textValue

        val cancel = dialog.findViewById<RelativeLayout>(R.id.linear_cancel)
        val yes = dialog.findViewById<RelativeLayout>(R.id.linear_purchase)
        dialog.findViewById<TextView>(R.id.text_cancle)
        val textYes = dialog.findViewById<TextView>(R.id.text_purchase)

        textYes.text = resources.getString(R.string.yes)

        cancel?.setOnClickListener {

            dialog.cancel()
            // 0 for not mandatory to update
            //1 for mandatory update
            if (!versionType!!) {
                goFurtherInApp()
            } else {
                exitProcess(0)
            }
        }

        yes?.setOnClickListener {
            dialog.cancel()
            updateFromPlayStore(this)
        }
        dialog.show()

    }

    private fun updateFromPlayStore(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 103) {
            */
/*if (Settings.canDrawOverlays(this))
                versionAPI()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
//                    versionAPI()
                    requestPermissionForCall()
                }
                else{
//                    Toast.makeText(this,"Please allow display over others App ",Toast.LENGTH_LONG).show()

                    if (!Settings.canDrawOverlays(this)) {   //Android M Or Over
                        intent=  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent,103);
                        return;
                    }
                }
            }
*//*

        }
        else if (requestCode==101){
            requestPermissionForCall()
        }
    }

    private fun goFurtherInApp() {
        Handler().postDelayed({
            if (isLogin) {
                val restartIntent = Intent(this, LandingActivity::class.java)
                restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(restartIntent)
                finish()
            } else {
                startNewActivity(LoginActivity::class.java)
            }
        }, 1000)
    }

    companion object {
        private const val TAG = "SplashActivity"
    }

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

}
*/
