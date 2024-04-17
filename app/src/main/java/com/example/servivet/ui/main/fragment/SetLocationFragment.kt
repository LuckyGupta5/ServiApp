package com.example.servivet.ui.main.fragment

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.servivet.R
import com.example.servivet.databinding.FragmentSetLocationBinding
import com.example.servivet.ui.base.BaseFragment
import com.example.servivet.ui.main.view_model.location.SetLocationViewModel
import com.example.servivet.utils.CommonUtils
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException

class SetLocationFragment : BaseFragment<FragmentSetLocationBinding,SetLocationViewModel>(R.layout.fragment_set_location) {
    override val binding: FragmentSetLocationBinding by viewBinding(FragmentSetLocationBinding::bind)
    override val mViewModel: SetLocationViewModel by viewModels()
    private lateinit var autoCompleteSupportFragment: AutocompleteSupportFragment
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null


    override fun isNetworkAvailable(boolean: Boolean) {
    }

    override fun setupViewModel() {
    }

    override fun setupViews() {
        binding.apply {
            lifecycleOwner=viewLifecycleOwner
            viewModel=mViewModel
            click=mViewModel.ClickAction()
        }
        mapInitialization()

    }

    override fun setupObservers() {
    }



    private fun mapInitialization() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
       // mapFragment.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_map_key))
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        searchLocation()
        isPermissionCheck()
    }

    private fun isPermissionCheck() {
      //  if (checkLocationPermission()) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)

            //getDeviceCurrentLocation()
       /// }
    }



    private fun searchLocation() {

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), getString(R.string.google_map_key))
        }
        autoCompleteSupportFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)?.visibility = View.GONE

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).textSize = 15f

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).setTextColor(
            ContextCompat.getColor(requireContext(), R.color.black))

        (autoCompleteSupportFragment.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_input) as EditText).hint = requireActivity().getString(
            R.string.search_location)

        autoCompleteSupportFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))


        autoCompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {

            }

            override fun onPlaceSelected(place: Place) {
               // mMap.clear()
                val geocoder = Geocoder(requireContext())
                var addressList: List<Address> = ArrayList()
                try {
                    addressList = geocoder.getFromLocationName(place.toString(), 2)!!
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList.isNotEmpty())
                {
                    val address: Address = addressList[0]
                    val fullAddress = address.getAddressLine(0)

                    //set source data in singleton

                    autoCompleteSupportFragment.setText(address.getAddressLine(0))
//                    binding.address.text = address!!.getAddressLine(0)
//                    mViewModel.saveAddressRequest.addressActionType="add"
//                    mViewModel.saveAddressRequest.addressId =""
//                    mViewModel.saveAddressRequest.city =address.locality
//                    mViewModel.saveAddressRequest.country =address.countryName
//                    mViewModel.saveAddressRequest.fullAddress =address.getAddressLine(0)
//                    mViewModel.saveAddressRequest.latitute =address.latitude.toString()
//                    mViewModel.saveAddressRequest.longitute =address.longitude.toString()
//                    mViewModel.saveAddressRequest.postalCode =address.postalCode
//
//                    Log.e("TAG", "getDeviceCurrentLocation: "+ CommonUtils.getPostalCodeByCoordinates(this,address.latitude,address.longitude,requireContext()) )
//
//                    val latLng = LatLng(address.latitude, address.longitude)
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
//                    mMap.addMarker(MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.currentlocationicon)))

                }
            }

        })
    }


}