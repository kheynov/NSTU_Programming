package ru.kheynov.hotel.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.text

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    var userId: String
    var name: String
    var email: String
    var passwordHash: String
}

object Users : Table<User>("users") {
    var userId = text("id").primaryKey().bindTo(User::userId)
    var name = text("name").bindTo(User::name)
    var email = text("email").bindTo(User::email)
    var passwordHash = text("password_hash").bindTo(User::passwordHash)
}