package cab_booking.repository

import cab_booking.model.Cab
import cab_booking.model.types.CabType

object CabRepo : InMemoryRepo<Cab>() {

    override fun getKey(entity: Cab): String = entity.cabId

    fun existsByRegistrationNumber(registrationNumber: String): Boolean {
        require(registrationNumber.isNotBlank()) {
            "Registration number cannot be blank"
        }

        return storage.values.any {
            it.registrationNumber.equals(registrationNumber, ignoreCase = true)
        }
    }

    fun findByCabType(cabType: CabType): List<Cab> =
        storage.values.filter {
            it.cabType == cabType
        }
}