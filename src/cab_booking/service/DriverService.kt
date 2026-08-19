package cab_booking.service

import cab_booking.exception.DriverNotFoundException
import cab_booking.model.Driver
import cab_booking.model.types.Location
import cab_booking.model.Ride
import cab_booking.model.types.RideStatus
import cab_booking.repository.DriverRepo
import cab_booking.repository.RideRepo
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Cab
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo
import java.math.BigDecimal
import java.time.LocalDateTime

object DriverService {

    fun isLicenseNumberExists(licenseNumber: String): Boolean =
        DriverRepo.existsByLicense(licenseNumber)

    fun createDriver(name: String, phone: String, email: String, password: String, location: Location, licenseNumber: String, cab: Cab): Driver {

        val driver = Driver(
            name = name,
            phone = phone,
            email = email,
            cabId = cab.cabId,
            licenseNumber = licenseNumber,
            currentLocation = location
        )

        try {

            DriverRepo.save(driver)
            AuthService.saveUserAndCredentials(
                driver, password
            )

            return driver

        }
        catch (e: IllegalArgumentException) {
            DriverRepo.deleteByKey(driver.userId)
            UserRepo.deleteByEmail(driver.email)
            CabService.deleteCab(cab.cabId)   // Remove the cab so that no cab is left without a driver
            throw e
        }
    }


    fun deleteDriver(driver: Driver): Boolean {

        if (RideRepo.hasCurrentRideOfDriver(driver.userId)) {
            return false
        }

        CabService.deleteCab(driver.cabId)
        DriverRepo.deleteByKey(driver.userId)
        UserRepo.deleteByEmail(driver.email)
        AuthRepo.deleteByKey(driver.userId)

        return true
    }


    fun updateProfile(
        driver: Driver,
        name: String,
        phone: String,
        location: Location
    ) {
        driver.updateName(name)
        driver.updatePhone(phone)
        driver.updateCurrentLocation(location)
    }

    fun completeRide(
        ride: Ride,
        driver: Driver
    ) {
        endRide(ride, driver){ ride -> markRideAsCompleted(ride)}
        driver.updateCurrentLocation(ride.dropLocation)
        addEarnings(driver,ride.fare)
    }

    fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {
       endRide(ride, driver){ ride -> markRideAsCancelled(ride)}
    }

    private fun endRide(
        ride: Ride,
        driver : Driver,
        action: (Ride) -> Unit
    ){
        validateRideOwnership(ride, driver)
        action(ride)
        markAvailable(driver)
    }

    private fun markAvailable(driver: Driver) {
        driver.setAvailability(true)
    }

    private fun markRideAsCompleted(ride: Ride){
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw InvalidRideStateException("Only booked rides can be completed.")
        }

        ride.updateRideStatus(RideStatus.COMPLETED)
        ride.setCompletedAt(LocalDateTime.now())
    }

    private fun markRideAsCancelled(ride: Ride){
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw InvalidRideStateException("Only booked rides can be cancelled.")
        }

        ride.updateRideStatus(RideStatus.CANCELLED)
        ride.setCancelledAt(LocalDateTime.now())
    }

    private fun addEarnings(driver: Driver, amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Amount must be greater than zero." }
        driver.updateEarnings(driver.earnings + amount)
    }

    private fun validateRideOwnership(
        ride: Ride,
        driver: Driver
    ) {

        if (ride.driverId != driver.userId) {
            throw UnauthorizedRideActionException("Only the assigned driver can perform this action.")
        }
    }

    fun getAverageRatingOfDriver(driver: Driver) : Double{
        return if (driver.ratingCount == 0){
            0.0
        } else {
            driver.totalRating.toDouble() / driver.ratingCount
        }
    }

    fun getAllDrivers(): List<Driver> =
        DriverRepo.findAll()

    fun getAvailableDrivers(): List<Driver> =
        DriverRepo.findAvailableDrivers()

    fun getUnavailableDrivers(): List<Driver> =
        DriverRepo.findUnavailableDrivers()

    fun findDriverById(driverId: String): Driver =
        DriverRepo.findByKey(driverId) ?: throw DriverNotFoundException("Driver not found for ID: $driverId")

}