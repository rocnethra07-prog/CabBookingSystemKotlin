package cab_booking.repository

import cab_booking.model.UserAuthInfo

object AuthRepo : InMemoryRepo<UserAuthInfo>() {

    override fun getKey(entity: UserAuthInfo): String {
        return entity.userId.trim()
    }

    override fun save(entity: UserAuthInfo) {
        require(!existsByKey(entity.userId)) {
            "Authentication information already exists for user: ${entity.userId}"
        }

        super.save(entity)
    }

    fun validateCredentials(userId: String, password: String): Boolean {
        val trimmedUserId = userId.trim()

        if (trimmedUserId.isBlank()) {
            return false
        }

        val authInfo = if (existsByKey(trimmedUserId)) {
            findByKey(trimmedUserId)
        } else {
            return false
        }

        return authInfo.verifyPassword(password)
    }

    fun findByUserId(userId: String): UserAuthInfo? {
        val trimmedUserId = userId.trim()

        require(trimmedUserId.isNotBlank()) {
            "User ID cannot be blank."
        }

        return if (existsByKey(trimmedUserId)) {
            findByKey(trimmedUserId)
        }
        else {
            null
        }
    }
}