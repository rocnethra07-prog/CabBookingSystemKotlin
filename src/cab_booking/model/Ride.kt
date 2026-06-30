package cab_booking.model

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

    val rideId: String = IdGenerator.generateRideId()

    var rideStatus: RideStatus = RideStatus.BOOKED
        private set

    val bookedAt: LocalDateTime = LocalDateTime.now()

    var completedAt: LocalDateTime? = null //null by default
        private set

    var cancelledAt: LocalDateTime? = null //null by default
        private set

    var rating: Int? = null  // 1–5, null if not yet rated
        private set

    init {
        require(riderId.isNotBlank()) {
            "Rider ID cannot be blank."
        }

        require(driverId.isNotBlank()) {
            "Driver ID cannot be blank."
        }

        require(pickupLocation != dropLocation) {
            "Pickup and drop locations cannot be the same."
        }

        require(fare > BigDecimal.ZERO) {
            "Fare must be greater than zero."
        }
    }

    fun completeRide() {
        require(rideStatus == RideStatus.BOOKED) {
            "Only booked rides can be completed."
        }

        rideStatus = RideStatus.COMPLETED
        completedAt = LocalDateTime.now()
    }

    fun cancelRide() {
        require(rideStatus == RideStatus.BOOKED) {
            "Only booked rides can be cancelled."
        }

        rideStatus = RideStatus.CANCELLED
        cancelledAt = LocalDateTime.now()
    }

    fun addRating(rating: Int) {
        require(rideStatus == RideStatus.COMPLETED) {
            "Only completed rides can be rated."
        }

        require(this.rating == null) {
            "Ride has already been rated."
        }

        require(rating in 1..5) {
            "Rating must be between 1 and 5."
        }

        this.rating = rating
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