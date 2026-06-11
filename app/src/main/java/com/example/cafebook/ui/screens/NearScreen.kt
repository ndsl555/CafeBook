package com.example.cafebook.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cafe.NearViewModel
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.R
import com.example.cafebook.ui.components.CafeDetailDialog
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearScreen(viewModel: NearViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val distanceLimit by viewModel.distanceLimit.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedCafe by remember { mutableStateOf<CafeShopEntity?>(null) }
    val mapView = remember { MapView(context) }
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.near_screen_title)) })
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = {
                    mapView.apply {
                        onCreate(Bundle())
                        getMapAsync { map ->
                            googleMap = map
                            map.setOnMarkerClickListener { marker ->
                                (marker.tag as? CafeShopEntity)?.let { selectedCafe = it }
                                true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            Card(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(0.8f),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.9f,
                            ),
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.search_range_label, distanceLimit),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Slider(
                        value = distanceLimit.toFloat(),
                        onValueChange = { viewModel.setDistanceLimit(it.toInt()) },
                        valueRange = 500f..5000f,
                        steps = 8,
                    )
                }
            }

            selectedCafe?.let { cafe ->
                CafeDetailDialog(
                    cafe = cafe,
                    confirmButtonText = stringResource(R.string.navigate_here),
                    onDismiss = { selectedCafe = null },
                    onConfirm = {
                        val gmmIntentUri =
                            "google.navigation:q=${cafe.latitude},${cafe.longitude}".toUri()
                        val mapIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                gmmIntentUri,
                            ).setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    onNavigate = { /* 這裡可以放加入口袋名單的邏輯 */ },
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    LaunchedEffect(uiState.cafes, distanceLimit, googleMap) {
        val map = googleMap ?: return@LaunchedEffect
        map.clear()
        uiState.cafes.forEach { cafe ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(cafe.latitude, cafe.longitude))
                    .title(cafe.name),
            )?.apply { tag = cafe }
        }
    }
}
