package com.example.cafebook

sealed class Screen(val route: String) {
    object SearchList : Screen("search_list")

    object Barcode : Screen("barcode")

    object Pocket : Screen("pocket")

    object Near : Screen("near")
}
