package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.util.IdGenerator
import cab_booking.util.Validator
import java.math.BigDecimal
import java.time.LocalDateTime



// A booking where the driver delivers a parcel instead of carrying a passenger.
// Everything about tracking (status, fare, cancellation, rating) is inherited
// from Booking — this class only adds who receives the parcel.
class ParcelDelivery(
    customerId: String,
    driverId: String,
    pickupLocation: Location,
    dropLocation: Location,
    fare: BigDecimal,
    val receiverName: String,
    val receiverPhoneNumber: String,
    bookedAt: LocalDateTime = LocalDateTime.now(),
    bookingId: String = IdGenerator.generateBookingId()
) : Booking(customerId, driverId, pickupLocation, dropLocation, fare, bookedAt, bookingId) {

    init {
        require(Validator.isValidName(receiverName)) { "Contact name must contain minimum 3 characters. Contact name cannot be blank." }
        require(Validator.isValidPhoneNumber(receiverPhoneNumber)) { "Invalid contact phone number format." }
    }

}