package com.kotlin.biteback.ui.productDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun Register(navController: NavController , viewModel: ProductDetailViewModel = viewModel<ProductDetailViewModel>()) {
    val message by viewModel.message.collectAsState()
}