package presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.DataInputDialog
import core.DropDownSelection
import core.MenuList
import core.RoomBooking
import core.Table
import presentation.state_holders.ErrorStates
import presentation.state_holders.Routes
import presentation.state_holders.State

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainPage() {
    val viewModel = remember { ViewModel() }

    val route = viewModel.route.collectAsState()
    val state = viewModel.state.collectAsState()
    val error = viewModel.errState.collectAsState()
    val data = viewModel.data.collectAsState()
    val hotels = viewModel.hotels.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            MenuList(buttonsNames = listOf("Отели", "Комнаты", "Бронирования", "Гости"),
                modifier = Modifier.width(200.dp).fillMaxHeight(0.6f),
                onClick = { idx ->
                    when (idx) {
                        0 -> viewModel.navigate(Routes.Hotels)
                        1 -> viewModel.navigate(Routes.Rooms(1))
                        2 -> viewModel.navigate(Routes.Reservations(1))
                        3 -> viewModel.navigate(Routes.Users)
                        else -> println("ERROR")
                    }
                })
        }
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.value is State.Loading || data.value.header.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.size(80.dp))
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (route.value) {
                        is Routes.Rooms -> DropDownSelection(
                            header = hotels.value.find { it.id == (route.value as Routes.Rooms).hotelId }?.name
                                ?: "Выберите отель",
                            data = hotels.value.map { it.name },
                            onSelection = { idx ->
                                viewModel.navigate(Routes.Rooms(hotels.value.find { it.name == idx }?.id ?: 1))
                            },
                            modifier = Modifier.width(200.dp).padding(8.dp),
                        )

                        is Routes.Reservations -> DropDownSelection(
                            header = hotels.value.find { it.id == (route.value as Routes.Reservations).hotelId }?.name
                                ?: "Выберите отель",
                            data = hotels.value.map { it.name },
                            onSelection = { idx ->
                                viewModel.navigate(Routes.Reservations(hotels.value.find { it.name == idx }?.id ?: 1))
                            },
                            modifier = Modifier.width(200.dp).padding(8.dp),
                        )

                        else -> {}
                    }

                    Table(
                        data = data.value, modifier = Modifier.fillMaxHeight(0.9f).padding(horizontal = 24.dp),
                        onEditing = { id ->
                            viewModel.startEditing(id)
                        },
                        onDeleting = { id ->
                            viewModel.deleteRow(id)
                        },
                        bookingEnabled = route.value is Routes.Rooms,
                        onBook = { id ->
                            viewModel.showRoomBooking(id)
                        },
                        infoEnabled = route.value is Routes.Reservations || route.value is Routes.Rooms || route.value is Routes.Users,
                        onInfo = { id ->
                            when (route.value) {
                                is Routes.Reservations -> viewModel.showReservationInfo(id)
                                is Routes.Rooms -> viewModel.showRoomReservations(id)
                                is Routes.Users -> viewModel.showUserReservations(id)
                                else -> {}
                            }
                        },
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        if (route.value !is Routes.Reservations) {
                            Button(
                                border = BorderStroke(1.dp, Color.Green),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green),
                                onClick = viewModel::startAdding,
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Добавить", color = Color.Black)
                            }
                        }

                        if (route.value is Routes.Rooms){
                            Button(
                                border = BorderStroke(1.dp, Color.Green),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green),
                                onClick = viewModel::showAvailableRoomsDateSelector,
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Свободные номера", color = Color.Black)
                            }
                        }
                    }
                }

                when (state.value) {
                    State.Adding -> if (route.value !is Routes.Reservations) DataInputDialog(
                        header = data.value.header,
                        onEdited = { row ->
                            viewModel.add(row)
                            viewModel.clearState()
                        },
                        editDenied = run {
                            when (route.value) {
                                is Routes.Reservations -> listOf(0, 3)
                                is Routes.Rooms -> listOf(0, 4)
                                else -> listOf(0)
                            }
                        },
                        onCanceled = viewModel::clearState,
                    )

                    is State.Editing -> DataInputDialog(
                        header = data.value.header,
                        data = data.value.data.firstOrNull { it.items[0] == (state.value as State.Editing).row }?.items
                            ?: emptyList(),
                        onEdited = { row ->
                            viewModel.edit(row)
                            viewModel.clearState()
                        },
                        editDenied = run {
                            when (route.value) {
                                is Routes.Reservations -> listOf(0, 3)
                                is Routes.Rooms -> listOf(0, 4)
                                else -> listOf(0)
                            }
                        },
                        onCanceled = viewModel::clearState,
                    )

                    is State.ShowRoomBooking -> RoomBooking(
                        onEdited = { row ->
                            viewModel.bookRoom(row, roomId = (state.value as State.ShowRoomBooking).roomId)
                            viewModel.clearState()
                        },
                        editDenied = emptyList(),
                        onCanceled = viewModel::clearState,
                    )

                    is State.ShowReservationInfo -> ReservationsInfo(
                        info = listOf((state.value as State.ShowReservationInfo).data),
                        onDismiss = viewModel::clearState,
                    )

                    is State.ShowRoomReservations -> ReservationsInfo(
                        info = (state.value as State.ShowRoomReservations).data,
                        onDismiss = viewModel::clearState,
                    )

                    is State.ShowUsersBooking -> ReservationsInfo(
                        info = (state.value as State.ShowUsersBooking).data,
                        onDismiss = viewModel::clearState,
                    )

                    is State.ShowDateSelector -> AvailableRoomsDateSelector(
                        onSelected = { dates ->
                            viewModel.showAvailableRooms(dates.first, dates.second)
                            viewModel.clearState()
                        },
                        onCanceled = viewModel::clearState,
                    )

                    is State.ShowAvailableRooms -> AvailableRooms(
                        info = (state.value as State.ShowAvailableRooms).data,
                        onDismiss = viewModel::clearState,
                    )
                    else -> {}
                }

                if (error.value is ErrorStates.ShowError) {
                    AlertDialog(
                        title = { Text("Ошибка", color = Color.Black) },
                        onDismissRequest = { },
                        text = {
                            Text(
                                text = (error.value as? ErrorStates.ShowError)?.error ?: "ERROR",
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = viewModel::clearState,
                                border = BorderStroke(1.dp, Color.Green),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green),
                            ) {
                                Text("OK", textAlign = TextAlign.Center, color = Color.Black)
                            }
                        },
                        modifier = Modifier.width(300.dp),
                    )
                }
            }
        }
    }
}