package com.example.cafe.ViewModels

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.*
import com.example.cafebook.Entity.BarEntity
import com.example.cafebook.UseCase.AddBarCodeUseCase
import com.example.cafebook.UseCase.LoadBarCodeUseCase
import com.example.cafebook.Utils.Result
import com.example.cafebook.Utils.invoke
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code39Writer
import kotlinx.coroutines.launch

class BarcodeViewModel(
    private val addBarCodeUseCase: AddBarCodeUseCase,
    private val loadBarCodeUseCase: LoadBarCodeUseCase,
) : ViewModel() {
    private val _barcodeBitmap = MutableLiveData<Bitmap>()
    val barcodeBitmap: LiveData<Bitmap> = _barcodeBitmap

    private val _barcodeText = MutableLiveData<String>()
    val barcodeText: LiveData<String> = _barcodeText

    fun loadLatestBarcode() {
        viewModelScope.launch {
            when (val loadResult = loadBarCodeUseCase.invoke()) {
                is Result.Success -> {
                    loadResult.let {
                        val text = "/" + it.data.barCodeData // 改用 barCodeData
                        val bitmap = generateBarcodeBitmap(text)
                        _barcodeText.value = text
                        _barcodeBitmap.value = bitmap
                    }
                    // 成功
                }

                is Result.Error -> {
                    // 失敗
                }
            }
        }
    }

    fun saveBarcode(input: String) {
        var cleanedInput = input.trim()
        if (cleanedInput.startsWith("/")) {
            cleanedInput = cleanedInput.substring(1)
        }
        cleanedInput = cleanedInput.uppercase()

        val bar = BarEntity(id = 1, barCodeData = cleanedInput) // 固定 id = 1

        viewModelScope.launch {
            when (val result = addBarCodeUseCase(AddBarCodeUseCase.Parameters(bar))) {
                is Result.Success -> {
                    // 成功後再載入更新 UI
                    loadLatestBarcode()
                }

                is Result.Error -> {
                    // 可以加入錯誤提示或 Log
                }
            }
        }
    }

    // 生成 barcode bitmap 的方法保持不變
    private fun generateBarcodeBitmap(data: String): Bitmap {
        val writer = Code39Writer()
        val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.CODE_39, 700, 200)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.parseColor("#754C00")

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (bitMatrix[x, y]) {
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                }
            }
        }

        return bitmap
    }
}
