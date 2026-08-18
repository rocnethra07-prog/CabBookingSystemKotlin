package cab_booking.repository

import cab_booking.exception.UserNotFoundException
import cab_booking.model.User

object UserRepo : InMemoryRepo<User>() {

    override fun getKey(entity: User): String {
        return entity.email.trim().lowercase()
    }

    fun findByEmail(email: String): User {
        val trimmedEmail = trimEmail(email)
        return findByKey(trimmedEmail) ?: throw UserNotFoundException("Account does not exist. Please register.")
    }

    fun existsByEmail(email: String): Boolean {
        val trimmedEmail = trimEmail(email)

        if (trimmedEmail.isBlank()) {
            return false
        }

        return existsByKey(trimmedEmail)
    }

    fun deleteByEmail(email: String) {
        val trimmedEmail = trimEmail(email)
        deleteByKey(trimmedEmail)
    }

    fun findByUserId(userId: String): User =
        storage.values.firstOrNull { it.userId == userId } ?: throw UserNotFoundException("User not found for ID: $userId")

    private fun trimEmail(email: String) =
        email.trim().lowercase()
}