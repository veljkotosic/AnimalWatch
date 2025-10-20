package com.veljkotosic.animalwatch.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veljkotosic.animalwatch.ui.theme.Background

@Composable
fun YesNoDialog (
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    yesText: @Composable (() -> Unit)? = null,
    noText: @Composable (() -> Unit)? = null,
    onYes: () -> Unit,
    onNo: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = Background,
        title = {
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    title()
                }
            }
        },
        text = {
            if (text != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    text()
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onYes()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    if (yesText != null) {
                        yesText()
                    } else {
                        Text("Yes")
                    }
                }
                Button(
                    onClick = {
                        onNo()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    if (noText != null) {
                        noText()
                    } else {
                        Text("No")
                    }
                }
            }
        },
        onDismissRequest = { onDismiss() },
        modifier = modifier
    )
}