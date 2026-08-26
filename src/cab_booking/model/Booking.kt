package cab_booking.model

import cab_booking.util.toDisplayString
import cab_booking.exception.InvalidBookingStateException
import cab_booking.model.types.Location
import cab_booking.model.types.BookingStatus
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime


//  Represents any service booked by a customer (a ride or a parcel delivery).
//  Holds the data and rules common to every kind of booking: who booked it,
//  where it goes, how much it costs, and its current status (booked, started,
//  completed, cancelled). Individual booking types (Ride, ParcelDelivery) add
//  only the extra details specific to them.
sealed class Booking(
    val customerId: String,
    val driverId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val fare: BigDecimal,
    val bookedAt: LocalDateTime = LocalDateTime.now(),
    val bookingId: String = IdGenerator.generateBookingId()
) {

    var cancelledAt: LocalDateTime? = null
        private set

    var completedAt: LocalDateTime? = null
        private set

    var status: BookingStatus = BookingStatus.BOOKED
        private set

    fun setCancelledAt(cancelledAt: LocalDateTime) {
        this.cancelledAt = cancelledAt
    }

    fun setCompletedAt(completedAt: LocalDateTime){
        this.completedAt = completedAt
    }

    fun updateStatus(newStatus: BookingStatus) {
        //INVALID CONDITIONS CHECK
        if (
        // COMPLETED OR CANCELLED - NO FURTHER STATUS CHANGES
            (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) ||

            // BOOKED - CAN ONLY BECOME ONGOING OR CANCELLED
            (status == BookingStatus.BOOKED && newStatus != BookingStatus.STARTED && newStatus != BookingStatus.CANCELLED) ||

            // ONGOING - CAN ONLY BECOME COMPLETED
            (status == BookingStatus.STARTED && newStatus != BookingStatus.COMPLETED)
        ) {
            throw InvalidBookingStateException("Booking status cannot be changed from $status to $newStatus.")
        }

        status = newStatus
    }

    var rating: Int = 0 // 1–5, 0 if not yet rated
        private set

    fun setRating(rating: Int){
        if(status != BookingStatus.COMPLETED) {
            throw InvalidBookingStateException("Only completed rides can be rated.")
        }

        require(rating in 1..5) { "Rating must be between 1 and 5." }

        this.rating = rating
    }

    init {
        require(customerId.isNotBlank()) { "Customer ID cannot be blank." }

        require(driverId.isNotBlank()) { "Driver ID cannot be blank." }

        require(pickupLocation != dropLocation) { "Pickup and drop locations cannot be the same." }

        require(fare > BigDecimal.ZERO) { "Fare must be greater than zero." }
    }

    override fun toString(): String {
        return """
            Pickup Location  : $pickupLocation
            Drop Location    : $dropLocation
            Fare             : ₹$fare
            Status           : $status
            Booked At        : ${bookedAt.toDisplayString()}
            Rating           : ${if (rating == 0) "Not rated yet" else "$rating/5"}
            Completed At     : ${completedAt?.toDisplayString() ?: "-"}
            Cancelled At     : ${cancelledAt?.toDisplayString() ?: "-"}
        """.trimIndent()
    }
}