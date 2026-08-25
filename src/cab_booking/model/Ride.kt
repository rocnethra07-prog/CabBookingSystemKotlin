package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

// Empty today — Ride needs no fields Dispatch doesn't already have.
// It still exists as a class, not an enum tag on Dispatch
//
// Dispatch is abstract, so it can never be instantiated on its own.
// Something concrete has to stand for "just a ride" or a ride could
// never be booked at all — that's what this class is for.
//
// Ride is what makes "just a ride" a valid object, and lets RideRepo/RideService
// work with Ride specifically instead of casting from Dispatch.
class Ride(
    customerId: String,
    driverId: String,
    pickupLocation: Location,
    dropLocation: Location,
    fare: BigDecimal,
    // Both are left out when a ride is booked; passed by the file storage at startup.
    bookedAt: LocalDateTime = LocalDateTime.now(),
    dispatchId: String = IdGenerator.generateBookingId()
) : Booking(customerId, driverId, pickupLocation, dropLocation, fare, bookedAt, dispatchId)