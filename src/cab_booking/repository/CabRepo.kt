package cab_booking.repository

import cab_booking.model.Cab
import cab_booking.model.types.CabType

object CabRepo : InMemoryRepo<Cab>() {

    override fun getKey(entity: Cab): String = entity.cabId

    fun existsByRegistrationNumber(registrationNumber: String): Boolean {
        val trimmed = registrationNumber.trim()
        if (trimmed.isBlank()) return false

        return storage.values.any {
            it.registrationNumber.equals(trimmed, ignoreCase = true)
        }
    }

    fun findByCabType(cabType: CabType): List<Cab> =
        storage.values.filter {
            it.cabType == cabType
        }
}