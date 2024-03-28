package data

import data.entities.Hotel
import data.entities.Hotels
import data.entities.Reservation
import data.entities.Reservations
import data.entities.Room
import data.entities.Rooms
import data.entities.User
import data.entities.Users
import domain.entities.RoomReservationInfo
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.entity.update
import java.time.LocalDate

class Repository(
    private val database: Database,
    ) {

    inner class HotelsRepository {
        fun getAll(): List<Hotel> =
            database.sequenceOf(Hotels).toList().sortedBy { it.id }

        fun create(hotel: Hotel): Boolean =
            database.sequenceOf(Hotels).add(hotel) == 1

        fun getById(id: Int): Hotel? =
            database.sequenceOf(Hotels).find { it.id eq id }

        fun update(hotel: Hotel): Boolean =
            database.sequenceOf(Hotels).update(hotel) == 1

        fun delete(id: Int): Boolean =
            database.sequenceOf(Hotels).removeIf { it.id eq id } == 1
    }

    inner class RoomsRepository {
        fun getAllInHotel(hotelId: Int): List<Room> =
            database.sequenceOf(Rooms).filter { it.hotel eq hotelId }.toList()

        fun create(room: Room): Boolean =
            database.sequenceOf(Rooms).add(room) == 1

        fun getById(id: String): Room? =
            database.sequenceOf(Rooms).find { it.id eq id }

        fun update(room: Room): Boolean =
            database.sequenceOf(Rooms).update(room) == 1

        fun delete(id: String): Boolean =
            database.sequenceOf(Rooms).removeIf { it.id eq id } == 1

        fun getFreeRooms(hotelId: Int, from: LocalDate, to: LocalDate): List<Room> {
            val rooms = getAllInHotel(hotelId)
            val occupiedRooms = database
                .from(Rooms)
                .innerJoin(Reservations, on = Rooms.id eq Reservations.room)
                .select()
                .where { Rooms.hotel eq hotelId }
                .map { row ->
                    RoomReservationInfo(
                        id = row[Reservations.id]!!,
                        room = Room {
                            id = row[Rooms.id]!!
                            type = row[Rooms.type]!!
                            price = row[Rooms.price]!!
                            hotel = HotelsRepository().getById(row[Rooms.hotel]!!)!!
                        },
                        user = UsersRepository().getById(row[Reservations.guest]!!)!!,
                        from = row[Reservations.arrivalDate]!!,
                        to = row[Reservations.departureDate]!!,
                    )
                }
            return rooms.filter { room ->
                occupiedRooms.none { it.room.id == room.id && (from !in it.from..it.to || to !in it.from..it.to) }
            }
        }
    }

    inner class UsersRepository {
        fun getAll(): List<User> =
            database.sequenceOf(Users).toList().sortedBy { it.name }

        fun create(user: User): Boolean =
            database.sequenceOf(Users).add(user) == 1

        fun getById(id: String): User? =
            database.sequenceOf(Users).find { it.userId eq id }

        fun update(user: User): Boolean =
            database.sequenceOf(Users).update(user) == 1

        fun delete(id: String): Boolean =
            database.sequenceOf(Users).removeIf { it.userId eq id } == 1
    }

    inner class ReservationsRespository {
        fun getAll(): List<RoomReservationInfo> =
            database
                .from(Reservations)
                .innerJoin(Rooms, on = Reservations.room eq Rooms.id)
                .innerJoin(Hotels, on = Rooms.hotel eq Hotels.id)
                .innerJoin(Users, on = Reservations.guest eq Users.userId)
                .select()
                .map { row ->
                    RoomReservationInfo(
                        id = row[Reservations.id]!!,
                        room = Room {
                            this.id = row[Rooms.id]!!
                            this.type = row[Rooms.type]!!
                            this.price = row[Rooms.price]!!
                            this.number = row[Rooms.number]!!
                            this.hotel = Hotel {
                                this.id = row[Hotels.id]!!
                                this.name = row[Hotels.name]!!
                                this.city = row[Hotels.city]!!
                                this.address = row[Hotels.address]!!
                            }
                        },
                        user = User {
                            this.userId = row[Users.userId]!!
                            this.name = row[Users.name]!!
                        },
                        from = row[Reservations.arrivalDate]!!,
                        to = row[Reservations.departureDate]!!,
                    )
                }

        fun create(reservation: RoomReservationInfo): Boolean =
            database.sequenceOf(Reservations).add(Reservation {
                id = reservation.id
                guest = reservation.user
                room = reservation.room
                arrivalDate = reservation.from
                departureDate = reservation.to
            }) == 1

        fun getById(id: String): RoomReservationInfo? =
            database
                .from(Reservations)
                .innerJoin(Rooms, on = Reservations.room eq Rooms.id)
                .innerJoin(Hotels, on = Rooms.hotel eq Hotels.id)
                .innerJoin(Users, on = Reservations.guest eq Users.userId)
                .select()
                .where { Reservations.id eq id }
                .map { row ->
                    RoomReservationInfo(
                        id = row[Reservations.id]!!,
                        room = Room {
                            this.id = row[Rooms.id]!!
                            type = row[Rooms.type]!!
                            price = row[Rooms.price]!!
                            this.number = row[Rooms.number]!!
                            hotel = Hotel {
                                this.id = row[Hotels.id]!!
                                city = row[Hotels.city]!!
                                this.name = row[Hotels.name]!!
                                address = row[Hotels.address]!!
                            }
                        },
                        user = User {
                            userId = row[Users.userId]!!
                            name = row[Users.name]!!
                        },
                        from = row[Reservations.arrivalDate]!!,
                        to = row[Reservations.departureDate]!!,
                    )
                }.firstOrNull()

        fun update(reservation: RoomReservationInfo): Boolean =
            database.sequenceOf(Reservations).update(Reservation {
                id = reservation.id
                guest = reservation.user
                room = reservation.room
                arrivalDate = reservation.from
                departureDate = reservation.to
            }) == 1

        fun delete(id: String): Boolean =
            database.sequenceOf(Reservations).removeIf { it.id eq id } == 1

        fun getByUserId(userId: String): List<RoomReservationInfo> =
            database
                .from(Reservations)
                .innerJoin(Rooms, on = Reservations.room eq Rooms.id)
                .innerJoin(Hotels, on = Rooms.hotel eq Hotels.id)
                .innerJoin(Users, on = Reservations.guest eq Users.userId)
                .select()
                .where { Reservations.guest eq userId }
                .map { row ->
                    RoomReservationInfo(
                        id = row[Reservations.id]!!,
                        room = Room {
                            this.id = row[Rooms.id]!!
                            type = row[Rooms.type]!!
                            price = row[Rooms.price]!!
                            this.number = row[Rooms.number]!!
                            hotel = Hotel {
                                this.id = row[Hotels.id]!!
                                this.name = row[Hotels.name]!!
                                city = row[Hotels.city]!!
                                address = row[Hotels.address]!!
                            }
                        },
                        user = User {
                            this.userId = row[Users.userId]!!
                            name = row[Users.name]!!
                        },
                        from = row[Reservations.arrivalDate]!!,
                        to = row[Reservations.departureDate]!!,
                    )
                }

        fun getByRoomId(roomId: String): List<RoomReservationInfo> =
            database
                .from(Reservations)
                .innerJoin(Rooms, on = Reservations.room eq Rooms.id)
                .innerJoin(Hotels, on = Rooms.hotel eq Hotels.id)
                .innerJoin(Users, on = Reservations.guest eq Users.userId)
                .select()
                .where { Reservations.room eq roomId }
                .map { row ->
                    RoomReservationInfo(
                        id = row[Reservations.id]!!,
                        room = Room {
                            this.id = row[Rooms.id]!!
                            type = row[Rooms.type]!!
                            price = row[Rooms.price]!!
                            number = row[Rooms.number]!!
                            hotel = Hotel {
                                this.id = row[Hotels.id]!!
                                city = row[Hotels.city]!!
                                this.name = row[Hotels.name]!!
                                address = row[Hotels.address]!!
                            }
                        },
                        user = User {
                            this.userId = row[Users.userId]!!
                            name = row[Users.name]!!
                        },
                        from = row[Reservations.arrivalDate]!!,
                        to = row[Reservations.departureDate]!!,
                    )
                }
    }
}