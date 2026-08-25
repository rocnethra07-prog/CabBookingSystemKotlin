package cab_booking.storage

import cab_booking.model.Ride
import cab_booking.model.types.DispatchStatus
import cab_booking.model.types.Location
import java.math.BigDecimal
import java.time.LocalDateTime

object RideFileStorage : FileStorage<Ride>("data/rides.csv") {

    // a date that was never reached
    private const val NOT_SET = "NA"

    override fun toLine(item: Ride) =
        "${item.dispatchId},${item.customerId},${item.driverId}," +
                "${item.pickupLocation.name},${item.dropLocation.name}," +
                "${item.fare.toPlainString()}," +
                "${item.status.name},${item.bookedAt}," +
                "${item.completedAt?.toString() ?: NOT_SET}," +
                "${item.cancelledAt?.toString() ?: NOT_SET},${item.rating}"

    override fun fromLine(parts: List<String>): Ride {
        val ride = Ride(
            dispatchId = parts[0],
            customerId = parts[1],
            driverId = parts[2],
            pickupLocation = Location.valueOf(parts[3]),
            dropLocation = Location.valueOf(parts[4]),
            fare = BigDecimal(parts[6]),
            bookedAt = LocalDateTime.parse(parts[8])
        )

        replayStatus(ride, parts)

        return ride
    }

    // A new Ride starts as BOOKED, so walk it back through the steps it took.
    private fun replayStatus(ride: Ride, parts: List<String>) {
        when (DispatchStatus.valueOf(parts[7])) {

            DispatchStatus.BOOKED -> {}

            DispatchStatus.STARTED -> ride.updateStatus(DispatchStatus.STARTED)

            DispatchStatus.COMPLETED -> {
                ride.updateStatus(DispatchStatus.STARTED)
                ride.updateStatus(DispatchStatus.COMPLETED)
                ride.setCompletedAt(LocalDateTime.parse(parts[9]))

                val rating = parts[11].toInt()
                if (rating in 1..5) {
                    ride.setRatings(rating)
                }
            }

            DispatchStatus.CANCELLED -> {
                ride.updateStatus(DispatchStatus.CANCELLED)
                ride.setCancelledAt(LocalDateTime.parse(parts[10]))
            }
        }
    }
}
