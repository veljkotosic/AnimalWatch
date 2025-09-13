package com.veljkotosic.animalwatch.composable.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.veljkotosic.animalwatch.ui.theme.Background
import com.veljkotosic.animalwatch.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagComboBox(
    tags: List<String>,
    title: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .background(Background)
    ) {
        TextField(
            value = title,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .background(Background)
                .menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Background,
                unfocusedContainerColor = Background,
                focusedTextColor = Primary,
                unfocusedTextColor = Primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Background)
        ) {
            tags.forEach { tag ->
                TextButton(
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    },
                    modifier = Modifier
                        .background(Background)
                        .fillMaxWidth()
                ) {
                    Text(text = tag)
                }
            }
        }
    }
}