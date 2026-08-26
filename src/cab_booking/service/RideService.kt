package cab_booking.service

import cab_booking.exception.InvalidBookingStateException
import cab_booking.model.Ride
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.RideRepo
import cab_booking.service.pricing.RideFareCalculator
import java.math.BigDecimal
import java.time.LocalDateTime

object RideService {

    fun createRide(customerId: String, driverId: String, pickupLocation: Location, dropLocation: Location, vehicleCategory: VehicleCategory) : Ride{
        val ride = Ride(
            customerId = customerId,
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


    fun startRide(ride: Ride) =
        BookingService.start(ride, "started")

    fun completeRide(ride: Ride) =
        BookingService.complete(ride, "completed")

    fun cancelRide(ride: Ride) =
        BookingService.cancel(ride, "ride")


    fun rateRide(ride: Ride, rating: Int) {
        if (ride.rating != 0) {
            throw InvalidBookingStateException("This ride has already been rated.")
        }

        ride.setRating(rating)
    }

    fun hasActiveRideOfDriver(driverId: String): Boolean =
        RideRepo.hasActiveRideOfDriver(driverId)

    fun hasActiveRideOfCustomer(customerId: String): Boolean =
        RideRepo.hasActiveRideOfCustomer(customerId)

    fun getCurrentRideOfDriver(driverId: String): Ride =
        RideRepo.findCurrentRideOfDriver(driverId)

    fun getCurrentRideOfCustomer(customerId: String): Ride =
        RideRepo.findCurrentRideOfCustomer(customerId)

    fun getLastCompletedRideOfCustomer(
        customerId: String
    ): Ride =
        RideRepo.findLastCompletedRideOfCustomer(customerId)
}