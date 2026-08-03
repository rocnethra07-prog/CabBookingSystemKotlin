package cab_booking.repository

import cab_booking.auth.UserCredential

object AuthRepo : InMemoryRepo<UserCredential>() {

    override fun getKey(entity: UserCredential) = entity.userId.trim()

    fun findByUserId(userId: String): UserCredential? =
        findByKey(userId)


    fun getLockedAccounts() =
         storage.values.filter { it.isAccountLocked }

}