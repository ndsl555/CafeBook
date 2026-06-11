package com.example.cafebook
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cafebook.Utils.NetworkUtils
import com.example.cafebook.ui.screens.BarcodeScreen
import com.example.cafebook.ui.screens.NearScreen
import com.example.cafebook.ui.screens.PocketScreen
import com.example.cafebook.ui.screens.SearchListScreen
import com.example.cafebook.ui.theme.CafeBookTheme

class MainActivity :
    AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CafeBookTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val isNetworkConnected by NetworkUtils.networkState.collectAsStateWithLifecycle()

    if (!isNetworkConnected) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(android.R.string.dialog_alert_title)) },
            text = { Text(stringResource(R.string.network_type_no_network)) },
            confirmButton = {
                TextButton(onClick = {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                    navController.context.startActivity(intent)
                }) { Text(stringResource(R.string.yes)) }
            },
        )
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                val items =
                    listOf(
                        Triple(Screen.SearchList, "店家搜搜", Icons.Default.Search),
                        Triple(Screen.Near, "鄰近店家", Icons.Default.MyLocation),
                        Triple(Screen.Barcode, "條碼載具", Icons.Default.QrCodeScanner),
                        Triple(Screen.Pocket, "口袋名單", Icons.Default.Star),
                    )

                items.forEach { (screen, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = null) },
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        label = { Text(label) }, // Used 'label' directly since it's a String
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) {
            innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SearchList.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.SearchList.route) { SearchListScreen() }
            composable(Screen.Near.route) { NearScreen() }
            composable(Screen.Barcode.route) { BarcodeScreen() }
            composable(Screen.Pocket.route) { PocketScreen() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    CafeBookTheme {
        MainApp()
    }
}
