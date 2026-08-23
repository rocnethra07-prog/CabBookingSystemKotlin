package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import java.math.BigDecimal
import java.time.LocalDateTime

abstract class Booking(
    val customerId: String,
    val driverId: String,
    val pickupLocation: Location,
    val dropLocation: Location,
    val vehicleCategory: VehicleCategory,
    val fare: BigDecimal
) {

    val bookedAt: LocalDateTime = LocalDateTime.now()

    var cancelledAt: LocalDateTime? = null
        private set

    fun setCancelledAt(cancelledAt: LocalDateTime) {
        this.cancelledAt = cancelledAt
    }

    init {
        require(customerId.isNotBlank()) { "Customer ID cannot be blank." }

        require(driverId.isNotBlank()) { "Driver ID cannot be blank." }

        require(pickupLocation != dropLocation) { "Pickup and drop locations cannot be the same." }

        require(fare > BigDecimal.ZERO) { "Fare must be greater than zero." }
    }
}