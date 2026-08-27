package cab_booking.storage

import cab_booking.model.Ride
import cab_booking.model.types.BookingStatus
import cab_booking.model.types.Location
import java.math.BigDecimal
import java.time.LocalDateTime

object RideFileStorage : FileStorage<Ride>("data/rides.csv") {

    // a date that was never reached
    private const val NOT_SET = "NA"

    override fun toLine(item: Ride) =
        "${item.bookingId},${item.customerId},${item.driverId}," +
                "${item.pickupLocation.name},${item.dropLocation.name}," +
                "${item.fare.toPlainString()}," +
                "${item.status.name},${item.bookedAt}," +
                "${item.completedAt?.toString() ?: NOT_SET}," +
                "${item.cancelledAt?.toString() ?: NOT_SET},${item.rating}"

    override fun fromLine(parts: List<String>): Ride {
        val ride = Ride(
            bookingId = parts[0],
            customerId = parts[1],
            driverId = parts[2],
            pickupLocation = Location.valueOf(parts[3]),
            dropLocation = Location.valueOf(parts[4]),
            fare = BigDecimal(parts[5]),
            bookedAt = LocalDateTime.parse(parts[7])
        )

        replayStatus(ride, parts)

        return ride
    }

    // A new Ride starts as BOOKED, so walk it back through the steps it took.
    private fun replayStatus(ride: Ride, parts: List<String>) {
        when (BookingStatus.valueOf(parts[6])) {

            BookingStatus.BOOKED -> {}

            BookingStatus.STARTED -> ride.updateStatus(BookingStatus.STARTED)

            BookingStatus.COMPLETED -> {
                ride.updateStatus(BookingStatus.STARTED)
                ride.updateStatus(BookingStatus.COMPLETED)
                ride.setCompletedAt(LocalDateTime.parse(parts[8]))

                val rating = parts[10].toInt()
                if (rating in 1..5) {
                    ride.setRating(rating)
                }
            }

            BookingStatus.CANCELLED -> {
                ride.updateStatus(BookingStatus.CANCELLED)
                ride.setCancelledAt(LocalDateTime.parse(parts[9]))
            }
        }
    }
}