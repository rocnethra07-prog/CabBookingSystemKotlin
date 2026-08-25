package cab_booking.model

import cab_booking.exception.InvalidDispatchStateException
import cab_booking.model.types.Location
import cab_booking.model.types.DispatchStatus
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
) : Dispatch(customerId, driverId, pickupLocation, dropLocation, fare, bookedAt, dispatchId){
    var ratings: Int = 0 // 1–5, 0 if not yet rated
        private set

    fun setRatings(rating: Int){
        if(status != DispatchStatus.COMPLETED) {
            throw InvalidDispatchStateException("Only completed rides can be rated.")
        }

        require(rating in 1..5) { "Rating must be between 1 and 5." }

        this.ratings = rating
    }
}