package data.entities

import core.DataRow
import core.TableData
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface Room : Entity<Room> {
    companion object : Entity.Factory<Room>()

    var id: String
    var type: String
    var price: Int
    var number: Int
    var hotel: Hotel
}

val List<Room>.asTableData: TableData
    get() = TableData(
        header = listOf("ID", "Тип", "Цена", "Номер", "Отель"),
        data = map {
            DataRow(
                listOf(
                    it.id,
                    it.type,
                    it.price.toString(),
                    it.number.toString(),
                    it.hotel.name
                )
            )
        }
    )

object Rooms : Table<Room>("rooms") {
    val id = text("id").primaryKey().bindTo { it.id }
    val type = text("type").bindTo { it.type }
    val price = int("price").bindTo { it.price }
    val number = int("number").bindTo { it.number }
    val hotel = int("hotel_id").references(Hotels) { it.hotel }
}