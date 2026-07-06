package cab_booking.repository

import cab_booking.exception.CabBookingException
import cab_booking.model.UserAuthInfo

object AuthRepo : InMemoryRepo<UserAuthInfo>() {

    override fun getKey(entity: UserAuthInfo): String {
        return entity.userId.trim()
    }

    fun findByUserId(userId: String): UserAuthInfo? {
        val trimmedUserId = userId.trim()

        if(trimmedUserId.isBlank()) {
            throw CabBookingException("User ID cannot be blank.")
        }
        return try{
            findByKey(trimmedUserId)
        }
        catch (ignored : CabBookingException){
            null
        }
    }
}