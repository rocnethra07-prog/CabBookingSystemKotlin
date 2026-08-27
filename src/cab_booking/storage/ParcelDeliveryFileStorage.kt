package cab_booking.storage

import cab_booking.model.ParcelDelivery
import cab_booking.model.types.BookingStatus
import cab_booking.model.types.Location
import java.math.BigDecimal
import java.time.LocalDateTime

object ParcelDeliveryFileStorage : FileStorage<ParcelDelivery>("data/parcel_deliveries.csv") {

    private const val NOT_SET = "NA"

    override fun toLine(item: ParcelDelivery) =
        "${item.bookingId},${item.customerId},${item.driverId}," +
                "${item.pickupLocation.name},${item.dropLocation.name}," +
                "${item.fare.toPlainString()}," +
                "${item.receiverName},${item.receiverPhoneNumber}," +
                "${item.status.name},${item.bookedAt}," +
                "${item.completedAt?.toString() ?: NOT_SET}," +
                (item.cancelledAt?.toString() ?: NOT_SET)

    override fun fromLine(parts: List<String>): ParcelDelivery {
        val parcelDelivery = ParcelDelivery(
            bookingId = parts[0],
            customerId = parts[1],
            driverId = parts[2],
            pickupLocation = Location.valueOf(parts[3]),
            dropLocation = Location.valueOf(parts[4]),
            fare = BigDecimal(parts[5]),
            receiverName = parts[6],
            receiverPhoneNumber = parts[7],
            bookedAt = LocalDateTime.parse(parts[9])
        )

        replayStatus(parcelDelivery, parts)

        return parcelDelivery
    }

    // STARTED means picked up, COMPLETED means delivered.
    private fun replayStatus(parcelDelivery: ParcelDelivery, parts: List<String>) {
        when (BookingStatus.valueOf(parts[8])) {

            BookingStatus.BOOKED -> {}

            BookingStatus.STARTED -> parcelDelivery.updateStatus(BookingStatus.STARTED)

            BookingStatus.COMPLETED -> {
                parcelDelivery.updateStatus(BookingStatus.STARTED)
                parcelDelivery.updateStatus(BookingStatus.COMPLETED)
                parcelDelivery.setCompletedAt(LocalDateTime.parse(parts[10]))
            }

            BookingStatus.CANCELLED -> {
                parcelDelivery.updateStatus(BookingStatus.CANCELLED)
                parcelDelivery.setCancelledAt(LocalDateTime.parse(parts[11]))
            }
        }
    }
}