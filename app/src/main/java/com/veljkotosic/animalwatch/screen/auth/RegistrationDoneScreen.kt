package com.veljkotosic.animalwatch.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.veljkotosic.animalwatch.composable.logo.Logo
import com.veljkotosic.animalwatch.screen.Screens

@Composable
fun RegistrationDoneScreen(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
            .background(color = Color(0xffeff5e9))
    ) {
        Logo(
            modifier = Modifier.size(280.dp).align(Alignment.CenterHorizontally)
        )

        TextButton(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            onClick = { navController.popBackStack(Screens.Login.route, false) }
        ) {
            Text("Registration successful! \n Click here to go back to Login.")
        }
    }
}