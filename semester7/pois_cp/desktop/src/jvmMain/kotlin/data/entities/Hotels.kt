package data.entities

import core.DataRow
import core.TableData
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface Hotel : Entity<Hotel> {
    companion object : Entity.Factory<Hotel>()

    var id: Int
    var name: String
    var city: String
    var address: String
    var rating: Int
}

val List<Hotel>.asTableData: TableData
    get() = TableData(
        header = listOf("ID", "Название", "Город", "Адрес", "Рейтинг"),
        data = map {
            DataRow(
                listOf(it.id.toString(), it.name, it.city, it.address, it.rating.toString())
            )
        }
    )

object Hotels : Table<Hotel>("hotels") {
    var id = int("id").primaryKey().bindTo(Hotel::id)
    var name = text("name").bindTo(Hotel::name)
    var city = text("city").bindTo(Hotel::city)
    var address = text("address").bindTo(Hotel::address)
    var rating = int("rating").bindTo(Hotel::rating)
}