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
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.databinding.FragmentCompleteProfileBinding
import com.example.servivet.databinding.ImagePickerLayoutBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.activity.HomeActivity
import com.example.servivet.ui.main.view_model.CompleteProfileViewModel
import com.example.servivet.ui.main.view_model.EditProfileVewModel
import com.example.servivet.utils.CommonUtils
import com.example.servivet.utils.CommonUtils.showSnackBar
import com.example.servivet.utils.CommonUtils.showToast
import com.example.servivet.utils.Constants
import com.example.servivet.utils.NoLeadingSpaceInputFilter
import com.example.servivet.utils.ProcessDialog
import com.example.servivet.utils.Session
import com.example.servivet.utils.Status
import com.example.servivet.utils.StatusCode

class CompleteProfileFragment :
    BaseFragment<FragmentCompleteProfileBinding, CompleteProfileViewModel>(R.layout.fragment_complete_profile) {
    private var imagerequestcode: Int = 0
    var dialog: Dialog? = null
    private var type = ""
    private var imagePath: String = ""
    override val binding: FragmentCompleteProfileBinding by viewBinding(
        FragmentCompleteProfileBinding::bind
    )
    override val mViewModel: CompleteProfileViewModel by viewModels()

    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
        if (isAdded)
            binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
            click = mViewModel.ClickAction(binding, requireContext(),requireActivity(),requireActivity().isFinishing)
        }
        mViewModel.mobilenumber = arguments?.getString(Constants.MOBILE_NUMBER)!!
        mViewModel.countrycode = arguments?.getString(Constants.COUNTRY_CODE)!!
        binding.mobileNo.setText("+" + mViewModel.countrycode + " " + mViewModel.mobilenumber)
        setData()
        setBack()

    }

    private fun setData() {
        EditProfileVewModel.isPhotoSelected = false
        mViewModel.editProfileRequest.mobile = mViewModel.mobilenumber
        mViewModel.editProfileRequest.countryCode = mViewModel.countrycode
        mViewModel.editProfileRequest.email = binding.email.text.toString()
        mViewModel.editProfileRequest.name = binding.name.text.toString()
        binding.name.filters = arrayOf(InputFilter.LengthFilter(30), NoLeadingSpaceInputFilter())
        binding.email.filters = arrayOf(InputFilter.LengthFilter(50), NoLeadingSpaceInputFilter())
        if (mViewModel.editProfileRequest.name!!.isNotEmpty()) {
            mViewModel.name.value = true

        }
        if (mViewModel.editProfileRequest.email != null && mViewModel.editProfileRequest.email!!.isNotEmpty())
            mViewModel.enailET.value = true
        binding.profileImage.setOnClickListener {
            EditProfileVewModel.isPhotoSelected = true
            type = "1"
            selectImage()
        }
        binding.camera.setOnClickListener {
            EditProfileVewModel.isPhotoSelected = true
            type = "1"
            selectImage()
        }

    }


    private fun selectImage() {
        val permission: Array<String?> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
            else
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)



        if (CommonUtils.requestPermissions(requireActivity(), 100, permission)) {
            imagerequestcode = 0
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
                imagePath = CommonUtils.getRealPathFromURI(
                    requireActivity(),
                    CommonUtils.getImageUri(requireActivity(), bitmap!!)
                )!!


                when (type) {
                    "1" -> {
                        Glide.with(requireContext()).load(imagePath).into(binding.profileImage)
                        mViewModel.editProfileRequest.image = imagePath
                        EditProfileVewModel.isPhotoSelected = true

                    }

                    /*"2" -> {
                        Glide.with(requireContext()).load(imagePath).into(binding.coverImage)
                        binding.changeCover.visibility = View.GONE
                        binding.coverImage.visibility = View.VISIBLE
                        mViewModel.editProfileRequest.coverImage=imagePath
                        EditProfileVewModel.isPhotoSelectedCoverImage=true
                    }*/
                }
            }
        }

    private fun setBack() {

    }


    @SuppressLint("SuspiciousIndentation")
    private val startForImageGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data!!.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imagePath =
                            CommonUtils.getRealPathFromURI(requireActivity(), imageUri).toString()
                        when (type) {
                            "1" -> {
                                Glide.with(requireContext()).load(imageUri)
                                    .into(binding.profileImage)
                                mViewModel.editProfileRequest.image = imagePath
                                EditProfileVewModel.isPhotoSelected = true
                            }

                            /*  "2" -> {
                                  Glide.with(requireContext()).load(imageUri).into(binding.coverImage)
                                  binding.changeCover.visibility = View.GONE
                                  binding.coverImage.visibility = View.VISIBLE
                                  mViewModel.editProfileRequest.coverImage=imagePath
                                  EditProfileVewModel.isPhotoSelectedCoverImage=true

                              }
      */
                        }
                    }
                } else {
                    val fileUri = data.data
                    if (fileUri!!.path!!.isNotEmpty())
                        imagePath =
                            CommonUtils.getRealPathFromURI(requireActivity(), fileUri).toString()

                    when (type) {
                        "1" -> {
                            Glide.with(requireContext()).load(fileUri).into(binding.profileImage)
                            mViewModel.editProfileRequest.image = imagePath
                            EditProfileVewModel.isPhotoSelected = true

                        }

                        /* "2" -> {
                             Glide.with(requireContext()).load(fileUri).into(binding.coverImage)
                             binding.changeCover.visibility = View.GONE
                             binding.coverImage.visibility = View.VISIBLE
                             mViewModel.editProfileRequest.coverImage=imagePath
                             EditProfileVewModel.isPhotoSelectedCoverImage=true

                         }*/
                    }
                }

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
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startForImageGallery.launch(intent)
            dialog!!.dismiss()
        }
        dialog!!.show()

    }


    override fun setupViews() {
    }

    override fun setupObservers() {
        mViewModel.errorMessage.observe(viewLifecycleOwner) { showSnackBar(it) }
        mViewModel.editProfileResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog()
                    when (it.data?.code) {
                        StatusCode.STATUS_CODE_SUCCESS -> {
                            Session.saveIsLogin(true)
                            showToast(it.data.message)
                            var intent = Intent(context, HomeActivity::class.java)
                            startActivity(intent)
                            (context as Activity).finish()
                            EditProfileVewModel.isPhotoSelected = false
                            //  EditProfileVewModel.isPhotoSelectedCoverImage = false
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