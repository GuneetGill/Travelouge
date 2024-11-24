package com.example.travelogue.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.travelogue.R
import com.example.travelogue.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class AddCountryFragment : Fragment(R.layout.fragment_add_country), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private var latlng: LatLng? = null
    private lateinit var mMap: GoogleMap
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var autocompleteEditText: EditText? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBrK3kjdBkJBiZRiQBvXVc_z_d2To3EU5w")
        }

        // Add the AutocompleteSupportFragment dynamically
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Configure the AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setHint("Search Location")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Handle the selected place
                Log.i("Place", "Place: ${place.name}, ${place.latLng}")
                latlng = place.latLng
                createPin(place.latLng)
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                // Handle the error
                Log.e("Place", "An error occurred: $status")
            }
        })

        // edit text for location input
        autocompleteEditText = autocompleteFragment.view?.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)

        // Set a TextWatcher to listen for when text is cleared
        autocompleteEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Detect if the text is cleared
                if (s.isNullOrEmpty()) {
                    // Handle the clear button press event
                    mMap.clear()
                    latlng = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // init map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // onclick for save
        view.findViewById<Button>(R.id.saveBtn).setOnClickListener {
            if (latlng != null) {
                // SAVE LATLNG TO DATABASE

                // Go back to countries list / home page
                findNavController().navigate(R.id.navigation_home)
            } else {
                // somehow prompt user to enter input
                autocompleteFragment.setText("")
                autocompleteEditText?.performClick()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
        mMap.setMapStyle(style)
        mMap.setOnMapClickListener(this)

        // set on click for POI
        mMap.setOnPoiClickListener { poi ->
            latlng = poi.latLng
            createPin(poi.latLng)
            autocompleteFragment.setText(poi.name)
        }
    }

    // Map click
    override fun onMapClick(coords: LatLng) {
        latlng = coords
        createPin(coords)
        val country = Util.getCountryName(requireContext(), coords)
        if (country != null) {
            autocompleteFragment.setText(country)
        }
        else {
            autocompleteFragment.setText("Invalid Location")
            latlng = null
        }
    }

    // put pin on the map given latlng
    private fun createPin(coords: LatLng) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions()
                .position(coords)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        val currentZoom = mMap.cameraPosition.zoom
        if (currentZoom <= 4f) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 4f))
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, currentZoom))
        }
    }

}