package cab_booking.service

import cab_booking.exception.InvalidDispatchStateException
import cab_booking.model.Ride
import cab_booking.model.types.DispatchStatus
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.RideRepo
import cab_booking.service.pricing.RideFareCalculator
import java.math.BigDecimal
import java.time.LocalDateTime

object RideService {

    fun createRide(riderId: String, driverId: String, pickupLocation: Location, dropLocation: Location, vehicleCategory: VehicleCategory) : Ride{
        val ride = Ride(
            customerId = riderId,
            driverId = driverId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            fare = RideFareCalculator.calculateBaseFare(vehicleCategory, pickupLocation, dropLocation, LocalDateTime.now())
        )

        RideRepo.save(ride)
        return ride
    }
    fun estimateRideFares(pickupLocation: Location, dropLocation: Location): Map<VehicleCategory, BigDecimal> {
        val map = mutableMapOf<VehicleCategory, BigDecimal>()
        VehicleCategory.entries.forEach {
            map[it] = RideFareCalculator.calculateBaseFare(it, pickupLocation, dropLocation, LocalDateTime.now())
        }
        return map
    }


    fun markAsStarted(ride: Ride) {
        if (ride.status != DispatchStatus.BOOKED) {
            throw InvalidDispatchStateException("Only booked rides can be started.")
        }

        ride.updateStatus(DispatchStatus.STARTED)
    }

    fun markAsCompleted(ride: Ride) {
        if (ride.status != DispatchStatus.STARTED) {
            throw InvalidDispatchStateException("Only started rides can be completed.")
        }

        ride.updateStatus(DispatchStatus.COMPLETED)
        ride.setCompletedAt(LocalDateTime.now())
    }

    fun markAsCancelled(ride: Ride) {
        if (ride.status != DispatchStatus.BOOKED) {
            throw InvalidDispatchStateException("Only booked rides can be cancelled.")
        }

        ride.updateStatus(DispatchStatus.CANCELLED)
        ride.setCancelledAt(LocalDateTime.now())
    }

    fun rateRide(ride: Ride, rating: Int) {
        if (ride.ratings != 0) {
            throw InvalidDispatchStateException("This ride has already been rated.")
        }

        ride.setRatings(rating)
    }

    fun hasActiveRideForDriver(driverId: String): Boolean =
        RideRepo.hasActiveRideOfDriver(driverId)

    fun hasActiveRideForRider(riderId: String): Boolean =
        RideRepo.hasActiveRideOfRider(riderId)

    fun getCurrentRideOfDriver(driverId: String): Ride =
        RideRepo.findCurrentRideOfDriver(driverId)

    fun getCurrentRideOfRider(riderId: String): Ride =
        RideRepo.findCurrentRideOfRider(riderId)

    fun getLastCompletedRide(
        riderId: String
    ): Ride =
        RideRepo.findLastCompletedRide(riderId)
}