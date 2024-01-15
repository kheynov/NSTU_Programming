package core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DataInputDialog(
    header: List<String>,
    data: List<String> = List(header.size) { "" },
    onEdited: (List<String>) -> Unit = {},
    editDenied: List<Int>,
    onCanceled: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val editedState = remember { mutableStateOf(data) }

    AlertDialog(
        onDismissRequest = { },
        title = { Text("Редактирование") },
        text = {
            DataEditor(
                header = header,
                data = data,
                editDenied = editDenied,
                onEdited = { editedState.value = it },
                modifier = modifier,
            )
        },
        confirmButton = {
            Button(
                border = BorderStroke(1.dp, Color.Green),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green),
                onClick = { onEdited(editedState.value) }) {
                Text("Сохранить", color = Color.Black)
            }
        },
        dismissButton = {
            Button(
                border = BorderStroke(1.dp, Color.Green),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green),
                onClick = onCanceled
            ) {
                Text("Отмена", color = Color.Black)
            }
        },
        modifier = modifier,
    )
}

@Composable
fun DataEditor(
    header: List<String>,
    data: List<String> = List(header.size) { "" },
    editDenied: List<Int> = listOf(0),
    onEdited: (List<String>) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val editedState = remember { mutableStateOf(data) }
    Column(modifier = modifier) {
        header.forEachIndexed { idx, col ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(text = "$col:   ", color = Color.Black)
                if (editDenied.contains(idx)) Text(text = "${editedState.value[idx]} (не редактируется)", color = Color.Black)
                else TextField(
                    value = editedState.value[idx],
                    onValueChange = { data ->
                        val tmp = editedState.value.toMutableList()
                        tmp[idx] = data
                        editedState.value = tmp
                        onEdited(editedState.value)
                    },
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}