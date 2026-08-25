package cab_booking.service

import cab_booking.exception.InvalidBookingStateException
import cab_booking.model.Ride
import cab_booking.model.types.BookingStatus
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
        if (ride.status != BookingStatus.BOOKED) {
            throw InvalidBookingStateException("Only booked rides can be started.")
        }

        ride.updateStatus(BookingStatus.STARTED)
    }

    fun markAsCompleted(ride: Ride) {
        if (ride.status != BookingStatus.STARTED) {
            throw InvalidBookingStateException("Only started rides can be completed.")
        }

        ride.updateStatus(BookingStatus.COMPLETED)
        ride.setCompletedAt(LocalDateTime.now())
    }

    fun markAsCancelled(ride: Ride) {
        if (ride.status != BookingStatus.BOOKED) {
            throw InvalidBookingStateException("Only booked rides can be cancelled.")
        }

        ride.updateStatus(BookingStatus.CANCELLED)
        ride.setCancelledAt(LocalDateTime.now())
    }

    fun rateRide(ride: Ride, rating: Int) {
        if (ride.rating != 0) {
            throw InvalidBookingStateException("This ride has already been rated.")
        }

        ride.setRating(rating)
    }

    fun hasActiveRideOfDriver(driverId: String): Boolean =
        RideRepo.hasActiveRideOfDriver(driverId)

    fun hasActiveRideOfRider(riderId: String): Boolean =
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