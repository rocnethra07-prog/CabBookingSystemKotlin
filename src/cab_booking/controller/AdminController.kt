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

    fun isLicenseNumberTaken(licenseNumber: String) =
        DriverService.isLicenseNumberExists(licenseNumber)

    fun isRegistrationNumberTaken(registrationNumber: String) =
        CabService.isRegistrationNumberExists(registrationNumber)

    //DRIVER MANAGEMENT
    fun addDriver(name: String, phone: String, email: String, password: String, currentLocation: Location, licenseNumber: String, cab: Cab) =
        DriverService.createDriver(name, phone, email, password,currentLocation, licenseNumber, cab)

    fun createCab(model: String, cabType: CabType, registrationNumber: String) =
        CabService.createCab(model, cabType, registrationNumber)

    fun deleteDriver(driver: Driver) =
        DriverService.deleteDriver(driver)

    fun getAllDrivers() =
        DriverService.getAllDrivers()

    fun getAvailableDrivers() =
        DriverService.getAvailableDrivers()

    fun getUnavailableDrivers() =
        DriverService.getUnavailableDrivers()

    fun getCabForDriverByCabId(cabId: String) =
        CabService.getCabForDriverByCabId(cabId)

    // RIDER MANAGEMENT
    fun getAllRiders()  =
        UserService.getAllRiders()

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

    //LOCK AND USER
    fun getLockedAccounts() =
        AuthService.getLockedAccounts()

    fun unlockUserAccount(userId: String) =
        AuthService.unlockUserAccount(userId)

    fun findUserById(userId: String) =
        UserService.findUserById(userId)
}