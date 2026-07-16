package cab_booking.repository

import cab_booking.model.UserAuthInfo

object AuthRepo : InMemoryRepo<UserAuthInfo>() {

    override fun getKey(entity: UserAuthInfo): String {
        return entity.userId.trim()
    }

    fun findByUserId(userId: String): UserAuthInfo? {
        return findByKey(userId)
    }

    fun getLockedAccounts(): List<UserAuthInfo> {
        return storage.values.filter { it.isAccountLocked }
    }
}