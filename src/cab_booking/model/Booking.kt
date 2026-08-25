package cab_booking.model

import cab_booking.util.toDisplayString
import cab_booking.exception.InvalidDispatchStateException
import cab_booking.model.types.Location
import cab_booking.model.types.DispatchStatus
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

// Abstract: a "dispatch" alone isn't a real bookable thing — it's always
// either a ride or a parcel delivery. Making this abstract stops one from
// ever being created generically.
// Not an enum on one concrete class, because ParcelDelivery needs extra
// mandatory fields (receiver name/phone) that a plain type flag can't
// enforce — subclassing lets the compiler guarantee they're always present.
abstract class Booking(
    val customerId: String,
    val driverId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val fare: BigDecimal,
    // Defaults to "right now" for a new booking. The file storage passes the saved
    // time so a reloaded booking keeps the moment it was actually booked.
    val bookedAt: LocalDateTime = LocalDateTime.now(),
    val bookingId: String = IdGenerator.generateBookingId()
) {

    var cancelledAt: LocalDateTime? = null
        private set

    var completedAt: LocalDateTime? = null
        private set

    var status: DispatchStatus = DispatchStatus.BOOKED
        private set

    fun setCancelledAt(cancelledAt: LocalDateTime) {
        this.cancelledAt = cancelledAt
    }

    fun setCompletedAt(completedAt: LocalDateTime){
        this.completedAt = completedAt
    }

    fun updateStatus(newStatus: DispatchStatus) {
        //INVALID CONDITIONS CHECK
        if (
        // COMPLETED OR CANCELLED - NO FURTHER STATUS CHANGES
            (status == DispatchStatus.COMPLETED || status == DispatchStatus.CANCELLED) ||

            // BOOKED - CAN ONLY BECOME ONGOING OR CANCELLED
            (status == DispatchStatus.BOOKED && newStatus != DispatchStatus.STARTED && newStatus != DispatchStatus.CANCELLED) ||

            // ONGOING - CAN ONLY BECOME COMPLETED
            (status == DispatchStatus.STARTED && newStatus != DispatchStatus.COMPLETED)
        ) {
            throw InvalidDispatchStateException("Ride status cannot be changed from $status to $newStatus.")
        }

        status = newStatus
    }

    var rating: Int = 0 // 1–5, 0 if not yet rated
        private set

    fun setRating(rating: Int){
        if(status != DispatchStatus.COMPLETED) {
            throw InvalidDispatchStateException("Only completed rides can be rated.")
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
            Booked At        : $bookedAt
            Completed At     : ${completedAt?.toDisplayString() ?: "-"}
            Cancelled At     : ${cancelledAt?.toDisplayString() ?: "-"}
        """.trimIndent()
    }
}