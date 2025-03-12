package com.kotlin.biteback.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.login.LoginViewModel

@Composable
fun Login(navController: NavController , viewModel: LoginViewModel = viewModel<LoginViewModel>()) {
    val message by viewModel.message.collectAsState()
}