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
import domain.entities.RoomReservationInfo
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReservationsInfo(
    info: List<RoomReservationInfo>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Информация о бронированиях") },
        text = {
            LazyColumn(
                modifier = modifier.size(width = 300.dp, height = 500.dp).padding(8.dp),
            ) {
                if (info.isEmpty()) item { Text("Нет бронирований") }
                else item { Text("Всего бронирований: ${info.size}") }
                items(info) {
                    Column(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .padding(8.dp)
                    ) {
                        Text(
                            "Имя: ${it.user.name}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Отель: ${it.room.hotel.name}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Комната: ${it.room.number}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Цена: ${it.room.price}₽", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Тип комнаты: ${it.room.type}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Заселение: ${it.from.format(formatter)}", fontSize = 16.sp, color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Выселение: ${it.to.format(formatter)}", fontSize = 16.sp, color = Color.Black,
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