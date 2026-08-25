package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime


// A booking where the driver carries a passenger from pickup to drop location.
// Everything about tracking (status, fare, cancellation, rating) is inherited
// from Booking — this class doesn't need to add anything extra.

class Ride(
    customerId: String,
    driverId: String,
    pickupLocation: Location,
    dropLocation: Location,
    fare: BigDecimal,
    // Both are left out when a ride is booked; passed by the file storage at startup.
    bookedAt: LocalDateTime = LocalDateTime.now(),
    bookingId: String = IdGenerator.generateBookingId()
) : Booking(customerId, driverId, pickupLocation, dropLocation, fare, bookedAt,  bookingId)