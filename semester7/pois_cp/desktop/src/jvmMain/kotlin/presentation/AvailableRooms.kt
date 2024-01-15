package presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.entities.Room
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AvailableRooms(
    info: List<Room>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Информация о свободных номерах") },
        text = {
            LazyColumn(
                modifier = modifier.size(width = 300.dp, height = 500.dp).padding(8.dp),
            ) {
                if (info.isEmpty()) item { Text("Нет свободных номеров") }
                else item { Text("Всего свободных номеров: ${info.size}") }
                items(info) {
                    Column(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .padding(8.dp)
                    ) {
                        Text(
                            "Отель: ${it.hotel.name}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Комната: ${it.number}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Цена: ${it.price}₽", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Тип комнаты: ${it.type}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("ОК")
            }
        },
        modifier = modifier,
    )
}