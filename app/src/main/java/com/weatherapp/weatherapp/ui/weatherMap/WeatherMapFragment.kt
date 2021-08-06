package com.weatherapp.weatherapp.ui.weatherMap

import android.content.ContentValues.TAG
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.weatherapp.weatherapp.R
import com.weatherapp.weatherapp.api.Constants.Companion.DEFAULT_LATITUDE
import com.weatherapp.weatherapp.api.Constants.Companion.DEFAULT_LONGITUDE
import com.weatherapp.weatherapp.api.Constants.Companion.DEFAULT_ZOOM_FOR_MAP
import com.weatherapp.weatherapp.api.Constants.Companion.HOURS_IN_A_DAY
import com.weatherapp.weatherapp.api.Constants.Companion.TEMPERATURE_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.URL_TO_GET_ICON
import com.weatherapp.weatherapp.api.Constants.Companion.WEATHER_ICON_EXTENSION
import com.weatherapp.weatherapp.api.Constants.Companion.ZONE_DELIMITER
import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import com.weatherapp.weatherapp.databinding.FragmentWeatherMapBinding
import com.weatherapp.weatherapp.repository.implementation.WeatherRepository
import com.weatherapp.weatherapp.ui.home.model.Parameter
import com.weatherapp.weatherapp.ui.weatherMap.adapter.WeatherMapAdapter
import com.weatherapp.weatherapp.ui.weatherMap.viewmodel.WeatherMapViewModel
import com.weatherapp.weatherapp.ui.weatherMap.viewmodel.WeatherMapViewModelFactory

class WeatherMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private var _binding: FragmentWeatherMapBinding? = null

    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private var lastKnownLocation: Location? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val defaultLocation = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)

    private var parametersForGetWeather: Parameter = Parameter()

    private val weatherMapVM by viewModels<WeatherMapViewModel> {
        WeatherMapViewModelFactory(WeatherRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherMapBinding.inflate(inflater, container, false)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val mMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.map, mMapFragment).commit()
        mMapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setOnMapClickListener(this)
        initHourlyWeatherRecyclerView()
        updateLocationUI()
        getDeviceLocation()
        setUpWeather(binding.root)
    }

    private fun initHourlyWeatherRecyclerView(){
        binding.rvHourlyWeather.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM_FOR_MAP.toFloat()
                            )
                        )
                        sendNewLocation(lastKnownLocation!!.latitude.toFloat(),
                            lastKnownLocation!!.longitude.toFloat())
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    map?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM_FOR_MAP.toFloat())
                    )
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setUpWeather(view: View) {
        weatherMapVM.fetchWeather.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val weather = response.body() as WeatherResponse
                binding.rvHourlyWeather.adapter = WeatherMapAdapter(weather.hourlyWeather.take(HOURS_IN_A_DAY).toTypedArray())
                updateUI(weather)
            } else {
                Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).setAction("OK") {}.show()
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        val zone = weather.timeZone.split(ZONE_DELIMITER)
        val icon = weather.currentWeather.weatherDescription[0].icon
        val imageUrl = "$URL_TO_GET_ICON$icon$WEATHER_ICON_EXTENSION"
        binding.tvLocation.text = zone.last()
        binding.tvTemp.text = "${weather.currentWeather.temperature.toInt()} $TEMPERATURE_UNIT"
        binding.tvDescription.text = weather.currentWeather.weatherDescription[0].detailedDescription.uppercase()
        Picasso.get().load(imageUrl).into(binding.ivIconWeather)
    }

    override fun onMapClick(point: LatLng) {
        sendNewLocation(point.latitude.toFloat(), point.longitude.toFloat())
        addMarker(point)
    }

    private fun sendNewLocation(latitude: Float, longitude: Float){
        parametersForGetWeather.longitude = longitude
        parametersForGetWeather.latitude = latitude
        weatherMapVM.setParameters(parametersForGetWeather)
    }

    private fun addMarker(point: LatLng){
        map!!.clear()
        map!!.addMarker(
            MarkerOptions()
                .position(point)
        )
        map!!.moveCamera(CameraUpdateFactory.newLatLng(point))
    }
}