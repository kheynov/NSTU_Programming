package presentation

import core.TableData
import data.Repository
import data.entities.Hotel
import data.entities.Reservation
import data.entities.Room
import data.entities.User
import data.entities.asTableData
import domain.entities.RoomReservationInfo
import domain.entities.asTableData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ktorm.database.Database
import presentation.state_holders.ErrorStates
import presentation.state_holders.Routes
import presentation.state_holders.State
import java.time.LocalDate
import java.util.*


class ViewModel {

    private var database: Database = Database.connect(
        url = Constants.url,
        driver = Constants.driver,
        user = Constants.user,
        password = Constants.password,
    )

    private val repository = Repository(database)

    private val _route: MutableStateFlow<Routes> = MutableStateFlow(Routes.Hotels)
    val route: StateFlow<Routes> = _route.asStateFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _errState: MutableStateFlow<ErrorStates> = MutableStateFlow(ErrorStates.Idle)
    val errState: StateFlow<ErrorStates> = _errState.asStateFlow()

    private val _data: MutableStateFlow<TableData> = MutableStateFlow(TableData(emptyList(), emptyList()))
    val data: StateFlow<TableData> = _data.asStateFlow()

    private val _hotels: MutableStateFlow<List<Hotel>> = MutableStateFlow(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    init {
        loadData(this._route.value)
        fetchHotels()
    }

    private fun loadData(route: Routes) {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(Dispatchers.IO).async {
                when (route) {
                    Routes.Hotels -> _data.update { repository.HotelsRepository().getAll().asTableData }
                    is Routes.Reservations -> {
                        fetchHotels()
                        _data.update {
                            repository
                                .ReservationsRespository()
                                .getAll()
                                .filter { it.room.hotel.id == route.hotelId }
                                .asTableData
                        }
                    }

                    Routes.Users -> _data.update { repository.UsersRepository().getAll().asTableData }
                    is Routes.Rooms -> {
                        fetchHotels()
                        _data.update {
                            repository.RoomsRepository().getAllInHotel(route.hotelId).asTableData
                        }
                    }
                }
            }
            awaitAll(job)
            _state.update { State.Idle }
        }
    }

    fun navigate(route: Routes) {
        this._route.update { route }
        loadData(route)
    }

    private fun fetchHotels() {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(Dispatchers.IO).async {
                _hotels.update { repository.HotelsRepository().getAll() }
            }
            awaitAll(job)
            _state.update { State.Idle }
        }
    }

    fun startEditing(id: String) = _state.update { if (it is State.Editing) State.Idle else State.Editing(id) }

    fun startAdding() = _state.update { if (it is State.Adding) State.Idle else State.Adding }

    fun clearState() {
        _state.update { State.Idle }
        _errState.update { ErrorStates.Idle }
    }

    private fun showError(text: String) {
        _errState.update { ErrorStates.ShowError(text) }
    }

    fun add(row: List<String>) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(Dispatchers.IO).async {
                when (_route.value) {
                    Routes.Hotels -> if (!repository.HotelsRepository().create(
                            updateData(_route.value, row) ?: return@async
                        )
                    ) {
                        _errState.update { ErrorStates.ShowError("Ошибка!") }
                        return@async
                    }

                    is Routes.Reservations -> if (!repository.ReservationsRespository().create(
                            updateData(_route.value, row) ?: return@async
                        )
                    ) {
                        _errState.update { ErrorStates.ShowError("Ошибка!") }
                        return@async
                    }

                    Routes.Users -> if (!repository.UsersRepository()
                            .create(updateData(_route.value, row) ?: return@async)
                    ) {
                        _errState.update { ErrorStates.ShowError("Ошибка!") }
                        return@async
                    }

                    is Routes.Rooms -> if (!repository.RoomsRepository().create(
                            updateData(_route.value, row) ?: return@async
                        )
                    ) {
                        _errState.update { ErrorStates.ShowError("Ошибка!") }
                        return@async
                    }
                }
            }

            awaitAll(job)
            _state.update { State.Idle }
            loadData(_route.value)
        }
    }

    fun edit(row: List<String>) {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
                when (_route.value) {
                    Routes.Hotels -> repository.HotelsRepository().update(updateData(_route.value, row) ?: return@async)
                    is Routes.Reservations -> repository.ReservationsRespository()
                        .update(updateData(_route.value, row) ?: return@async)

                    Routes.Users -> repository.UsersRepository().update(updateData(_route.value, row) ?: return@async)
                    is Routes.Rooms -> repository.RoomsRepository()
                        .update(updateData(_route.value, row) ?: return@async)
                }
            }
            awaitAll(job)
            _state.update { State.Idle }
            loadData(_route.value)
        }
    }

    fun deleteRow(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(Dispatchers.IO).async {
                when (_route.value) {
                    Routes.Hotels -> repository.HotelsRepository().delete(id.toIntOrNull() ?: run {
                        _errState.update { ErrorStates.ShowError("Неправильный формат id") }
                        return@async
                    })

                    is Routes.Reservations -> repository.ReservationsRespository().delete(id)
                    Routes.Users -> repository.UsersRepository().delete(id)
                    is Routes.Rooms -> repository.RoomsRepository().delete(id)
                }
            }
            awaitAll(job)
            _state.update { State.Idle }
            loadData(_route.value)
        }
    }

    private fun checkRooms(id: Int, roomNumber: Int): Boolean =
        repository.RoomsRepository().getAllInHotel(id).find { it.number == roomNumber } == null

    private fun checkRoomAvailable(roomId: String, from: LocalDate, to: LocalDate): Boolean {
        val reservations = repository.ReservationsRespository().getByRoomId(roomId)
        return reservations.find {
            (from in it.from..it.to) || (to in it.from..it.to)
        } == null
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> updateData(route: Routes, row: List<String>): T? {
        val row = row.map { it.trim() }
        return when (route) {
            Routes.Hotels -> Hotel {
                id = if (row[0].isNotBlank()) try {
                    row[0].toInt()
                } catch (e: Exception) {
                    showError("Неправильный формат id")
                    return null
                }
                else hotels.value.last().id + 1
                name = row[1].ifBlank { showError("Название не может быть пустым"); return null }
                city = row[2].ifBlank { showError("Город не может быть пустым"); return null }
                address = row[3].ifBlank { showError("Адрес не может быть пустым"); return null }
                rating = try {
                    row[4].toInt().also {
                        if (it !in 1..5) {
                            showError("Рейтинг должен быть в диапазоне от 1 до 5")
                            return null
                        }
                    }
                } catch (e: Exception) {
                    showError("Неправильный формат рейтинга")
                    return null
                }
            } as T

            is Routes.Reservations -> Reservation {
                id = row[0].ifBlank { UUID.randomUUID().toString().substring(0, 8) }
                guest = repository.UsersRepository().getById(row[1]) ?: run {
                    showError("Пользователь с ID ${row[1]} не найден!")
                    return null
                }
                room = repository.RoomsRepository().getById(row[2]) ?: run {
                    showError("Комната с ID ${row[3]} не найдена!")
                    return null
                }
                arrivalDate = try {
                    LocalDate.parse(row[3])
                } catch (e: Exception) {
                    showError("Неправильный формат даты")
                    return null
                }
                departureDate = try {
                    LocalDate.parse(row[4])
                } catch (e: Exception) {
                    showError("Неправильный формат даты")
                    return null
                }
            } as T

            Routes.Users -> User {
                userId = row[0].ifBlank { UUID.randomUUID().toString().substring(0, 8) }
                name = row[1].ifBlank { showError("Имя не может быть пустым"); return null }
            } as T

            is Routes.Rooms -> Room {
                id = row[0].ifBlank { UUID.randomUUID().toString().substring(0, 8) }
                type = row[1].ifBlank { showError("Тип не может быть пустым"); return null }
                price = try {
                    row[2].toInt()
                } catch (e: Exception) {
                    showError("Неправильный формат цены")
                    return null
                }
                number = try {
                    row[3].toInt().also {
                        if (it < 0) {
                            showError("Номер комнаты не может быть отрицательным")
                            return null
                        }
                        if (!checkRooms(route.hotelId, it)) {
                            showError("Такая комната уже существует")
                            return null
                        }
                    }
                } catch (e: Exception) {
                    showError("Неправильный формат номера")
                    return null
                }
                hotel = repository.HotelsRepository().getById(route.hotelId) ?: run {
                    showError("Отель с ID ${route.hotelId} не найден!")
                    return null
                }
            } as T
        }
    }

    fun showRoomBooking(roomId: String) {
        _state.update { State.ShowRoomBooking(roomId) }
    }

    fun bookRoom(row: List<String>, roomId: String) {
        val row = row.map { it.trim() }
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
                if (!repository.ReservationsRespository().create(
                        RoomReservationInfo(
                            id = UUID.randomUUID().toString().substring(0, 8),
                            room = repository.RoomsRepository().getById(roomId) ?: run {
                                showError("Комната с ID $roomId не найдена!")
                                return@async
                            },
                            user = repository.UsersRepository().getById(row[0].ifBlank {
                                showError("ID пользователя не может быть пустым")
                                return@async
                            }) ?: run {
                                showError("Пользователь с ID ${row[0]} не найден!")
                                return@async
                            },
                            from = try {
                                LocalDate.parse(row[1])
                            } catch (e: Exception) {
                                showError("Неправильный формат даты")
                                return@async
                            },
                            to = try {
                                LocalDate.parse(row[2]).also {
                                    when {
                                        it < LocalDate.now() -> {
                                            showError("Дата выезда не может быть раньше текущей")
                                            return@async
                                        }

                                        it < LocalDate.parse(row[1]) -> {
                                            showError("Дата выезда не может быть раньше даты заезда")
                                            return@async
                                        }
                                    }

                                    if (!checkRoomAvailable(roomId, LocalDate.parse(row[1]), it)) {
                                        showError("Комната занята в этот период")
                                        return@async
                                    }
                                }
                            } catch (e: Exception) {
                                showError("Неправильный формат даты")
                                return@async
                            }
                        )
                    )
                ) {
                    _errState.update { ErrorStates.ShowError("Ошибка!") }
                    return@async
                }
            }
            job.await()
            _state.update { State.Idle }
            loadData(_route.value)
        }

    }

    fun showReservationInfo(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
                val reservation = repository.ReservationsRespository().getById(id) ?: run {
                    showError("Бронь с ID $id не найдена!")
                    return@async
                }
                _state.update { State.ShowReservationInfo(reservation) }
            }
            job.await()
        }
    }

    fun showRoomReservations(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
                repository.RoomsRepository().getById(id) ?: run {
                    showError("Комната с ID $id не найдена!")
                    return@async
                }
                val reservations = repository.ReservationsRespository().getByRoomId(id)
                _state.update {
                    State.ShowRoomReservations(reservations)
                }
            }
            job.await()
        }
    }

    fun showUserReservations(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
                repository.UsersRepository().getById(id) ?: run {
                    showError("Пользователь с ID $id не найден!")
                    return@async
                }
                val reservations = repository.ReservationsRespository().getByUserId(id)
                _state.update {
                    State.ShowUsersBooking(reservations)
                }
            }
            job.await()
        }
    }

    fun showAvailableRoomsDateSelector() {
        _state.update { State.ShowDateSelector }
    }

    fun showAvailableRooms(from: String, to: String) {
        val arrival = try {
            LocalDate.parse(from.trim())
        } catch (e: Exception) {
            showError("Неправильный формат даты")
            return
        }
        val departure = try {
            LocalDate.parse(to.trim()).also {
                when {
                    it < LocalDate.now() -> {
                        showError("Дата выезда не может быть раньше текущей")
                        return
                    }

                    it < arrival -> {
                        showError("Дата выезда не может быть раньше даты заезда")
                        return
                    }
                }
            }
        } catch (e: Exception) {
            showError("Неправильный формат даты")
            return
        }
        CoroutineScope(Dispatchers.Default).launch {
            _state.update { State.Loading }
            val job = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
                val hotelId = (route.value as? Routes.Rooms)?.hotelId ?: run {
                    showError("Ошибка!")
                    return@async
                }
                val availableRooms = repository.RoomsRepository().getFreeRooms(hotelId, arrival, departure)
                _state.update { State.ShowAvailableRooms(availableRooms) }
            }
            job.await()
        }
    }
}

