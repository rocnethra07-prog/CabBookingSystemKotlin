package cab_booking.repository

import cab_booking.auth.UserAuthInfo

object AuthRepo : InMemoryRepo<UserAuthInfo>() {

    override fun getKey(entity: UserAuthInfo) = entity.userId.trim()

    fun findByUserId(userId: String): UserAuthInfo? =
        findByKey(userId)


    fun getLockedAccounts() =
         storage.values.filter { it.isAccountLocked }

}