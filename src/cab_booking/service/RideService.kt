package cab_booking.service

import cab_booking.model.Ride
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.RideRepo
import cab_booking.service.pricing.RideFareCalculator
import java.time.LocalDateTime

object RideService {

    fun createRide(riderId: String, driverId: String, pickupLocation: Location, dropLocation: Location, vehicleCategory: VehicleCategory) : Ride{
        val ride = Ride(
            riderId = riderId,
            driverId = driverId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            vehicleCategory = vehicleCategory,
            fare = RideFareCalculator.calculateRideFare(vehicleCategory, pickupLocation, dropLocation, LocalDateTime.now())
        )

        RideRepo.save(ride)
        return ride
    }
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