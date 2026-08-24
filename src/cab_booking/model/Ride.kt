package cab_booking.model

import cab_booking.exception.InvalidRideStateException
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import cab_booking.model.types.VehicleCategory
import cab_booking.util.IdGenerator
import cab_booking.console.input.toDisplayString
import java.math.BigDecimal
import java.time.LocalDateTime

class Ride(
    riderId: String,
    driverId: String,
    pickupLocation: Location,
    dropLocation: Location,
    fare: BigDecimal
) : Booking(riderId, driverId, pickupLocation, dropLocation, fare) {

    // Public properties are used for simple property access
    val rideId: String = IdGenerator.generateRideId()

    var rideStatus: RideStatus = RideStatus.BOOKED
        private set

    var completedAt: LocalDateTime? = null //null by default
        private set

    var rating: Int = 0  // 1–5, 0 if not yet rated
        private set

    fun updateRideStatus(newStatus: RideStatus) {
        //INVALID CONDITIONS CHECK
        if (
            // COMPLETED OR CANCELLED - NO FURTHER STATUS CHANGES
            (rideStatus == RideStatus.COMPLETED || rideStatus == RideStatus.CANCELLED) ||

            // BOOKED - CAN ONLY BECOME ONGOING OR CANCELLED
            (rideStatus == RideStatus.BOOKED && newStatus != RideStatus.STARTED && newStatus != RideStatus.CANCELLED) ||

            // ONGOING - CAN ONLY BECOME COMPLETED
            (rideStatus == RideStatus.STARTED && newStatus != RideStatus.COMPLETED)
        ) {
            throw InvalidRideStateException("Ride status cannot be changed from $rideStatus to $newStatus.")
        }

        rideStatus = newStatus
    }

    fun setCompletedAt(completedAt: LocalDateTime) {
        this.completedAt = completedAt
    }

    fun setRatings(rating: Int){
        if(rideStatus != RideStatus.COMPLETED) {
            throw InvalidRideStateException("Only completed rides can be rated.")
        }

        require(rating in 1..5) { "Rating must be between 1 and 5." }

        this.rating = rating
    }

    override fun toString(): String {
        return """
            Pickup Location  : $pickupLocation
            Drop Location    : $dropLocation
            Fare             : ₹$fare
            Status           : $rideStatus
            Booked At        : ${bookedAt.toDisplayString()}
            Completed At     : ${completedAt?.toDisplayString() ?: "-"}
            Cancelled At     : ${cancelledAt?.toDisplayString() ?: "-"}
            Rating           : ${if(rating == 0) "-" else rating}
        """.trimIndent()
    }
}