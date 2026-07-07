package cab_booking.model

import cab_booking.exception.CabBookingException
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
                throw CabBookingException("Only booked rides can be completed or cancelled.")
            }
            if (value == RideStatus.BOOKED &&
                (field == RideStatus.CANCELLED || field == RideStatus.COMPLETED)
            ) {
                throw CabBookingException("Completed or cancelled rides cannot be booked again.")
            }
            field = value
        }

    val bookedAt: LocalDateTime = LocalDateTime.now()

    var completedAt: LocalDateTime? = null //null by default

    var cancelledAt: LocalDateTime? = null //null by default

    var rating: Int? = null  // 1–5, null if not yet rated
        set(value) {
            if(rideStatus != RideStatus.COMPLETED) {
                throw CabBookingException("Only completed rides can be rated.")
            }

            if(field != null) {
                throw CabBookingException("Ride has already been rated.")
            }

            if(value !in 1..5) {
                throw CabBookingException("Rating must be between 1 and 5.")
            }

            field = value
        }

    init {
        if(riderId.isBlank()) {
            throw CabBookingException("Rider ID cannot be blank.")
        }

        if(driverId.isBlank()) {
            throw CabBookingException("Driver ID cannot be blank.")
        }

        if(pickupLocation == dropLocation) {
            throw CabBookingException("Pickup and drop locations cannot be the same.")
        }

        if(fare <= BigDecimal.ZERO) {
            throw CabBookingException("Fare must be greater than zero.")
        }
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