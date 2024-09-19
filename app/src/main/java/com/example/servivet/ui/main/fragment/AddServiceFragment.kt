package com.example.servivet.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.annotation.GlideModule
import com.example.servivet.R
import com.example.servivet.data.model.CustomeServiceModeData
import com.example.servivet.data.model.add_service.request.AddServiceRequest
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.save_address.request.SaveAddressRequest
import com.example.servivet.databinding.FragmentAddServiceBinding
import com.example.servivet.databinding.ImagePickerLayoutBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.AddLocationForServices
import com.example.servivet.ui.main.adapter.AddServiceImageAdapter
import com.example.servivet.ui.main.adapter.AddServiceModePriceAdapter
import com.example.servivet.ui.main.view_model.AddServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.checkVideoFileSize
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson

@GlideModule
class AddServiceFragment :
    BaseFragment<FragmentAddServiceBinding, AddServiceViewModel>(R.layout.fragment_add_service) {
    private var showDayList = ArrayList<String>()
    private var imagerequestcode: Int = 0
    private var imagePath: String = ""
    private var dialog: Dialog? = null
    private val stringList = ArrayList<String>()
    private var categoryList = ArrayList<String>()
    lateinit var subCategoryList: ArrayList<String>
    private var category = ArrayList<HomeServiceCategory>()
    var adapter: ArrayAdapter<String>? = null
    var hashMap = HashMap<String, CustomeServiceModeData>()
    var daysList: ArrayList<Days>? = null
    private lateinit var type: String
    var addServicesRequest = AddServiceRequest()
    var latitude: String? = null
    var longitude: String? = null
    var fulladress: String? = null
    var addServiceModePriceAdapter: AddServiceModePriceAdapter? = null
    override val binding: FragmentAddServiceBinding by viewBinding(FragmentAddServiceBinding::bind)
    override val mViewModel: AddServiceViewModel by viewModels()
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(
                requireContext(), binding, requireActivity(), requireActivity().isFinishing
            )
        }
        category = Session.category
        setCategorySpinner()
        setClick()
        initClickEvent()
        binding.addressLl.setOnClickListener {}

    }

    private var addLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            Log.d("TAG", "$: ${Gson().toJson(data)}")
            val jsonResult = data?.getStringExtra("LocationResult")
            val saveAddressRequest = Gson().fromJson(jsonResult, SaveAddressRequest::class.java)
            Log.d("TAG", " saveAddressRequest $saveAddressRequest: ")
            binding.idAddress.text = saveAddressRequest.fullAddress
            mViewModel.addServicesRequest.latitute = saveAddressRequest.latitute
            mViewModel.addServicesRequest.longitute = saveAddressRequest.longitute
            mViewModel.addServicesRequest.address = saveAddressRequest.fullAddress

            // Handle the result here
            Log.d("TAG", " saveAddressRequest ${saveAddressRequest.fullAddress}: ")
        }
    }

    private fun initClickEvent() {
        binding.idAddress.setOnClickListener {
            val intent = Intent(requireContext(), AddLocationForServices::class.java)
            addLocationLauncher.launch(intent)
//            findNavController().navigate(
//                AddServiceFragmentDirections.actionAddServiceFragmentToAddLocationFragment()
//            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1001) {

        }
    }

    private fun setDaysArray() {
        daysList = ArrayList()
        showDayList = ArrayList()
        showDayList.add("MON")
        showDayList.add("TUE")
        showDayList.add("WED")
        showDayList.add("THU")
        showDayList.add("FRI")
        showDayList.add("SAT")
        showDayList.add("SUN")
        var slotList = ArrayList<ServiceListSlot>()
        slotList.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Monday", true, slotList))

        var slotList1 = ArrayList<ServiceListSlot>()
        slotList1.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Tuesday", false, slotList1))

        var slotList2 = ArrayList<ServiceListSlot>()
        slotList2.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Wednesday", false, slotList2))

        var slotList3 = ArrayList<ServiceListSlot>()
        slotList3.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Thursday", false, slotList3))

        var slotList4 = ArrayList<ServiceListSlot>()
        slotList4.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Friday", false, slotList4))

        var slotList5 = ArrayList<ServiceListSlot>()
        slotList5.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Saturday", false, slotList5))

        var slotList6 = ArrayList<ServiceListSlot>()
        slotList6.add(ServiceListSlot("", "10", ""))
        daysList?.add(Days("Sunday", false, slotList6))
    }

    private fun setSubCategoriesSpinner(position: Int) {
        subCategoryList = ArrayList()
        subCategoryList.add(getString(R.string.select_sub_category))
        for (i in category[position].subCategory?.indices!!) {
            subCategoryList.add(category[position].subCategory?.get(i)?.name!!)
        }

        adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            subCategoryList as List<String?>
        )
        binding.subCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, i: Int, l: Long
                ) {
                    mViewModel.subCatPostion = i
                    if (i > 0) mViewModel.addServicesRequest.subCategory =
                        category[position].subCategory?.get(i - 1)?._id
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        binding.subCategorySpinner.adapter = adapter

    }

    private fun setCategorySpinner() {
        categoryList.add("select category")
        for (i in category.indices) {
            category[i].name?.let { categoryList.add(it) }
        }
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            categoryList as List<String?>
        )
        binding.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, i: Int, l: Long
                ) {
                    mViewModel.catPostion = i
                    if (i > 0) {
                        mViewModel.addServicesRequest.category =
                            category[i - 1]._id    // some doubt
                        setSubCategoriesSpinner(i - 1)
                    } else {
                        setSubCategoriesSpinner(i)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        binding.categorySpinner.adapter = adapter

    }

    private fun setClick() {
        binding.addImageBtn.setOnClickListener {
            type = getString(R.string.image)
            selectImage()
        }
//        binding.addVideo.setOnClickListener {
//            type = getString(R.string.videos)
//            selectImage()
//        }

        binding.atHome.setOnClickListener {
            mViewModel.isHomeClick = if (!mViewModel.isHomeClick) {
                setDaysArray()
                hashMap[Constants.AT_HOME] =
                    CustomeServiceModeData(Constants.AT_HOME, true,/* list, */daysList)
                setAdapter(showDayList)
                binding.homeCheckBox.setBackgroundResource(R.drawable.selected_checkbox)
                mViewModel.addServicesRequest.atHome = true
                true
            } else {
                hashMap.remove(Constants.AT_HOME)
                setAdapter(showDayList)
                binding.homeCheckBox.setBackgroundResource(R.drawable.unselected_checkbox)
                mViewModel.addServicesRequest.atHome = false
                false
            }
        }

        binding.atCentre.setOnClickListener {
            mViewModel.isCentreClick = if (!mViewModel.isCentreClick) {
                setDaysArray()
                hashMap[Constants.AT_CENTER] =
                    CustomeServiceModeData(Constants.AT_CENTER, true, /*list,*/ daysList)
                setAdapter(showDayList)
                binding.centreCheckBox.setBackgroundResource(R.drawable.selected_checkbox)
                mViewModel.addServicesRequest.atCenter = true
                true
            } else {
                hashMap.remove(Constants.AT_CENTER)
                setAdapter(showDayList)
                binding.centreCheckBox.setBackgroundResource(R.drawable.unselected_checkbox)
                mViewModel.addServicesRequest.atCenter = false
                false
            }
            binding.address.isVisible = mViewModel.isCentreClick
        }
    }

    private fun setAdapter(showDayList: ArrayList<String>) {
        val list = ArrayList<CustomeServiceModeData>(hashMap.values)
        if (list.size > 0) {
            binding.priceRecycler.visibility = View.VISIBLE
            addServiceModePriceAdapter =
                AddServiceModePriceAdapter(requireContext(), list, mViewModel, showDayList) {
                    list[it.position] = it.data
                    addServiceModePriceAdapter?.updateData(list)

                }
            binding.priceRecycler.adapter = addServiceModePriceAdapter
        } else {
            binding.priceRecycler.visibility = View.GONE
        }
    }

    private fun selectImage() {
        val permission: Array<String?> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA
            )
            else arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (CommonUtils.requestPermissions(requireActivity(), 100, permission)) {
            imagerequestcode = 1
            openImagePicker()
        }/*else {
            Toast.makeText(requireContext(), getString(R.string.servivet_require_this_permission), Toast.LENGTH_SHORT).show()
            val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName))
            requireActivity().finish()
            startActivity(i)
        }*/
    }

    private val startForCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val bitmap = data!!.extras!!["data"] as Bitmap?
                imagePath = CommonUtils.getRealPathFromURI(
                    requireActivity(), CommonUtils.getImageUri(requireActivity(), bitmap!!)
                )!!
                stringList.add(imagePath)
                mViewModel.addServicesRequest.image = stringList
                mViewModel.isPhotoSelected = true
            }
            setImageAdapter(stringList)

        }

    @SuppressLint("SuspiciousIndentation")


    private fun setImageAdapter(list: ArrayList<String>) {
        if (list != null && list.isNotEmpty()) {
            binding.imageRecycler.visibility = View.VISIBLE
            binding.imageRecycler.adapter = AddServiceImageAdapter(
                requireContext(), list, ArrayList()
            ) {
                val list = ArrayList<String>()
                for (i in it.indices) {
                    list.add(it[i])
                }
                mViewModel.addServicesRequest.image = it
            }
        } else {
            binding.imageRecycler.visibility = View.GONE
        }
    }

    private fun openImagePicker() {
        dialog = Dialog(requireContext(), R.style.BottomSheetDialogTheme)
        val layoutInflater = LayoutInflater.from(requireContext())
        val imagePickerLayoutBinding: ImagePickerLayoutBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.image_picker_layout, null, false)
        dialog!!.setContentView(imagePickerLayoutBinding.getRoot())
        val window = dialog!!.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        dialog!!.window!!.setBackgroundDrawable(inset)
        imagePickerLayoutBinding.camera.isVisible = type == getString(R.string.image)
        imagePickerLayoutBinding.camera.setOnClickListener { view ->
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startForCamera.launch(takePicture)
            dialog!!.dismiss()
        }
        imagePickerLayoutBinding.gallery.setOnClickListener {
            val intent = Intent()
            if (type == getString(R.string.image)) {
                intent.type = "image/*"
            } else {
                intent.type = "video/*"
            }
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startForImageGallery.launch(intent)
            dialog!!.dismiss()
        }
        dialog!!.show()

    }

    private val startForImageGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data!!.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imagePath = com.example.servivet.utils.getVideoPathFromUri(
                            requireActivity(), imageUri
                        ).toString()
                        val fileSize = checkVideoFileSize(imagePath)

                        if (fileSize < 100) {
                            stringList.add(imagePath)
                            mViewModel.addServicesRequest.image = stringList
                            mViewModel.isPhotoSelected = true
                        } else {
                        }

                    }
                } else {
                    val fileUri = data.data
                    if (fileUri!!.path!!.isNotEmpty()) {
                        imagePath =
                            CommonUtils.getRealPathFromURI(requireActivity(), fileUri).toString()
                        stringList.add(imagePath)
                        Log.e("TAG", "datadata:${imagePath} ")

                        mViewModel.addServicesRequest.image = stringList
                        mViewModel.isPhotoSelected = true
                    }
                }
                setImageAdapter(stringList)
            }
        }

    override fun setupObservers() {
        mViewModel.errorMessage.observe(viewLifecycleOwner) {
            showSnackBar(it)
        }
        mViewModel.addServiceResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            showToast(it.data.message ?: "Something went wrong")
                            findNavController().popBackStack()
                            // findNavController().navigate(R.id.action_addServiceFragment_to_myServiceFragment)
                        }

                        StatusCode.STATUS_CODE_FAIL -> {
                            showSnackBar(it.data.message)
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.startDialog(requireContext())
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog()
                    it.message?.let {
                        showSnackBar(it)
                    }
                }

                Status.UNAUTHORIZED -> {
                    CommonUtils.logoutAlert(
                        requireContext(), "Session Expired", "Unauthorized User", requireActivity()
                    )
                }
            }
        }
    }


    data class Days(
        var days: String = "",
        var isSelected: Boolean = false,
        var slotList: ArrayList<ServiceListSlot>? = null,
        var position: Int? = null
    ) : ListAdapterItem

    fun getVideoThumbnailFromPath(videoPath: String): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaMetadataRetriever
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(videoPath)
                retriever.frameAtTime
            } catch (e: Exception) {
                null
            } finally {
                retriever.release()
            }
        } else {
            // For versions below Android Q, you might consider using Glide or another image loading library
            // to load the video thumbnail into an ImageView directly.
            // Glide example:
            // Glide.with(context).load(videoPath).into(imageView)
            null
        }
    }


    fun getVideoPathFromUri(context: Context, videoUri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        var cursor: Cursor? = null

        try {
            cursor = context.contentResolver.query(videoUri, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            Log.e("VideoPath", "Error getting video path: ${e.message}")
        } finally {
            cursor?.close()
        }

        return path
    }
}