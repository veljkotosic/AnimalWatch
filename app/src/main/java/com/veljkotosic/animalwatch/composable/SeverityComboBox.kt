package com.veljkotosic.animalwatch.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.ui.theme.Primary
import com.veljkotosic.animalwatch.uistate.map.WatchMarkerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeverityComboBox(
    uiState: WatchMarkerUiState,
    onSelectionChanged: (WatchMarkerSeverity) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = uiState.severity.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Severity") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Background)
        ) {
            WatchMarkerSeverity.entries.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag.name, color = Primary) },
                    onClick = {
                        onSelectionChanged(tag)
                        expanded = false
                    },
                    modifier = Modifier.background(Background)
                )
            }
        }
    }
}