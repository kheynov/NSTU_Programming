package domain.entities

import core.DataRow
import core.TableData
import data.entities.Room
import data.entities.User
import java.time.LocalDate


data class RoomReservationInfo(
    val id: String,
    val room: Room,
    val user: User,
    val from: LocalDate,
    val to: LocalDate,
)

private val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")

val List<RoomReservationInfo>.asTableData: TableData
    get() = TableData(
        header = listOf("ID", "Гость", "Комната", "Отель", "Заселение", "Выселение"),
        data = map {
            DataRow(
                listOf(
                    it.id,
                    it.user.name,
                    it.room.number.toString(),
                    it.room.hotel.name,
                    it.from.format(formatter),
                    it.to.format(formatter)
                )
            )
        }
    )