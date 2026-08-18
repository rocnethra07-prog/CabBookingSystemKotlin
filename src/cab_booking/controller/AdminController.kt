package cab_booking.controller

import cab_booking.model.Cab
import cab_booking.model.Driver
import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.service.AuthService
import cab_booking.service.CabService
import cab_booking.service.DriverService
import cab_booking.service.RideService
import cab_booking.service.UserService

object AdminController{

    fun isEmailRegistered(email: String) =
        UserService.isEmailRegistered(email)

    fun isLicenseNumberTaken(licenseNumber: String) =
        DriverService.isLicenseNumberExists(licenseNumber)

    fun isRegistrationNumberTaken(registrationNumber: String) =
        CabService.isRegistrationNumberExists(registrationNumber)

    fun addDriver(name: String, phone: String, email: String, password: String, currentLocation: Location, licenseNumber: String, cab: Cab) =
        DriverService.createDriver(name, phone, email, password,currentLocation, licenseNumber, cab)

    fun createCab(model: String, cabType: CabType, registrationNumber: String) =
        CabService.createCab(model, cabType, registrationNumber)

    fun findDriverById(driverId: String) =
        DriverService.findDriverById(driverId)

    fun deleteDriver(driver: Driver) =
        DriverService.deleteDriver(driver)

    fun getAllDrivers() =
        DriverService.getAllDrivers()

    fun getAvailableDrivers() =
        DriverService.getAvailableDrivers()

    fun getUnavailableDrivers() =
        DriverService.getUnavailableDrivers()

    fun getCabForDriver(driver: Driver) =
        CabService.getCabForDriver(driver)

    fun getDriverRideHistory(driverId: String) =
        RideService.getDriverRideHistory(driverId)

    // RIDER MANAGEMENT
    fun getAllRiders()  =
        UserService.getAllRiders()

    fun getRiderRideHistory(riderId: String)  =
        RideService.getRiderRideHistory(riderId)

    // RIDE MANAGEMENT
    fun getAllRides() =
        RideService.getAllRides()

    fun getActiveRides() =
        RideService.getActiveRides()

    fun getCompletedRides() =
        RideService.getCompletedRides()

    fun getCancelledRides() =
        RideService.getCancelledRides()

    // CAB MANAGEMENT
    fun getAllCabs() =
        CabService.getAllCabs()

    fun getCabsByType(cabType: CabType) =
        CabService.getCabsByType(cabType)

    fun getLockedAccounts() =
        AuthService.getLockedAccounts()

    fun unlockUserAccount(userId: String) =
        AuthService.unlockUserAccount(userId)

    fun findUserById(userId: String) =
        UserService.findUserById(userId)
}