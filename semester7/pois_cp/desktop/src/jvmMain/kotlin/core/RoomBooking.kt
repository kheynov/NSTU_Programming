package core

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RoomBooking(
    onEdited: (List<String>) -> Unit = {},
    editDenied: List<Int>,
    onCanceled: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val header: List<String> = listOf("ID гостя", "Дата заезда", "Дата выезда")
    val data: List<String> = List(header.size) { "" }

    val editedState = remember { mutableStateOf(data) }
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Забронировать комнату") },
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