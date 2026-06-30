package cab_booking.repository

import cab_booking.model.User

object UserRepo : InMemoryRepo<User>() {

    override fun getKey(entity: User): String {
        return entity.email.trim().lowercase()
    }

    override fun save(entity: User) {
        require(!existsByEmail(entity.email)) {
            "User already exists with email: ${entity.email}"
        }

        super.save(entity)
    }

    fun findByEmail(email: String): User? {
        val trimmedEmail = email.trim().lowercase()

        require(trimmedEmail.isNotBlank()) {
            "Email cannot be blank."
        }

        return if (existsByKey(trimmedEmail)) {
            findByKey(trimmedEmail)
        }
        else {
            null
        }
    }

    fun existsByEmail(email: String): Boolean {
        val trimmedEmail = email.trim().lowercase()

        if (trimmedEmail.isBlank()) {
            return false
        }

        return existsByKey(trimmedEmail)
    }

    fun deleteByEmail(email: String) {
        val trimmedEmail = email.trim().lowercase()

        require(trimmedEmail.isNotBlank()) {
            "Email cannot be blank."
        }

        deleteByKey(trimmedEmail)
    }
}