package com.veljkotosic.animalwatch.composable.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.veljkotosic.animalwatch.composable.tag.TagSelector
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.ui.theme.Primary
import com.veljkotosic.animalwatch.utility.toDateString
import com.veljkotosic.animalwatch.viewmodel.map.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSelector(
    mapViewModel: MapViewModel,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTagSelector by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    val filterUiState by mapViewModel.filterUiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Background,
        title = {
            Text("Filter Markers", color = Primary)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Severity:", color = Primary)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    WatchMarkerSeverity.entries.filter { it != WatchMarkerSeverity.Undefined }.forEach { severity ->
                        val selected = filterUiState.severities.contains(severity)
                        TextButton(
                            onClick = {
                                if (selected) mapViewModel.removeSeverityFilter(severity)
                                else mapViewModel.addSeverityFilter(severity)
                            },
                            modifier = Modifier
                                .border(
                                    BorderStroke(2.dp, if (selected) Primary else Color.Gray),
                                    RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (selected) Primary.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text = severity.name,
                                color = if (selected) Primary else Color.Gray
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = filterUiState.owner,
                    onValueChange = { mapViewModel.changeOwnerFilter(it) },
                    label = { Text("Owner name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                Text("Tags:", color = Primary)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    filterUiState.tags.forEach { tag ->
                        AssistChip(
                            onClick = { mapViewModel.removeTagFilter(tag) },
                            label = { Text(tag) },
                            leadingIcon = {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Remove tag",
                                    tint = Color.Red
                                )
                            }
                        )
                    }
                    AssistChip(
                        onClick = { showTagSelector = true },
                        label = { Text("Add") },
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add tag"
                            )
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    TextButton(onClick = { showDateRangePicker = true }) {
                        Text("Date Range: ${filterUiState.createdAfter.toDateString()} - ${filterUiState.createdBefore.toDateString()}")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    mapViewModel.applyFilters();
                    onDismissRequest()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { mapViewModel.clearFilters() }) {
                    Text("Clear")
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        },
        modifier = modifier
    )

    if (showTagSelector) {
        TagSelector(
            title = "Select tag",
            onTagSelected = {
                mapViewModel.addTagFilter(it)
                showTagSelector = false
            },
            onDismissRequest = { showTagSelector = false },
            modifier = Modifier.background(Background).padding(8.dp)
        )
    }
    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = filterUiState.createdAfter.seconds * 1000,
            initialSelectedEndDateMillis = filterUiState.createdBefore.seconds * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis

                        if (startMillis != null) {
                            mapViewModel.changeCreatedAfterFilter(Timestamp(startMillis / 1000, 0))
                        }
                        if (endMillis != null) {
                            mapViewModel.changeCreatedBeforeFilter(Timestamp(endMillis / 1000, 0))
                        }

                        showDateRangePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false })
                {
                    Text("Cancel")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Background
            )
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Background,
                    titleContentColor = Primary,
                    headlineContentColor = Primary,
                    weekdayContentColor = Primary,
                    subheadContentColor = Primary,
                    selectedDayContainerColor = Primary,
                    selectedDayContentColor = Background,
                    todayContentColor = Primary,
                    dayInSelectionRangeContainerColor = Background,
                    dayInSelectionRangeContentColor = Primary
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}