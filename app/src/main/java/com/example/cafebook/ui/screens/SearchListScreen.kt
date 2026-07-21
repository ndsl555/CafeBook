package com.example.cafebook.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.R
import com.example.cafebook.ViewModels.SearchViewModel
import com.example.cafebook.ui.components.CafeDetailDialog
import com.example.cafebook.ui.components.CafeListItem
import com.example.cafebook.ui.components.GenericFilterDrawerContent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchListScreen(viewModel: SearchViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredCafes by viewModel.filteredCafes.collectAsStateWithLifecycle()

    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val wifiThreshold by viewModel.wifiThreshold.collectAsStateWithLifecycle()
    val seatThreshold by viewModel.seatThreshold.collectAsStateWithLifecycle()
    val quietThreshold by viewModel.quietThreshold.collectAsStateWithLifecycle()
    val tastyThreshold by viewModel.tastyThreshold.collectAsStateWithLifecycle()
    val cheapThreshold by viewModel.cheapThreshold.collectAsStateWithLifecycle()
    val musicThreshold by viewModel.musicThreshold.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedCafe by rememberSaveable { mutableStateOf<CafeShopEntity?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                GenericFilterDrawerContent(
                    cities = uiState.cities,
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
                    onReset = {
                        viewModel.resetFilters()
                        searchQuery = ""
                    },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.search_screen_title)) },
                        actions = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.FilterList, contentDescription = stringResource(R.string.filter_desc))
                            }
                        },
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.setSearchQuery(it)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon_desc)) },
                        singleLine = true,
                    )
                }
            },
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                    onDismiss = { selectedCafe = null },
                    onConfirm = {
                        viewModel.addCafeToPocket(cafe)
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
        }
    }
}
