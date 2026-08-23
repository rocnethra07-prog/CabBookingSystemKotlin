package cab_booking.service

import cab_booking.model.Ride
import cab_booking.repository.RideRepo

object RideService {

    fun hasActiveRideForRider(riderId: String): Boolean =
        RideRepo.hasActiveRideOfRider(riderId)

    fun hasActiveRideForDriver(driverId: String): Boolean =
        RideRepo.hasActiveRideOfDriver(driverId)

    fun getCurrentRide(driverId: String): Ride =
        RideRepo.findCurrentRideOfDriver(driverId)

    fun getCurrentBookedRide(riderId: String): Ride =
        RideRepo.findCurrentRideOfRider(riderId)

    fun getLastCompletedRide(
        riderId: String
    ): Ride =
        RideRepo.findLastCompletedRide(riderId)
}