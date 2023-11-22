package ru.kheynov.hotel.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface Employee : Entity<Employee> {
    companion object : Entity.Factory<Employee>()

    var id: Int
    var user: User
}

object Employees : Table<Employee>("employees") {
    var id = int("id").primaryKey().bindTo(Employee::id)
    var user = text("user_id").references(Users) { it.user }
}