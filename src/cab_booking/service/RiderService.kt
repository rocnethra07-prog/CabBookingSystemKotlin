package cab_booking.service

import cab_booking.exception.CabBookingException
import cab_booking.model.*
import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import cab_booking.repository.CabRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.RideRepo
import java.math.BigDecimal
import java.time.LocalDateTime

class RiderService {

    fun updateProfile(
        user: User,
        name: String,
        phone: String
    ) {
        user.name = name
        user.phone = phone
    }

    fun bookRide(
        rider: User,
        pickupLocation: Location,
        dropLocation: Location,
        cabType: CabType
    ): Ride {

        val driver = findAvailableDriver(cabType, pickupLocation)
            ?: throw CabBookingException(
                "No $cabType drivers are available right now."
            )

        val ride = Ride(
            riderId = rider.userId,
            driverId = driver.userId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            fare = calculateFare(cabType)
        )

        RideRepo.save(ride)

        markUnavailable(driver)

        return ride
    }

    private fun findAvailableDriver(
        cabType: CabType,
        pickupLocation: Location
    ): Driver? {

        val matchingDrivers = DriverRepo
            .findAvailableDrivers()
            .filter { driver ->

                val cab = CabRepo.findByKey(driver.cabId)

                cab.cabType == cabType
            }

        if (matchingDrivers.isEmpty()) {
            return null
        }

        return matchingDrivers.firstOrNull {
            it.currentLocation == pickupLocation
        } ?: matchingDrivers.first()
    }

    fun getDriverForRide(ride: Ride): Driver =
        DriverRepo.findByKey(ride.driverId)

    fun hasActiveRide(user: User): Boolean =
        getCurrentBookedRide(user) != null

    private fun calculateFare(cabType: CabType): BigDecimal =
        cabType.basePay

    fun getCurrentBookedRide(user: User): Ride? =
        RideRepo.findCurrentRideOfRider(user.userId)
    fun cancelRide(
        ride: Ride,
        rider: User
    ) {

        if (ride.riderId != rider.userId) {
            throw CabBookingException(
                "Only the rider who booked this ride can cancel it."
            )
        }

        cancelRide(ride)

        markDriverAvailable(ride)
    }

    private fun cancelRide(ride: Ride) {
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw CabBookingException("Only booked rides can be cancelled.")
        }

        ride.rideStatus = RideStatus.CANCELLED
        ride.cancelledAt = LocalDateTime.now()
    }

    private fun markDriverAvailable(ride: Ride) {
        val driver = getDriverForRide(ride)
        markAvailable(driver)
    }

    private fun markAvailable(driver: Driver){
        driver.isAvailable = true
    }

    private fun markUnavailable(driver: Driver){
        driver.isAvailable = false
    }

    fun getRidesByRider(
        rider: User
    ): List<Ride> =
        RideRepo.findRidesByRider(rider.userId)

    fun getLastCompletedRide(
        rider: User
    ): Ride? =
        RideRepo.findLastCompletedRide(rider.userId)

    fun rateDriver(
        ride: Ride,
        rider: User,
        rating: Int
    ) {

        if (ride.riderId != rider.userId) {
            throw CabBookingException(
                "Only the rider who booked this ride can rate it."
            )
        }

        ride.rating = rating

        val driver = DriverRepo.findByKey(ride.driverId)

        driver.addRating(rating)
    }
}