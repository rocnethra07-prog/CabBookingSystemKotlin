package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.util.IdGenerator
import java.math.BigDecimal
import java.time.LocalDateTime

class Ride(
    customerId: String,
    driverId: String,
    pickupLocation: Location,
    dropLocation: Location,
    fare: BigDecimal,
    // Both are left out when a ride is booked; passed by the file storage at startup.
    bookedAt: LocalDateTime = LocalDateTime.now(),
    dispatchId: String = IdGenerator.generateDispatchId()
) : Dispatch(customerId, driverId, pickupLocation, dropLocation, fare, bookedAt, dispatchId)