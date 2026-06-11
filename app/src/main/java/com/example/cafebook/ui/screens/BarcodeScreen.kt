package com.example.cafebook.ui.screens
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.cafe.ViewModels.BarcodeViewModel
import com.example.cafebook.R
import org.koin.androidx.compose.koinViewModel
import kotlin.text.uppercase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScreen() {
    // 使用 Scaffold 包裹整個頁面以加入 TopAppBar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.barcode_fragment_title)) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // 確保內容不被標頭遮住
        ) {
            BarcodeContent()
        }
    }
}

@Composable
fun BarcodeContent(viewModel: BarcodeViewModel = koinViewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity

    val barcodeBitmap by viewModel.barcodeBitmap.observeAsState()
    val barcodeText by viewModel.barcodeText.observeAsState("")
    var showInputDialog by remember { mutableStateOf(false) }
    var isLightOn by remember { mutableStateOf(false) }

    LaunchedEffect(isLightOn) {
        activity?.window?.let { window ->
            val layoutParams = window.attributes
            layoutParams.screenBrightness = if (isLightOn) 1.0f else -1.0f
            window.attributes = layoutParams
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.window?.let { window ->
                val layoutParams = window.attributes
                layoutParams.screenBrightness = -1.0f
                window.attributes = layoutParams
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLatestBarcode()
    }

    if (showInputDialog) {
        BarcodeInputDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { text ->
                viewModel.saveBarcode(text)
                showInputDialog = false
            },
        )
    }

    // 注意：這裡的 Scaffold 只負責 FAB，不再包含 TopAppBar
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showInputDialog = true }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_barcode_desc),
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            barcodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(R.string.barcode_image_desc),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = barcodeText,
                style = MaterialTheme.typography.headlineMedium,
                modifier =
                    Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("barcode", barcodeText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            context,
                            context.getString(R.string.copied_to_clipboard, barcodeText),
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isLightOn = !isLightOn },
            ) {
                Text(stringResource(R.string.adjust_the_brightness))
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isLightOn,
                    onCheckedChange = { isLightOn = it },
                )
            }
        }
    }
}

@Composable
fun BarcodeInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.enter_barcode_hint)) },
        text = {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    var newText = newValue.text.uppercase()
                    var newSelection = newValue.selection

                    // 如果輸入不為空，且第一個字元不是 '/'，則自動補上 '/'
                    if (newText.isNotEmpty() && !newText.startsWith("/")) {
                        newText = "/$newText"
                        // 將游標向後位移一格，補償增加的 '/'
                        newSelection = TextRange(newSelection.start + 1)
                    }

                    textFieldValue =
                        newValue.copy(
                            text = newText,
                            selection = newSelection,
                        )
                },
                label = { Text(stringResource(R.string.barcode_fragment_title)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val finalBarcode = textFieldValue.text.trim()
                if (isBarcodeValid(finalBarcode)) onConfirm(finalBarcode)
            }) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.no))
            }
        },
    )
}

fun isBarcodeValid(barcode: String): Boolean {
    // 正則表達式：第一位是斜線，後面接 7 位英數字或特定符號(.-+)
    val regex = Regex("^/[0-9A-Z.+-]{7}$")
    return regex.matches(barcode)
}
