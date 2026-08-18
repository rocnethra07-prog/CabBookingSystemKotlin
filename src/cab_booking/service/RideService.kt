package cab_booking.service

import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.model.User
import cab_booking.model.types.RideStatus
import cab_booking.repository.RideRepo

object RideService {
    fun getDriverRideHistory(driverId: String): List<Ride> =
        RideRepo.findRidesByDriver(driverId)

    fun getRiderRideHistory(riderId: String): List<Ride> =
        RideRepo.findRidesByRider(riderId)

    fun getAllRides(): List<Ride> = RideRepo.findAll()

    fun getRidesByStatus(status: RideStatus): List<Ride> =
        RideRepo.findRidesByStatus(status)

    fun getActiveRides(): List<Ride> =
        getRidesByStatus(RideStatus.BOOKED)

    fun getCompletedRides(): List<Ride> =
        getRidesByStatus(RideStatus.COMPLETED)

    fun getCancelledRides(): List<Ride> =
        getRidesByStatus(RideStatus.CANCELLED)

    fun getRidesByDriver(
        driver: Driver
    ): List<Ride> =
        RideRepo.findRidesByDriver(driver.userId)

    fun getRidesByRider(
        rider: User
    ): List<Ride> =
        RideRepo.findRidesByRider(rider.userId)

    fun hasActiveRide(user: User): Boolean =
        RideRepo.hasCurrentRideOfRider(user.userId)

    fun getCurrentRide(driver: Driver): Ride =
        RideRepo.findCurrentRideOfDriver(driver.userId)

    fun getCurrentBookedRide(user: User): Ride =
        RideRepo.findCurrentRideOfRider(user.userId)

    fun getLastCompletedRide(
        rider: User
    ): Ride =
        RideRepo.findLastCompletedRide(rider.userId)
}