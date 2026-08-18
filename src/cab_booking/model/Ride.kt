package cab_booking.model

import cab_booking.exception.InvalidRideStateException
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

class Ride(
    val riderId: String,
    val driverId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val fare: BigDecimal
) {

    // Public properties are used for simple property access
    val rideId: String = IdGenerator.generateRideId()

    var rideStatus: RideStatus = RideStatus.BOOKED
        private set

    val bookedAt: LocalDateTime = LocalDateTime.now()

    var completedAt: LocalDateTime? = null //null by default
        private set

    var cancelledAt: LocalDateTime? = null //null by default
        private set

    var rating: Int = 0  // 1–5, 0 if not yet rated
        private set

    fun updateRideStatus(rideStatus: RideStatus){
        if ((rideStatus == RideStatus.CANCELLED || rideStatus == RideStatus.COMPLETED) &&
            this.rideStatus != RideStatus.BOOKED
        ) {
            throw InvalidRideStateException("Only booked rides can be completed or cancelled.")
        }
        if (rideStatus == RideStatus.BOOKED &&
            (this.rideStatus == RideStatus.CANCELLED || this.rideStatus == RideStatus.COMPLETED)
        ) {
            throw InvalidRideStateException("Completed or cancelled rides cannot be booked again.")
        }
        this.rideStatus = rideStatus
    }

    fun setCompletedAt(completedAt: LocalDateTime){
        this.completedAt = completedAt
    }

    fun setCancelledAt(cancelledAt: LocalDateTime){
        this.cancelledAt = cancelledAt
    }

    fun setRatings(rating: Int){
        if(rideStatus != RideStatus.COMPLETED) {
            throw InvalidRideStateException("Only completed rides can be rated.")
        }

        require(rating in 1..5) { "Rating must be between 1 and 5." }

        this.rating = rating
    }

    init {
        require(riderId.isNotBlank()) { "Rider ID cannot be blank." }

        require(driverId.isNotBlank()) { "Driver ID cannot be blank." }

        require(pickupLocation != dropLocation) { "Pickup and drop locations cannot be the same." }

        require(fare > BigDecimal.ZERO) { "Fare must be greater than zero." }
    }

    override fun toString(): String {
        return """
            Pickup Location  : $pickupLocation
            Drop Location    : $dropLocation
            Fare             : ₹$fare
            Status           : $rideStatus
            Booked At        : $bookedAt
            Completed At     : ${completedAt ?: "-"}
            Cancelled At     : ${cancelledAt ?: "-"}
            Rating           : ${if(rating == 0) "-" else rating}
        """.trimIndent()
    }
}