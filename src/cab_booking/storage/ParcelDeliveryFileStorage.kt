package cab_booking.storage

import cab_booking.model.ParcelDelivery
import cab_booking.model.types.DispatchStatus
import cab_booking.model.types.Location
import java.math.BigDecimal
import java.time.LocalDateTime

object ParcelDeliveryFileStorage : FileStorage<ParcelDelivery>("data/parcel_deliveries.csv") {

    private const val NOT_SET = "NA"

    override fun toLine(item: ParcelDelivery) =
        "${item.dispatchId},${item.customerId},${item.driverId}," +
                "${item.pickupLocation.name},${item.dropLocation.name}," +
                "${item.fare.toPlainString()}," +
                "${item.receiverName},${item.receiverPhoneNumber}," +
                "${item.status.name},${item.bookedAt}," +
                "${item.completedAt?.toString() ?: NOT_SET}," +
                (item.cancelledAt?.toString() ?: NOT_SET)

    override fun fromLine(parts: List<String>): ParcelDelivery {
        val parcelDelivery = ParcelDelivery(
            dispatchId = parts[0],
            customerId = parts[1],
            driverId = parts[2],
            pickupLocation = Location.valueOf(parts[3]),
            dropLocation = Location.valueOf(parts[4]),
            fare = BigDecimal(parts[6]),
            receiverName = parts[7],
            receiverPhoneNumber = parts[8],
            bookedAt = LocalDateTime.parse(parts[10])
        )

        replayStatus(parcelDelivery, parts)

        return parcelDelivery
    }

    // STARTED means picked up, COMPLETED means delivered.
    private fun replayStatus(parcelDelivery: ParcelDelivery, parts: List<String>) {
        when (DispatchStatus.valueOf(parts[9])) {

            DispatchStatus.BOOKED -> {}

            DispatchStatus.STARTED -> parcelDelivery.updateStatus(DispatchStatus.STARTED)

            DispatchStatus.COMPLETED -> {
                parcelDelivery.updateStatus(DispatchStatus.STARTED)
                parcelDelivery.updateStatus(DispatchStatus.COMPLETED)
                parcelDelivery.setCompletedAt(LocalDateTime.parse(parts[11]))
            }

            DispatchStatus.CANCELLED -> {
                parcelDelivery.updateStatus(DispatchStatus.CANCELLED)
                parcelDelivery.setCancelledAt(LocalDateTime.parse(parts[12]))
            }
        }
    }
}
