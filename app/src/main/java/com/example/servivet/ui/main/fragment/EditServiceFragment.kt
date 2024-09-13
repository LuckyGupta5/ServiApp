package com.example.servivet.ui.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.data.model.CustomeServiceModeData
import com.example.servivet.data.model.SimpleImageModel
import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.data.model.home.response.HomeServiceCategory
import com.example.servivet.data.model.service_category_details.response.ServiceDetail
import com.example.servivet.databinding.FragmentEditServiceBinding
import com.example.servivet.databinding.ImagePickerLayoutBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.adapter.EditServiceImageAdapter
import com.example.servivet.ui.main.adapter.EditServiceModePriceAdapter
import com.example.servivet.ui.main.view_model.EditServiceViewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode
import com.example.servivet.utils.checkVideoFileSize
import com.example.servivet.utils.getVideoPathFromUri
import com.google.gson.Gson


class EditServiceFragment :
    BaseFragment<FragmentEditServiceBinding, EditServiceViewModel>(R.layout.fragment_edit_service) {
    override val binding: FragmentEditServiceBinding by viewBinding(FragmentEditServiceBinding::bind)
    override val mViewModel: EditServiceViewModel by viewModels()
    var data: ServiceDetail? = null
    private var imagerequestcode: Int = 0
    private var imagePath: String = ""
    private var latitude: String = ""
    private var longitude: String = ""
    var addServiceModePriceAdapter: EditServiceModePriceAdapter? = null
    private var dialog: Dialog? = null
    private lateinit var type: String

    // private val stringList = ArrayList<String>()
    private var showDayList = ArrayList<String>()
    var daysList: ArrayList<AddServiceFragment.Days>? = null
    var hashMap = HashMap<String, CustomeServiceModeData>()
    var adapter: ArrayAdapter<String>? = null
    private var categoryList = ArrayList<String>()
    lateinit var subCategoryList: ArrayList<String>
    private var categoryIDList = ArrayList<String>()
    private var subCategoryIDList = ArrayList<String>()
    private var isFirst = true
    private var category = ArrayList<HomeServiceCategory>()

    //   private var deleteImageList = ArrayList<String>()
    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        ProcessDialog.dismissDialog()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(
                requireContext(),
                binding,
                requireActivity(),
                requireActivity().isFinishing
            )
        }
        data = arguments?.getSerializable(Constants.DATA) as ServiceDetail?
        setEditData(data)


    }


    override fun setupViews() {
    }

    private fun setEditData(data: ServiceDetail?) {
        category = Session.category
        setCategorySpinner(category)

        //pre selected data
        mViewModel.addServicesRequest.category = data!!.category
        mViewModel.addServicesRequest.subCategory = data.subCategory
        mViewModel.addServicesRequest.aboutService = data.aboutService
        mViewModel.addServicesRequest.serviceName = data.serviceName
        mViewModel.addServicesRequest.atCenter = data.serviceMode?.atCenter
        mViewModel.addServicesRequest.atHome = data.serviceMode?.atHome
        mViewModel.addServicesRequest.atCenterAvailability = data.atCenterAvailability
        mViewModel.addServicesRequest.atHomeAvailability = data.atHomeAvailability
        mViewModel.addServicesRequest.atCenterPrice = data.atCenterPrice.toString()
        mViewModel.addServicesRequest.atHomePrice = data.atHomePrice.toString()
        mViewModel.addServicesRequest.serviceId = data._id
        binding.descriptionEditText.setText(data.aboutService)
        binding.serviceModeEdit.setText(data.serviceName)

        for (i in data.images!!.indices)
            mViewModel.imageListing.add(SimpleImageModel("1", "1", data.images[i]))
        setImageAdapter(mViewModel.imageListing)


        //pre selected centre
        if (data.serviceMode?.atCenter == true) {
            setDaysArray()
            hashMap[Constants.AT_CENTER] =
                CustomeServiceModeData(Constants.AT_CENTER, true, /*list,*/ daysList)
            setAdapter(showDayList, data, data.atCenterPrice, data.atHomePrice, "center")
            binding.centreCheckBox.setBackgroundResource(R.drawable.selected_checkbox)
            mViewModel.addServicesRequest.address = binding.addressEt.text.toString()
            mViewModel.addServicesRequest.latitute =  "28.612673"
            mViewModel.addServicesRequest.longitute = "77.377400"
            mViewModel.addServicesRequest.atCenter = true
            mViewModel.isCentreClick = true
            binding.address.visibility = View.VISIBLE
            true
        } else {
            hashMap.remove(Constants.AT_CENTER)
            binding.address.visibility = View.GONE
            mViewModel.isCentreClick = false
            setAdapter(showDayList, data, data.atCenterPrice, data.atHomePrice)
            binding.centreCheckBox.setBackgroundResource(R.drawable.unselected_checkbox)
            mViewModel.addServicesRequest.atCenter = false
            false
        }

        //pre selected home
        if (data.serviceMode?.atHome == true) {
            mViewModel.isHomeClick = if (!mViewModel.isHomeClick) {
                setDaysArray()
                hashMap[Constants.AT_HOME] =
                    CustomeServiceModeData(Constants.AT_HOME, true,/* list, */daysList)
                mViewModel.isHomeClick = true
                setAdapter(showDayList, data, data.atCenterPrice, data.atHomePrice, "home")
                binding.homeCheckBox.setBackgroundResource(R.drawable.selected_checkbox)
                mViewModel.addServicesRequest.atHome = true
                true
            } else {
                hashMap.remove(Constants.AT_HOME)
                mViewModel.isHomeClick = false
                setAdapter(showDayList, data, data.atCenterPrice, data.atHomePrice)
                binding.homeCheckBox.setBackgroundResource(R.drawable.unselected_checkbox)
                mViewModel.addServicesRequest.atHome = false
                false
            }
        }
        setClick()
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
        daysList?.add(AddServiceFragment.Days("Monday", true, slotList))

        var slotList1 = ArrayList<ServiceListSlot>()
        slotList1.add(ServiceListSlot("", "10", ""))
        daysList?.add(AddServiceFragment.Days("Tuesday", false, slotList1))

        var slotList2 = ArrayList<ServiceListSlot>()
        slotList2.add(ServiceListSlot("", "10", ""))
        daysList?.add(AddServiceFragment.Days("Wednesday", false, slotList2))

        var slotList3 = ArrayList<ServiceListSlot>()
        slotList3.add(ServiceListSlot("", "10", ""))
        daysList?.add(AddServiceFragment.Days("Thursday", false, slotList3))

        var slotList4 = ArrayList<ServiceListSlot>()
        slotList4.add(ServiceListSlot("", "10", ""))
        daysList?.add(AddServiceFragment.Days("Friday", false, slotList4))

        var slotList5 = ArrayList<ServiceListSlot>()
        slotList5.add(ServiceListSlot("", "10", ""))
        daysList?.add(AddServiceFragment.Days("Saturday", false, slotList5))

        var slotList6 = ArrayList<ServiceListSlot>()
        slotList6.add(ServiceListSlot("", "10", ""))
        daysList?.add(AddServiceFragment.Days("Sunday", false, slotList6))
    }


    private fun setSubCategoriesSpinner(position: Int) {
        subCategoryList = ArrayList()
        subCategoryList.add(getString(R.string.select_sub_category))
        subCategoryIDList.add("Select id")
        for (i in category[position].subCategory?.indices!!) {
            subCategoryList.add(category[position].subCategory?.get(i)?.name!!)
        }
        for (i in category[position].subCategory?.indices!!) {
            subCategoryIDList.add(category[position].subCategory?.get(i)?._id!!)
        }
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            subCategoryList as List<String?>
        )
        binding.subCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    mViewModel.subCatPostion = i
                    if (i > 0)
                        mViewModel.addServicesRequest.subCategory =
                            category[position].subCategory?.get(i - 1)?._id
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        binding.subCategorySpinner.adapter = adapter
        if (isFirst) {
            for (i in subCategoryIDList.indices) {

                if (subCategoryIDList[i] == data!!.subCategory) {
                    Log.e(
                        "TAG",
                        "setSubCategoriesSpinner: ${subCategoryIDList[i]},${data!!.subCategory},${i}",
                    )
                    binding.subCategorySpinner.setSelection(i)
                    isFirst = false
                }
            }
        } else {
            binding.subCategorySpinner.setSelection(0)
        }

    }

    private fun setCategorySpinner(category: ArrayList<HomeServiceCategory>) {
        categoryList.add("select category")
        categoryIDList.add("select id")
        for (i in this.category.indices) {
            this.category[i].name?.let { categoryList.add(it) }
        }
        for (i in category.indices) {
            this.category[i].id?.let { categoryIDList.add(it) }
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
                            this@EditServiceFragment.category[i - 1]._id    // some doubt
                        setSubCategoriesSpinner(i - 1)
                    } else {
                        setSubCategoriesSpinner(i)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        binding.categorySpinner.adapter = adapter
        for (i in categoryIDList.indices) {
            if (categoryIDList[i] == data!!.category) {
                binding.categorySpinner.setSelection(i)
            }
        }

    }

    private fun setClick() {
        binding.addImageBtn.setOnClickListener {
            type = getString(R.string.image)
            selectImage()
        }
//        binding.idAddVideoBtnn.setOnClickListener {
//            type = getString(R.string.videos)
//            selectImage()
//        }

        binding.atHome.setOnClickListener {
            mViewModel.isHomeClick = if (!mViewModel.isHomeClick) {
                setDaysArray()
                hashMap[Constants.AT_HOME] =
                    CustomeServiceModeData(Constants.AT_HOME, true,/* list, */daysList)
                setAdapter(showDayList, data!!, data!!.atCenterPrice, data!!.atHomePrice)
                binding.homeCheckBox.setBackgroundResource(R.drawable.selected_checkbox)
                mViewModel.addServicesRequest.atHome = true
                true
            } else {
                hashMap.remove(Constants.AT_HOME)
                setAdapter(showDayList, data!!, data!!.atCenterPrice, data!!.atHomePrice)
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
                setAdapter(showDayList, data!!, data!!.atCenterPrice, data!!.atHomePrice)
                binding.centreCheckBox.setBackgroundResource(R.drawable.selected_checkbox)
                mViewModel.addServicesRequest.address = binding.addressEt.text.toString()
                mViewModel.addServicesRequest.latitute ="28.612673"
                mViewModel.addServicesRequest.longitute ="77.377400"
                mViewModel.addServicesRequest.atCenter = true
                true
            } else {
                hashMap.remove(Constants.AT_CENTER)
                setAdapter(showDayList, data!!, data!!.atCenterPrice, data!!.atHomePrice)
                binding.centreCheckBox.setBackgroundResource(R.drawable.unselected_checkbox)
                mViewModel.addServicesRequest.atCenter = false
                false
            }
            binding.address.isVisible = mViewModel.isCentreClick
        }


    }

    private fun setAdapter(
        showDayList: ArrayList<String>,
        data: ServiceDetail,
        atCenterPrice: Double?,
        atHomePrice: Double?,
        comingFrom: String? = null
    ) {
        val list = ArrayList<CustomeServiceModeData>(hashMap.values)

        for (k in list.indices) {
            when (list[k].type) {
                Constants.AT_HOME -> {
                    for (i in list[k].daysList!!.indices) {
                        if (data.atHomeAvailability!!.isNotEmpty() && comingFrom == "home") {
                            for (j in data.atHomeAvailability.indices) {
                                if (list.getOrNull(k)?.daysList?.getOrNull(i)?.days == data.atHomeAvailability[j].day) {
                                    if (list[k]?.daysList!![i].days == "Monday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }
                                    if (list[k]?.daysList!![i].days == "Tuesday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Wednesday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Thursday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Friday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Saturday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Sunday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atHomeAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                }
                            }
                        }
                    }

                }

                Constants.AT_CENTER -> {
                    for (i in list[k]?.daysList!!.indices) {
                        if (data.atCenterAvailability!!.isNotEmpty() && comingFrom == "center") {
                            for (j in data.atCenterAvailability.indices) {
                                if (list[k]?.daysList!![i].days == data.atCenterAvailability[j].day) {
                                    if (list[k]?.daysList!![i].days == "Monday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }
                                    if (list[k]?.daysList!![i].days == "Tuesday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Wednesday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Thursday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Friday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Saturday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                    if (list[k]?.daysList!![i].days == "Sunday") {
                                        list[k]?.daysList!![i].slotList!!.addAll(data.atCenterAvailability!![j].slot!!)
                                        if (list[k].daysList!![i]!!.slotList!!.size > 1) {
                                            list[k].daysList!![i].slotList!!.removeAt(0)
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
            }
            if (list.size > 0) {
                binding.priceRecycler.visibility = View.VISIBLE
                addServiceModePriceAdapter =
                    EditServiceModePriceAdapter(
                        requireContext(),
                        list,
                        mViewModel,
                        showDayList,
                        atCenterPrice.toString(),
                        atHomePrice.toString()
                    ) {
                        list[it.position] = it.data
                        addServiceModePriceAdapter?.updateData(list)

                    }
                binding.priceRecycler.adapter = addServiceModePriceAdapter
            } else {
                binding.priceRecycler.visibility = View.GONE
            }
        }
    }

    private fun selectImage() {
        val permission: Array<String?> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
            else
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (CommonUtils.requestPermissions(requireActivity(), 100, permission)) {
            imagerequestcode = 1
            openImagePicker()
        }/* else {
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
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    imagePath = CommonUtils.getRealPathFromDocumentUri(
                        requireActivity(),
                        CommonUtils.getUriFromBitmap(bitmap!!, requireActivity())!!
                    )!!
                    mViewModel.isPhotoSelected = true
                    mViewModel.imageListing.add(SimpleImageModel("0", "0", imagePath))
                    mViewModel.addServicesRequest.image.add(imagePath)
                } else {
                    imagePath = CommonUtils.getRealPathFromURI(
                        requireActivity(),
                        CommonUtils.getImageUri(requireActivity(), bitmap!!)
                    )!!
                    mViewModel.isPhotoSelected = true
                    mViewModel.imageListing.add(SimpleImageModel("0", "0", imagePath))
                    mViewModel.addServicesRequest.image.add(imagePath)
                }
                //                mViewModel.addServicesRequest.image = stringList
            }
            setImageAdapter(mViewModel.imageListing)

        }

    @SuppressLint("SuspiciousIndentation")
    private val startForImageGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data!!.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imagePath = getVideoPathFromUri(requireActivity(), imageUri).toString()
                        val fileSize = checkVideoFileSize(imagePath)

                        if (fileSize < 100) {
                            mViewModel.isPhotoSelected = true
                            mViewModel.imageListing.add(SimpleImageModel("0", "0", imagePath))
                            mViewModel.addServicesRequest.image.add(imagePath)
                        } else {
                        }
                    }
                } else {
                    val fileUri = data.data
                    if (fileUri!!.path!!.isNotEmpty()) {
                        imagePath =
                            CommonUtils.getRealPathFromURI(requireActivity(), fileUri).toString()

                        Log.e("TAG", "imagePath:${imagePath} ", )
                        mViewModel.isPhotoSelected = true
                        mViewModel.imageListing.add(SimpleImageModel("0", "0", imagePath))
                        mViewModel.addServicesRequest.image.add(imagePath)
//                        mViewModel.addServicesRequest.image = stringList
                    }
                }
                setImageAdapter(mViewModel.imageListing)
            }
        }


    private fun setImageAdapter(imageListing: ArrayList<SimpleImageModel>) {
        var deleteImageList = ArrayList<String>()
        var updatedAddList = ArrayList<String>()
        if (imageListing != null && imageListing.isNotEmpty()) {
            binding.imageRecycler.visibility = View.VISIBLE
            binding.imageRecycler.adapter =
                EditServiceImageAdapter(requireContext(), imageListing) {
                    if (it.status == "1" && it.isDeleted == "1") {
                        deleteImageList.add(it.image)
                        imageListing.remove(it)
                    } else if (it.status == "0" && it.isDeleted == "0") {
                        imageListing.remove(it)
                        if (mViewModel.addServicesRequest.image.contains(it.image)) {
                            mViewModel.addServicesRequest.image.remove(it.image)
                        }
                    }
                    Log.e(
                        "TAG",
                        "setImageAdapter: " + Gson().toJson(mViewModel.addServicesRequest.image)
                    )
                    mViewModel.addServicesRequest.deleteImage = deleteImageList
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
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        imagePickerLayoutBinding.camera.isVisible = type == getString(R.string.image)

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 50)
        dialog!!.window!!.setBackgroundDrawable(inset)

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
                            showToast(it.data.message?:"Something went wrong")
                            findNavController().popBackStack()
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
                        requireContext(),
                        "Session Expired",
                        "Unauthorized User",
                        requireActivity()
                    )
                }
            }
        }
    }


}