@file:JvmName("ProfileScreenKt")

package com.veljkotosic.animalwatch.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.veljkotosic.animalwatch.screen.Screens

@Composable
fun ProfileScreen(
    navController: NavController,
    onSignOut: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        Text(
            "Home screen!",
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )

        TextButton(
            onClick = {
                navController.navigate(Screens.Map.route)
            }
        ) {
            Text("Open map.")
        }

        TextButton(
            onClick = {
                //TODO: Actual signOut via authRepository
                onSignOut()
            }
        ) {
            Text("Sign out.")
        }
    }
}