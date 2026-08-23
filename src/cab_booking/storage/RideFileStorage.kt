package cab_booking.storage

import cab_booking.model.Ride
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import cab_booking.model.types.VehicleCategory
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime

class RideFileStorage(private val filePath: String) : FileStorage<Ride> {

    override fun save(items: List<Ride>) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { writer ->
            items.forEach { ride ->
                val completedAtText = ride.completedAt?.toString() ?: "NA"
                val cancelledAtText = ride.cancelledAt?.toString() ?: "NA"
                writer.write(
                    "${ride.rideId},${ride.customerId},${ride.driverId}," +
                            "${ride.pickupLocation.name},${ride.dropLocation.name}," +
                            "${ride.vehicleCategory.name},${ride.fare.toPlainString()}," +
                            "${ride.rideStatus.name},${ride.bookedAt}," +
                            "$completedAtText,$cancelledAtText,${ride.rating}"
                )
                writer.newLine()
            }
        }
    }

    override fun load(): List<Ride> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",")
                val ride = Ride(
                    riderId = parts[1],
                    driverId = parts[2],
                    pickupLocation = Location.valueOf(parts[3]),
                    dropLocation = Location.valueOf(parts[4]),
                    vehicleCategory = VehicleCategory.valueOf(parts[5]),
                    fare = BigDecimal(parts[6]),
                    rideId = parts[0],
                    bookedAt = LocalDateTime.parse(parts[8])
                )

                // A new Ride always starts as BOOKED, so the saved ride is walked back
                // through the same steps it originally took. Nothing is set behind the
                // model's back - updateRideStatus still checks every transition.
                when (RideStatus.valueOf(parts[7])) {

                    RideStatus.BOOKED -> {
                        // already BOOKED, nothing to replay
                    }

                    RideStatus.STARTED -> {
                        ride.updateRideStatus(RideStatus.STARTED)
                    }

                    RideStatus.COMPLETED -> {
                        ride.updateRideStatus(RideStatus.STARTED)
                        ride.updateRideStatus(RideStatus.COMPLETED)
                        ride.setCompletedAt(LocalDateTime.parse(parts[9]))

                        val rating = parts[11].toInt()
                        if (rating in 1..5) {
                            ride.setRatings(rating)
                        }
                    }

                    RideStatus.CANCELLED -> {
                        ride.updateRideStatus(RideStatus.CANCELLED)
                        ride.setCancelledAt(LocalDateTime.parse(parts[10]))
                    }
                }

                ride
            }
    }
}
