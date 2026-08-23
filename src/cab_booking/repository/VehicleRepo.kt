package cab_booking.repository

import cab_booking.model.Vehicle
import cab_booking.model.types.VehicleCategory

object VehicleRepo : InMemoryRepo<Vehicle>() {
    override fun getKey(entity: Vehicle): String = entity.vehicleId

    fun existsByRegistrationNumber(registrationNumber: String): Boolean {
        val trimmed = registrationNumber.trim()
        if (trimmed.isBlank()) return false

        return storage.values.any {
            it.registrationNumber.equals(trimmed, ignoreCase = true)
        }
    }

    fun findByCategory(vehicleCategory: VehicleCategory): List<Vehicle> =
        storage.values.filter {
            it.vehicleCategory == vehicleCategory
        }
}