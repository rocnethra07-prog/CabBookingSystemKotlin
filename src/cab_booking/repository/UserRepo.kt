package cab_booking.repository

import cab_booking.exception.UserNotFoundException
import cab_booking.model.User

object UserRepo : InMemoryRepo<User>() {

    override fun getKey(entity: User): String {
        return entity.email.trim().lowercase()
    }

    fun findByEmail(email: String): User {
        return findByKey(email) ?: throw UserNotFoundException("Account does not exist. Please register.")
    }

    fun existsByEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        return existsByKey(email)
    }

    fun deleteByEmail(email: String) {
        deleteByKey(email)
    }

    fun findByUserId(userId: String): User =
        storage.values.firstOrNull { it.userId == userId } ?: throw UserNotFoundException("User not found for ID: $userId")

}