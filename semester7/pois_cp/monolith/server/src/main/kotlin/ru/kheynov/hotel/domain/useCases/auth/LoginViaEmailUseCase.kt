package ru.kheynov.hotel.domain.useCases.auth

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.hotel.domain.repositories.UsersRepository
import ru.kheynov.hotel.jwt.hashing.HashingService
import ru.kheynov.hotel.jwt.token.RefreshToken
import ru.kheynov.hotel.jwt.token.TokenClaim
import ru.kheynov.hotel.jwt.token.TokenConfig
import ru.kheynov.hotel.jwt.token.TokenPair
import ru.kheynov.hotel.jwt.token.TokenService

class LoginViaEmailUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val hashingService: HashingService by inject()
    private val tokenService: TokenService by inject()
    private val tokenConfig: TokenConfig by inject()

    sealed interface Result {
        data class Success(val tokenPair: TokenPair) : Result
        data object Forbidden : Result
        data object Failed : Result
    }

    suspend operator fun invoke(email: String, password: String, clientId: String): Result {
        val user = usersRepository.getUserByEmail(email) ?: return Result.Forbidden
        val passwordVerificationResult =
            hashingService.verify(password, user.passwordHash ?: return Result.Forbidden)
        if (!passwordVerificationResult.verified) return Result.Forbidden
        val tokenPair =
            tokenService.generateTokenPair(tokenConfig, TokenClaim("userId", user.userId))

        val isTokenExists = usersRepository.getRefreshToken(user.userId, clientId) != null

        val tokenUpdateResult = when {
            isTokenExists -> usersRepository.updateUserRefreshToken(
                userId = user.userId,
                clientId = clientId,
                newRefreshToken = tokenPair.refreshToken.token,
                refreshTokenExpiration = tokenPair.refreshToken.expiresAt,
            )

            else -> usersRepository.createRefreshToken(
                userId = user.userId,
                clientId = clientId,
                refreshToken = RefreshToken(
                    token = tokenPair.refreshToken.token,
                    expiresAt = tokenPair.refreshToken.expiresAt,
                ),
            )
        }

        if (tokenUpdateResult) {
            return Result.Success(tokenPair)
        }
        return Result.Failed
    }
}