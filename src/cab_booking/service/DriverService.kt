package cab_booking.service

import exception.DriverNotFoundException
import cab_booking.model.Driver
import cab_booking.model.types.Location
import cab_booking.model.Ride
import cab_booking.model.types.RideStatus
import cab_booking.repository.DriverRepo
import cab_booking.repository.RideRepo
import exception.InvalidRideStateException
import exception.UnauthorizedRideActionException
import java.time.LocalDateTime

class DriverService {

    fun updateProfile(
        driver: Driver,
        name: String,
        phone: String,
        location: Location
    ) {
        driver.name = name
        driver.phone = phone
        driver.currentLocation = location
    }

    fun getCurrentRide(driver: Driver): Ride? =
        RideRepo.findCurrentRideOfDriver(driver.userId)

    fun completeRide(
        ride: Ride,
        driver: Driver
    ) {
        endRide(ride, driver){ ride -> markRideAsCompleted(ride)}
        driver.currentLocation = ride.dropLocation
        driver.addEarnings(ride.fare)
    }

    fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {
       endRide(ride, driver){ ride -> markRideAsCancelled(ride)}
    }

    private fun markAvailable(driver: Driver){
        driver.isAvailable = true
    }

    private fun markRideAsCompleted(ride: Ride){
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw InvalidRideStateException("Only booked rides can be completed.")
        }

        ride.rideStatus = RideStatus.COMPLETED
        ride.completedAt = LocalDateTime.now()
    }

    private fun endRide(ride: Ride, driver : Driver, action: (Ride) -> Unit ){
        validateRideOwnership(ride, driver)
        action(ride)
        markAvailable(driver)
    }

    private fun markRideAsCancelled(ride: Ride){
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw InvalidRideStateException("Only booked rides can be cancelled.")
        }

        ride.rideStatus = RideStatus.CANCELLED
        ride.cancelledAt = LocalDateTime.now()
    }

    private fun validateRideOwnership(
        ride: Ride,
        driver: Driver
    ) {

        if (ride.driverId != driver.userId) {
            throw UnauthorizedRideActionException("Only the assigned driver can perform this action.")
        }
    }
    fun getRidesByDriver(
        driver: Driver
    ): List<Ride> =
        RideRepo.findRidesByDriver(driver.userId)

    //method used for routing the User object -> DriverController
    fun findDriverById(
        driverId: String
    ): Driver {
        return DriverRepo.findByKey(driverId) ?: throw DriverNotFoundException("Driver not found: $driverId")
    }

}