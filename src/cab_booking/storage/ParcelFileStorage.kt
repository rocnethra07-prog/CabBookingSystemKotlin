package cab_booking.storage

import cab_booking.model.Parcel
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.ParcelStatus
import cab_booking.model.types.VehicleCategory
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime

class ParcelFileStorage(private val filePath: String) : FileStorage<Parcel> {

    override fun save(items: List<Parcel>) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { writer ->
            items.forEach { parcel ->
                val deliveredAtText = parcel.deliveredAt?.toString() ?: "NA"
                val cancelledAtText = parcel.cancelledAt?.toString() ?: "NA"
                writer.write(
                    "${parcel.parcelId},${parcel.customerId},${parcel.driverId}," +
                            "${parcel.pickupLocation.name},${parcel.dropLocation.name}," +
                            "${parcel.vehicleCategory.name},${parcel.fare.toPlainString()}," +
                            "${parcel.modeOfParcel.name},${parcel.contactName},${parcel.contactPhone}," +
                            "${parcel.weightKg.toPlainString()},${parcel.category.name}," +
                            "${parcel.parcelStatus.name},${parcel.bookedAt}," +
                            "$deliveredAtText,$cancelledAtText"
                )
                writer.newLine()
            }
        }
    }

    override fun load(): List<Parcel> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",")
                val parcel = Parcel(
                    customerId = parts[1],
                    driverId = parts[2],
                    pickupLocation = Location.valueOf(parts[3]),
                    dropLocation = Location.valueOf(parts[4]),
                    vehicleCategory = VehicleCategory.valueOf(parts[5]),
                    fare = BigDecimal(parts[6]),
                    modeOfParcel = ParcelMode.valueOf(parts[7]),
                    contactName = parts[8],
                    contactPhone = parts[9],
                    weightKg = BigDecimal(parts[10]),
                    category = ParcelCategory.valueOf(parts[11]),
                    parcelId = parts[0],
                    bookedAt = LocalDateTime.parse(parts[13])
                )

                // A new Parcel always starts as BOOKED, so the saved parcel is walked
                // back through the same steps it originally took.
                when (ParcelStatus.valueOf(parts[12])) {

                    ParcelStatus.BOOKED -> {
                        // already BOOKED, nothing to replay
                    }

                    ParcelStatus.PICKED_UP -> {
                        parcel.updateParcelStatus(ParcelStatus.PICKED_UP)
                    }

                    ParcelStatus.DELIVERED -> {
                        parcel.updateParcelStatus(ParcelStatus.PICKED_UP)
                        parcel.updateParcelStatus(ParcelStatus.DELIVERED)
                        parcel.setDeliveredAt(LocalDateTime.parse(parts[14]))
                    }

                    ParcelStatus.CANCELLED -> {
                        parcel.updateParcelStatus(ParcelStatus.CANCELLED)
                        parcel.setCancelledAt(LocalDateTime.parse(parts[15]))
                    }
                }

                parcel
            }
    }
}
