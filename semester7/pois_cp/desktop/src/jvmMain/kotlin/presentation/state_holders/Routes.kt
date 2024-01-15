package presentation.state_holders

sealed interface Routes {
    object Hotels : Routes
    data class Reservations(
        val hotelId: Int,
    ) : Routes
    data class Rooms(
        val hotelId: Int,
    ): Routes
    object Users : Routes
}