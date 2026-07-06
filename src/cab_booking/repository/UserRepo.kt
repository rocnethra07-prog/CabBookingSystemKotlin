package cab_booking.repository

import cab_booking.exception.CabBookingException
import cab_booking.model.User

object UserRepo : InMemoryRepo<User>() {

    override fun getKey(entity: User): String {
        return entity.email.trim().lowercase()
    }

    override fun save(entity: User) {
        if(existsByEmail(entity.email)) {
            throw CabBookingException("User already exists with email: ${entity.email}")
        }

        super.save(entity)
    }

    fun findByEmail(email: String): User? {
        val trimmedEmail = email.trim().lowercase()

        if(trimmedEmail.isBlank()) {
            throw CabBookingException("Email cannot be blank.")
        }

        return try {
            findByKey(trimmedEmail)
        }
        catch (_ : CabBookingException){
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

        if(trimmedEmail.isBlank()) {
            throw CabBookingException("Email cannot be blank.")
        }

        deleteByKey(trimmedEmail)
    }
}