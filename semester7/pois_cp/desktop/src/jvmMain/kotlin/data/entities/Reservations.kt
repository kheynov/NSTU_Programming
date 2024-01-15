package data.entities

import core.DataRow
import core.TableData
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.text
import java.time.LocalDate

interface Reservation : Entity<Reservation> {
    companion object : Entity.Factory<Reservation>()

    var id: String
    var guest: User
    var room: Room
    var arrivalDate: LocalDate
    var departureDate: LocalDate
}

object Reservations : Table<Reservation>("reservations") {
    val id = text("id").primaryKey().bindTo { it.id }
    val guest = text("guest_id").references(Users) { it.guest }
    val room = text("room_id").references(Rooms) { it.room }
    val arrivalDate = date("arrival_date").bindTo { it.arrivalDate }
    val departureDate = date("departure_date").bindTo { it.departureDate }
}