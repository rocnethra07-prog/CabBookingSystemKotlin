package cab_booking.model

import cab_booking.exception.InvalidRideStateException
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

data class Ride(
    val riderId: String,
    val driverId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val fare: BigDecimal
) {

    // Public properties are used for simple property access
    val rideId: String = IdGenerator.generateRideId()

    var rideStatus: RideStatus = RideStatus.BOOKED
        set(value) {
            if(field == value) return
            if ((value == RideStatus.CANCELLED || value == RideStatus.COMPLETED) &&
                field != RideStatus.BOOKED
            ) {
                throw InvalidRideStateException("Only booked rides can be completed or cancelled.")
            }
            if (value == RideStatus.BOOKED &&
                (field == RideStatus.CANCELLED || field == RideStatus.COMPLETED)
            ) {
                throw InvalidRideStateException("Completed or cancelled rides cannot be booked again.")
            }
            field = value
        }

    val bookedAt: LocalDateTime = LocalDateTime.now()

    var completedAt: LocalDateTime? = null //null by default

    var cancelledAt: LocalDateTime? = null //null by default

    var rating: Int? = null  // 1–5, null if not yet rated
        set(value) {
            if(rideStatus != RideStatus.COMPLETED) {
                throw InvalidRideStateException("Only completed rides can be rated.")
            }

            if(field != null) {
                throw InvalidRideStateException("Ride has already been rated.")
            }

            require(value in 1..5) { "Rating must be between 1 and 5." }

            field = value
        }

    init {
        require(riderId.isNotBlank()) { "Rider ID cannot be blank." }

        require(driverId.isNotBlank()) { "Driver ID cannot be blank." }

        require(pickupLocation != dropLocation) { "Pickup and drop locations cannot be the same." }

        require(fare > BigDecimal.ZERO) { "Fare must be greater than zero." }
    }

    override fun toString(): String {
        return """
            Ride ID          : $rideId
            Rider ID         : $riderId
            Driver ID        : $driverId
            Pickup Location  : $pickupLocation
            Drop Location    : $dropLocation
            Fare             : ₹$fare
            Status           : $rideStatus
            Booked At        : $bookedAt
            Completed At     : ${completedAt ?: "-"}
            Cancelled At     : ${cancelledAt ?: "-"}
            Rating           : ${rating ?: "-"}
        """.trimIndent()
    }
}