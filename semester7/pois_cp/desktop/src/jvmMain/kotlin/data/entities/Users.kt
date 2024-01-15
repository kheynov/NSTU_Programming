package data.entities

import core.DataRow
import core.TableData
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.text

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    var userId: String
    var name: String
}

val List<User>.asTableData: TableData
    get() = TableData(
        header = listOf("ID", "Имя"),
        data = map {
            DataRow(
                listOf(
                    it.userId,
                    it.name
                )
            )
        }
    )

object Users : Table<User>("users") {
    var userId = text("id").primaryKey().bindTo(User::userId)
    var name = text("name").bindTo(User::name)
}