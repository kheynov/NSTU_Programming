package presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.DataEditor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AvailableRoomsDateSelector(
    onSelected: (Pair<String, String>) -> Unit = {},
    onCanceled: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var editedState by remember { mutableStateOf("" to "") }

    AlertDialog(
        onDismissRequest = { },
        title = { Text("Редактирование") },
        text = {
            DataEditor(
                header = listOf("Дата заезда", "Дата выезда"),
                editDenied = emptyList(),
                onEdited = {
                    editedState = it[0] to it[1]
                },
                modifier = modifier,
            )
        },
        confirmButton = {
            Button(
                border = BorderStroke(1.dp, Color.Green),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green),
                onClick = { onSelected(editedState) }) {
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