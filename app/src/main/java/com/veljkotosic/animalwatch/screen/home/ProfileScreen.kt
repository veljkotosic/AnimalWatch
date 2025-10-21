package com.veljkotosic.animalwatch.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.ui.theme.AnimalWatchTheme
import com.veljkotosic.animalwatch.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    onSignOut: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        Text(
            "Home screen!",
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineMedium
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
                profileViewModel.signOut()
                onSignOut()
            }
        ) {
            Text("Sign out.")
        }
    }
}