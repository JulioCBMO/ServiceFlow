package com.example.serviceflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.serviceflow.model.OrdemServico
import com.example.serviceflow.model.TipoUsuario
import com.example.serviceflow.ui.screens.*
import com.example.serviceflow.ui.theme.ServiceFlowTheme
import com.example.serviceflow.viewmodel.AdminViewModel
import com.example.serviceflow.viewmodel.AuthViewModel
import com.example.serviceflow.viewmodel.FuncionarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServiceFlowTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ServiceFlowApp()
                }
            }
        }
    }
}

@Composable
fun ServiceFlowApp() {
    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    var tipoUsuario by remember { mutableStateOf<TipoUsuario?>(null) }
    var selectedOS by remember { mutableStateOf<OrdemServico?>(null) }

    if (tipoUsuario == null && currentUser == null) {
        LoginScreen(viewModel = authViewModel, onLoginSuccess = { tipo -> tipoUsuario = tipo })
    } else {
        if (selectedOS != null) {
            DetalheOSScreen(
                os = selectedOS!!,
                isAdmin = tipoUsuario == TipoUsuario.ADMIN,
                viewModel = if (tipoUsuario == TipoUsuario.FUNCIONARIO) viewModel<FuncionarioViewModel>() else null,
                onBack = { selectedOS = null }
            )
        } else {
            when (tipoUsuario) {
                TipoUsuario.ADMIN -> {
                    val adminViewModel: AdminViewModel = viewModel()
                    AdminDashboardScreen(
                        currentUser = currentUser!!,
                        viewModel = adminViewModel,
                        onOSClick = { os -> selectedOS = os },
                        onLogout = { authViewModel.logout(); tipoUsuario = null }
                    )
                }
                TipoUsuario.FUNCIONARIO -> {
                    val funcViewModel: FuncionarioViewModel = viewModel()
                    FuncionarioDashboardScreen(
                        currentUser = currentUser!!,
                        viewModel = funcViewModel,
                        onOSClick = { os -> selectedOS = os },
                        onLogout = { authViewModel.logout(); tipoUsuario = null }
                    )
                }
                null -> {}
            }
        }
    }
}
