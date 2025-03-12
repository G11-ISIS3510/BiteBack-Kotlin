package com.kotlin.biteback.ui.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.register.RegisterViewModel

@Composable
fun Register(navController: NavController , viewModel: RegisterViewModel = viewModel<RegisterViewModel>()) {
    val message by viewModel.message.collectAsState()
}