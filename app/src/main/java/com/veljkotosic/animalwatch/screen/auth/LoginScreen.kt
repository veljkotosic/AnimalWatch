package com.veljkotosic.animalwatch.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.component.Logo
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val loginUiState by authViewModel.loginUiState.collectAsState()
    val userState by userViewModel.userState.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(loginUiState.processing.isSuccess) {
        if (loginUiState.processing.isSuccess) {
            userViewModel.getUser(authViewModel.getCurrentUserId()!!)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Color(0xffeff5e9))
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ){
            Logo(
                modifier = Modifier.size(280.dp).align(Alignment.CenterHorizontally)
            )

            Text(
                "Login",
                style = MaterialTheme.typography.headlineMedium,
            )

            OutlinedTextField(
                value = loginUiState.email,
                onValueChange = { authViewModel.onLoginEmailChanged(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
            )
            OutlinedTextField(
                value = loginUiState.password,
                onValueChange = { authViewModel.onLoginPasswordChanged(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            loginUiState.processing.errorMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Button(
                onClick = {
                    if (loginUiState.email.isBlank()) {
                        authViewModel.setLoginError("Email field is empty.")
                        return@Button
                    }
                    if (loginUiState.password.isBlank()) {
                        authViewModel.setLoginError("Password field is empty.")
                        return@Button
                    }

                    if (!loginUiState.processing.isLoading) {
                        authViewModel.login()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                if (loginUiState.processing.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Gray)
                } else {
                    Text("Login")
                }
            }

            TextButton(onClick = { navController.navigate(Screens.Register.route) }) {
                Text("Don't have an account? Register here!")
            }
        }
    }
}
