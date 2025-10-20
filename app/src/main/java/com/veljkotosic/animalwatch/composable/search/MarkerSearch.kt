package com.veljkotosic.animalwatch.composable.search

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.veljkotosic.animalwatch.ui.theme.Primary
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("MissingPermission")
fun MarkerSearch(
    mapViewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val searchParametersUiState by mapViewModel.searchParametersUiState.collectAsState()

    var radiusString by remember { mutableStateOf(searchParametersUiState.radiusMeters.toString()) }
    var parsedRadius by remember { mutableStateOf<Double?>(searchParametersUiState.radiusMeters) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text("Search parameters", color = Primary)
        },

        text = {
            Column {
                OutlinedTextField(
                    value = radiusString,
                    onValueChange = {
                        radiusString = it
                        parsedRadius = radiusString.toDoubleOrNull()
                    },
                    label = { Text("Radius (meters)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                if (parsedRadius == null) {
                    Text("Entered value is not a decimal number")
                } else {
                    if (!mapViewModel.searchRadiusMetersInRange(parsedRadius!!)) {
                        Text("Radius should be between ${searchParametersUiState.radiusMetersLowerBound} and ${searchParametersUiState.radiusMetersUpperBound} meters")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    mapViewModel.onSearchRadiusMetersChanged(radiusString.toDouble())
                    mapViewModel.forceRefreshMarkers()
                    onDismiss()
                },
                enabled = (parsedRadius != null && mapViewModel.searchRadiusMetersInRange(parsedRadius!!))
            ) {
                Text("Refresh")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
                    mapViewModel.resetSearchParameters()
                    mapViewModel.forceRefreshMarkers()
                    onDismiss()
                }) {
                    Text("Reset")
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}