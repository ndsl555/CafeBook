package com.example.cafebook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.R
import kotlin.math.roundToInt

@Composable
fun CafeListItem(
    cafe: CafeShopEntity,
    onClick: () -> Unit,
) {
    val displayName =
        if (cafe.name.length > 12) {
            cafe.name.substring(0, 13) + "\n" + cafe.name.substring(13)
        } else {
            cafe.name
        }

    val average = listOf(cafe.wifi, cafe.seat, cafe.quiet, cafe.tasty, cafe.cheap, cafe.music).average()

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = displayName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(top = 4.dp)) {
                if (cafe.mrt.isNotBlank()) {
                    Badge(containerColor = Color.Blue, contentColor = Color.White, modifier = Modifier.padding(end = 4.dp)) {
                        Text(stringResource(R.string.mrt))
                    }
                }
                if (cafe.limitedTime == "yes") {
                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                        Text(stringResource(R.string.limited_time))
                    }
                }
            }
        }
        Text(
            text = String.format("%.1f", average),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericFilterDrawerContent(
    cities: List<String>,
    initialCity: String? = null,
    initialWifi: Int = 0,
    initialSeat: Int = 0,
    initialQuiet: Int = 0,
    initialTasty: Int = 0,
    initialCheap: Int = 0,
    initialMusic: Int = 0,
    onApply: (city: String?, wifi: Int, seat: Int, quiet: Int, tasty: Int, cheap: Int, music: Int) -> Unit,
    onReset: () -> Unit,
) {
    val cityNameMap =
        mapOf(
            "taipei" to "台北",
            "newtaipei" to "新北",
            "taoyuan" to "桃園",
            "hsinchu" to "新竹",
            "miaoli" to "苗栗",
            "taichung" to "台中",
            "changhua" to "彰化",
            "nantou" to "南投",
            "yunlin" to "雲林",
            "chiayi" to "嘉義",
            "tainan" to "台南",
            "kaohsiung" to "高雄",
            "pingtung" to "屏東",
            "yilan" to "宜蘭",
            "hualien" to "花蓮",
            "taitung" to "台東",
            "keelung" to "基隆",
            "penghu" to "澎湖",
            "kinmen" to "金門",
            "lienchiang" to "連江",
        )

    var expanded by rememberSaveable { mutableStateOf(false) }
    val allLabel = stringResource(R.string.all)

    // 內部狀態，只在按下「應用」時才回傳
    var selectedCityCode by rememberSaveable { mutableStateOf(initialCity) }
    var selectedCityName by rememberSaveable {
        mutableStateOf(if (initialCity == null) allLabel else (cityNameMap[initialCity] ?: initialCity))
    }

    var wifi by rememberSaveable { mutableFloatStateOf(initialWifi.toFloat()) }
    var seat by rememberSaveable { mutableFloatStateOf(initialSeat.toFloat()) }
    var quiet by rememberSaveable { mutableFloatStateOf(initialQuiet.toFloat()) }
    var tasty by rememberSaveable { mutableFloatStateOf(initialTasty.toFloat()) }
    var cheap by rememberSaveable { mutableFloatStateOf(initialCheap.toFloat()) }
    var music by rememberSaveable { mutableFloatStateOf(initialMusic.toFloat()) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Text(stringResource(R.string.filter_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.city))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedCityName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(allLabel) },
                    onClick = {
                        selectedCityName = allLabel
                        selectedCityCode = null
                        expanded = false
                    },
                )
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(cityNameMap[city] ?: city) },
                        onClick = {
                            selectedCityName = cityNameMap[city] ?: city
                            selectedCityCode = city
                            expanded = false
                        },
                    )
                }
            }
        }

        RatingFilterSlider(stringResource(R.string.wifi), wifi) { wifi = it }
        RatingFilterSlider(stringResource(R.string.seat), seat) { seat = it }
        RatingFilterSlider(stringResource(R.string.quiet), quiet) { quiet = it }
        RatingFilterSlider(stringResource(R.string.tasty), tasty) { tasty = it }
        RatingFilterSlider(stringResource(R.string.cheap), cheap) { cheap = it }
        RatingFilterSlider(stringResource(R.string.music), music) { music = it }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    onApply(
                        selectedCityCode,
                        wifi.roundToInt(),
                        seat.roundToInt(),
                        quiet.roundToInt(),
                        tasty.roundToInt(),
                        cheap.roundToInt(),
                        music.roundToInt(),
                    )
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(R.string.apply))
            }
            OutlinedButton(onClick = {
                onReset()
                selectedCityName = allLabel
                selectedCityCode = null
                wifi = 0f
                seat = 0f
                quiet = 0f
                tasty = 0f
                cheap = 0f
                music = 0f
            }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.clear)) }
        }
    }
}

@Composable
fun RatingFilterSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text("$label: ${value.roundToInt()}")
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..5f,
            steps = 4,
        )
    }
}

@Composable
fun CafeDetailDialog(
    cafe: CafeShopEntity,
    confirmButtonText: String = stringResource(R.string.add_to_pocket),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onNavigate: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(cafe.name) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                RatingRow(stringResource(R.string.wifi), cafe.wifi)
                RatingRow(stringResource(R.string.seat), cafe.seat)
                RatingRow(stringResource(R.string.quiet), cafe.quiet)
                RatingRow(stringResource(R.string.tasty), cafe.tasty)
                RatingRow(stringResource(R.string.cheap), cafe.cheap)
                RatingRow(stringResource(R.string.music), cafe.music)

                Spacer(modifier = Modifier.height(8.dp))
                DetailText(stringResource(R.string.address), cafe.address)
                DetailText(stringResource(R.string.limited_time), cafe.limitedTime)
                DetailText(stringResource(R.string.socket), cafe.socket)
                DetailText(stringResource(R.string.standing_desk), cafe.standingDesk)
                DetailText(stringResource(R.string.mrt), cafe.mrt)
                DetailText(stringResource(R.string.open_time), cafe.openTime)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmButtonText) }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onNavigate) { Text(stringResource(R.string.navigate)) }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.confirm)) }
            }
        },
    )
}

@Composable
fun RatingRow(
    label: String,
    rating: Float,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", modifier = Modifier.width(60.dp))
        repeat(5) { index ->
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating.roundToInt()) Color(0xFFFFB400) else Color.LightGray,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun DetailText(
    label: String,
    value: String,
) {
    if (value.isNotBlank()) {
        Text(text = "$label: $value", fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp))
    }
}
