package ru.kheynov.hotel.jwt.token

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val accessLifetime: Long,
    val refreshLifetime: Long,
    val secret: String,
)