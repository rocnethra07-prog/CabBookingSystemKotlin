package cab_booking.service

import cab_booking.exception.CabBookingException
import cab_booking.model.Driver
import cab_booking.model.Location
import cab_booking.model.Ride
import cab_booking.repository.DriverRepo
import cab_booking.repository.RideRepo

class DriverService {

    fun updateProfile(
        driver: Driver,
        name: String,
        phone: String,
        location: Location
    ) {
        driver.name = name
        driver.phone = phone
        driver.updateLocation(location)
    }

    fun getCurrentRide(driver: Driver): Ride? =
        RideRepo.findCurrentRideOfDriver(driver.userId)

    fun completeRide(
        ride: Ride,
        driver: Driver
    ) {

        validateRideOwnership(ride, driver)

        ride.completeRide()

        driver.updateLocation(ride.dropLocation)

        driver.addEarnings(ride.fare)

        driver.markAvailable()
    }

    fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {

        validateRideOwnership(ride, driver)

        ride.cancelRide()

        driver.markAvailable()
    }

    private fun validateRideOwnership(
        ride: Ride,
        driver: Driver
    ) {

        if (ride.driverId != driver.userId) {
            throw CabBookingException(
                "Only the assigned driver can perform this action."
            )
        }
    }
    fun getRidesByDriver(
        driver: Driver
    ): List<Ride> =
        RideRepo.findRidesByRider(driver.userId)

    fun findDriverById(
        driverId: String
    ): Driver =
        DriverRepo.findByKey(driverId)
}