package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.Vehicle
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.service.AuthService
import cab_booking.service.DriverService
import cab_booking.service.UserService
import cab_booking.service.VehicleService

object AdminController{

    fun isLicenseNumberTaken(licenseNumber: String) =
        DriverService.isLicenseNumberExists(licenseNumber)

    fun isRegistrationNumberTaken(registrationNumber: String) =
        VehicleService.isRegistrationNumberExists(registrationNumber)

    fun isAccountLocked(userId: String) =
        AuthService.isAccountLocked(userId)

    fun lockUserAccount(userId: String) =
        AuthService.lockUserAccount(userId)

    //DRIVER MANAGEMENT
    fun addDriver(name: String, phone: String, email: String, password: String, currentLocation: Location, licenseNumber: String, vehicle: Vehicle) =
        DriverService.createDriver(name, phone, email, password,currentLocation, licenseNumber, vehicle)

    fun createVehicle(model: String, registrationNumber: String, vehicleCategory: VehicleCategory) =
        VehicleService.createVehicle(model, registrationNumber, vehicleCategory)

    fun deleteDriver(driver: Driver) =
        DriverService.deleteDriver(driver)

    fun getAllDrivers() =
        DriverService.getAllDrivers()

    fun getAvailableDrivers() =
        DriverService.getAvailableDrivers()

    fun getUnavailableDrivers() =
        DriverService.getUnavailableDrivers()

    // ---- VEHICLE MANAGEMENT ----

    fun getAllVehicles() =
        VehicleService.getAllVehicles()

    fun getVehiclesByCategory(vehicleCategory: VehicleCategory) =
        VehicleService.getVehiclesByCategory(vehicleCategory)

    //LOCK AND USER
    fun getLockedAccounts() =
        AuthService.getLockedAccounts()

    fun unlockUserAccount(userId: String) =
        AuthService.unlockUserAccount(userId)

    fun findUserById(userId: String) =
        UserService.findUserById(userId)
}