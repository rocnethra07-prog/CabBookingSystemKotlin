package cab_booking.model

import cab_booking.exception.InvalidRideStateException
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import cab_booking.model.types.VehicleCategory
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

abstract class Booking(
    val customerId: String,
    val driverId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val fare: BigDecimal
) {

    val bookedAt: LocalDateTime = LocalDateTime.now()

    var cancelledAt: LocalDateTime? = null
        private set

    var completedAt: LocalDateTime? = null
        private set

    var status: RideStatus = RideStatus.BOOKED
        private set

    var ratings: Int = 0 // 1–5, 0 if not yet rated
        private set

    fun setRatings(rating: Int){
        if(status != RideStatus.COMPLETED) {
            throw InvalidRideStateException("Only completed rides can be rated.")
        }

        require(rating in 1..5) { "Rating must be between 1 and 5." }

        this.ratings = rating
    }
    fun setCancelledAt(cancelledAt: LocalDateTime) {
        this.cancelledAt = cancelledAt
    }

    fun setCompletedAt(completedAt: LocalDateTime){
        this.completedAt = completedAt
    }

    fun updateStatus(newStatus: RideStatus) {
        //INVALID CONDITIONS CHECK
        if (
        // COMPLETED OR CANCELLED - NO FURTHER STATUS CHANGES
            (status == RideStatus.COMPLETED || status == RideStatus.CANCELLED) ||

            // BOOKED - CAN ONLY BECOME ONGOING OR CANCELLED
            (status == RideStatus.BOOKED && newStatus != RideStatus.STARTED && newStatus != RideStatus.CANCELLED) ||

            // ONGOING - CAN ONLY BECOME COMPLETED
            (status == RideStatus.STARTED && newStatus != RideStatus.COMPLETED)
        ) {
            throw InvalidRideStateException("Ride status cannot be changed from $status to $newStatus.")
        }

        status = newStatus
    }

    init {
        require(customerId.isNotBlank()) { "Customer ID cannot be blank." }

        require(driverId.isNotBlank()) { "Driver ID cannot be blank." }

        require(pickupLocation != dropLocation) { "Pickup and drop locations cannot be the same." }

        require(fare > BigDecimal.ZERO) { "Fare must be greater than zero." }
    }
}