package com.example.cafebook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cafe.NearViewModel
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Extension.launchAndRepeatWithViewLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NearFragment : Fragment() {
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var distanceSeekBar: SeekBar

    private val viewModel: NearViewModel by viewModel()

    private var distanceLimit = 1000 // 公尺範圍
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var currentLatLng: LatLng = LatLng(0.0, 0.0)
    private var cafes: List<CafeShopEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_near, container, false)

        distanceSeekBar = view.findViewById(R.id.distanceSeekBar)
        progressBar = view.findViewById(R.id.progressBarNear)
        mapView = view.findViewById(R.id.mapViewNear)
        mapView.onCreate(savedInstanceState)

        val drawable = progressBar.indeterminateDrawable
        drawable.setColorFilter(resources.getColor(R.color.ave_color), PorterDuff.Mode.SRC_IN)
        progressBar.indeterminateDrawable = drawable

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        setupSeekBars()
        observeViewModel()

        mapView.getMapAsync { map ->
            googleMap = map
            requestLocationPermission()
        }

        return view
    }

    private fun setupSeekBars() {
        distanceSeekBar.setOnSeekBarChangeListener(seekBarListener {viewModel.setDistanceLimit(it)})
    }

    private fun seekBarListener(onChange: (Int) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                onChange(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }
    private fun observeViewModel() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.uiState.collect { state ->
                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }

                    cafes = state.cafes
                    addMarkersToMap()
                }
            }
            launch {
                viewModel.distanceLimit.collect {
                    distanceLimit = it
                    addMarkersToMap()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            showCurrentLocation()
        }
    }

    private fun showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
                    addCurrentLocationMarker()
                    addMarkersToMap()
                } else {
                    Toast.makeText(requireContext(), "無法獲取目前位置", Toast.LENGTH_SHORT).show()
                }

                viewModel.fetchCafeData()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "定位失敗: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addCurrentLocationMarker() {
        currentLatLng.let {
            val vectorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_emoji_people_24)
            val icon = getMarkerIconFromDrawable(vectorDrawable)
            val options = MarkerOptions().position(it).icon(icon).title("您的位置")
            googleMap?.addMarker(options)
        }
    }

    private fun addMarkersToMap() {
        val map = googleMap ?: return
        map.clear()
        addCurrentLocationMarker()

        val center = currentLatLng ?: return

        cafes.forEach { cafe ->
            val lat = cafe.latitude
            val lng = cafe.longitude
            val cafeLatLng = LatLng(lat, lng)
            val results = FloatArray(1)
            Location.distanceBetween(center.latitude, center.longitude, lat, lng, results)

            if (results[0] <= distanceLimit) {
                val markerOptions =
                    MarkerOptions()
                        .position(cafeLatLng)
                        .title(cafe.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                val marker = map.addMarker(markerOptions)
                marker?.tag = cafe
            }
        }

        // 設定 InfoWindow adapter
        map.setInfoWindowAdapter(
            object : GoogleMap.InfoWindowAdapter {
                override fun getInfoContents(marker: Marker): View? {
                    val view = layoutInflater.inflate(R.layout.marker_info_window, null)
                    val title = view.findViewById<TextView>(R.id.tvCafeName)
                    val cafe = marker.tag as? CafeShopEntity
                    title.text = "店名：${cafe?.name ?: "未知"}"
                    return view
                }

                override fun getInfoWindow(p0: Marker): View? = null
            },
        )

        // 點擊 InfoWindow 啟動導航
        map.setOnInfoWindowClickListener { marker ->
            val position = marker.position
            if (position != currentLatLng) {
                startNavigation(position.latitude, position.longitude)
            }
        }

        // 顯示附近範圍圓圈
        map.addCircle(
            CircleOptions()
                .center(center)
                .radius(distanceLimit.toDouble())
                .strokeWidth(0f)
                .fillColor(0x55FFFF00),
        )
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable?): BitmapDescriptor? {
        drawable ?: return null
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun startNavigation(
        latitude: Double,
        longitude: Double,
    ) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(requireContext(), "未找到地圖應用程式", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "需要位置權限以顯示地圖", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
