package cab_booking.service

import cab_booking.model.Ride
import cab_booking.repository.RideRepo

object RideService {

    fun hasActiveRide(riderId: String): Boolean =
        RideRepo.hasCurrentRideOfRider(riderId)

    fun getCurrentRide(driverId: String): Ride =
        RideRepo.findCurrentRideOfDriver(driverId)

    fun getCurrentBookedRide(riderId: String): Ride =
        RideRepo.findCurrentRideOfRider(riderId)

    fun getLastCompletedRide(
        riderId: String
    ): Ride =
        RideRepo.findLastCompletedRide(riderId)
}