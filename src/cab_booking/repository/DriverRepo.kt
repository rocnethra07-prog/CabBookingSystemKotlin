package cab_booking.repository

import cab_booking.model.Driver

object DriverRepo : InMemoryRepo<Driver>() {

    override fun getKey(entity: Driver): String = entity.userId

    fun findAvailableDrivers(): List<Driver> =
        storage.values.filter {
            it.isAvailable
        }

    fun findUnavailableDrivers(): List<Driver> =
        storage.values.filterNot {
            it.isAvailable
        }

    fun existsByLicense(license: String): Boolean {
        require(license.isNotBlank()) {"License number cannot be blank"}

        return storage.values.any {
            it.licenseNumber.equals(license, ignoreCase = true)
        }
    }
}