package com.weatherapp.weatherapp.ui.home

import android.Manifest
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
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.weatherapp.weatherapp.api.Constants.Companion.DATE_PATTERN
import com.weatherapp.weatherapp.api.Constants.Companion.HOUR_PATTERN
import com.weatherapp.weatherapp.api.Constants.Companion.HUMIDITY_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.PRESSURE_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.TEMPERATURE_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.URL_TO_GET_ICON
import com.weatherapp.weatherapp.api.Constants.Companion.WEATHER_ICON_EXTENSION
import com.weatherapp.weatherapp.api.Constants.Companion.WIND_SPEED_UNIT
import com.weatherapp.weatherapp.api.Constants.Companion.ZONE_DELIMITER
import com.weatherapp.weatherapp.api.model.weather.WeatherResponse
import com.weatherapp.weatherapp.databinding.FragmentHomeBinding
import com.weatherapp.weatherapp.repository.implementation.WeatherRepository
import com.weatherapp.weatherapp.ui.home.adapter.WeatherAdapter
import com.weatherapp.weatherapp.ui.home.model.Parameter
import com.weatherapp.weatherapp.ui.home.viewmodel.HomeViewModel
import com.weatherapp.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }

    private var _binding: FragmentHomeBinding? = null

    private var parametersForGetWeather: Parameter = Parameter()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var lastKnownLocation: Location? = null

    private val homeVM by viewModels<HomeViewModel> {
        HomeViewModelFactory(WeatherRepository())
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getDeviceLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWeeklyWeatherRecyclerView()
        setUpWeather(view)
    }

    private fun initWeeklyWeatherRecyclerView(){
        binding.rvTimeOfTheWeek.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setUpWeather(view: View) {
        homeVM.fetchWeather.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val weather = response.body() as WeatherResponse
                binding.rvTimeOfTheWeek.adapter = WeatherAdapter(weather.weeklyWeather)
                updateUI(weather)
            } else {
                Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).setAction("OK") {}.show()
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        val zone = weather.timeZone.split(ZONE_DELIMITER)
        val today = weather.currentWeather.dateTime
        val sunrise = weather.currentWeather.sunrise
        val sunset = weather.currentWeather.sunset
        val humidity = weather.currentWeather.humidity
        val windSpeed = weather.currentWeather.windSpeed
        val pressure = weather.currentWeather.pressure
        val weatherIconCode = weather.currentWeather.weatherDescription[0].icon
        val imageUrl = "$URL_TO_GET_ICON$weatherIconCode$WEATHER_ICON_EXTENSION"
        binding.tvDate.text =
            SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH).format(Date(today * 1000))
        binding.tvCity.text = zone.last()
        binding.tvTemperature.text = "${weather.currentWeather.temperature.toInt()} $TEMPERATURE_UNIT"
        binding.tvSunriseTime.text =
            SimpleDateFormat(HOUR_PATTERN, Locale.ENGLISH).format(Date(sunrise * 1000))
        binding.tvSunsetTime.text =
            SimpleDateFormat(HOUR_PATTERN, Locale.ENGLISH).format(Date(sunset * 1000))
        binding.tvHumidityData.text = "$humidity $HUMIDITY_UNIT"
        binding.tvWindData.text = "$windSpeed $WIND_SPEED_UNIT"
        binding.tvPressureData.text = "$pressure $PRESSURE_UNIT"
        Picasso.get().load(imageUrl).into(binding.ivWeatherIcon)
    }

    private fun getDeviceLocation() {
        if (hasLocationPermission()) {
            try {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            parametersForGetWeather.longitude = lastKnownLocation!!.longitude.toFloat()
                            parametersForGetWeather.latitude = lastKnownLocation!!.latitude.toFloat()
                            homeVM.setParameters(parametersForGetWeather)
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }
        else{
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        getDeviceLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}