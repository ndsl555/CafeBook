package com.example.cafebook.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.R
import com.example.cafebook.ViewModels.PocketViewModel
import com.example.cafebook.ui.components.CafeDetailDialog
import com.example.cafebook.ui.components.CafeListItem
import com.example.cafebook.ui.components.GenericFilterDrawerContent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketScreen(viewModel: PocketViewModel = koinViewModel()) {
    val filteredCafes by viewModel.filteredCafes.collectAsStateWithLifecycle()
    val cityList by viewModel.cityList.collectAsStateWithLifecycle()

    val selectedCity by viewModel.city.collectAsStateWithLifecycle()
    val wifiThreshold by viewModel.wifi.collectAsStateWithLifecycle()
    val seatThreshold by viewModel.seat.collectAsStateWithLifecycle()
    val quietThreshold by viewModel.quiet.collectAsStateWithLifecycle()
    val tastyThreshold by viewModel.tasty.collectAsStateWithLifecycle()
    val cheapThreshold by viewModel.cheap.collectAsStateWithLifecycle()
    val musicThreshold by viewModel.music.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedCafe by rememberSaveable { mutableStateOf<CafeShopEntity?>(null) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getuniCity()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                GenericFilterDrawerContent(
                    cities = cityList,
                    initialCity = selectedCity,
                    initialWifi = wifiThreshold,
                    initialSeat = seatThreshold,
                    initialQuiet = quietThreshold,
                    initialTasty = tastyThreshold,
                    initialCheap = cheapThreshold,
                    initialMusic = musicThreshold,
                    onApply = { city, wifi, seat, quiet, tasty, cheap, music ->
                        viewModel.updateFilters(city, wifi, seat, quiet, tasty, cheap, music)
                        scope.launch { drawerState.close() }
                    },
                    onReset = { viewModel.clearFilters() },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.pocket_screen_title)) },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.FilterList, contentDescription = stringResource(R.string.filter_desc))
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    showDeleteConfirm = true
                }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_all_desc))
                }
            },
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (filteredCafes.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_saved_cafes),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredCafes) { cafe ->
                        CafeListItem(cafe = cafe, onClick = { selectedCafe = cafe })
                        HorizontalDivider()
                    }
                }
            }

            selectedCafe?.let { cafe ->
                CafeDetailDialog(
                    cafe = cafe,
                    confirmButtonText = stringResource(R.string.remove_from_pocket),
                    onDismiss = { selectedCafe = null },
                    onConfirm = {
                        viewModel.deleteCafeShop(cafe.name)
                        selectedCafe = null
                    },
                    onNavigate = {
                        val gmmIntentUri =
                            "geo:${cafe.latitude},${cafe.longitude}?q=${Uri.encode(cafe.name)}".toUri()
                        val mapIntent =
                            Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, context.getString(R.string.cannot_open_maps), Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text(stringResource(R.string.delete_all_title)) },
                    text = { Text(stringResource(R.string.delete_all_confirm_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteAllCafe()
                            showDeleteConfirm = false
                        }) {
                            Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text(stringResource(R.string.no))
                        }
                    },
                )
            }
        }
    }
}
