package cab_booking.service

import cab_booking.exception.VehicleNotFoundException
import cab_booking.model.Vehicle
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.VehicleRepo


object VehicleService {
    fun isRegistrationNumberExists(registrationNumber: String): Boolean =
        VehicleRepo.existsByRegistrationNumber(registrationNumber)

    fun createVehicle(model: String, registrationNumber: String, vehicleCategory: VehicleCategory) : Vehicle {
        val vehicle = Vehicle(model, registrationNumber, vehicleCategory)
        VehicleRepo.save(vehicle)
        return vehicle
    }

    fun deleteVehicle(vehicleId: String) =
        VehicleRepo.deleteByKey(vehicleId)

    fun getAllVehicles(): List<Vehicle> =
        VehicleRepo.findAll()

    fun getVehiclesByCategory(vehicleCategory: VehicleCategory): List<Vehicle> =
        VehicleRepo.findByCategory(vehicleCategory)

    fun getVehicleById(vehicleId: String): Vehicle =
        VehicleRepo.findByKey(vehicleId) ?: throw VehicleNotFoundException(vehicleId)

}
