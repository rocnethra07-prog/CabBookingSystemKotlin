package cab_booking.storage

import cab_booking.model.Driver
import cab_booking.model.types.Location
import java.io.File
import java.math.BigDecimal

class DriverFileStorage(private val filePath: String) : FileStorage<Driver> {

    override fun save(items: List<Driver>) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { writer ->
            items.forEach { driver ->
                writer.write(
                    "${driver.userId},${driver.name},${driver.phone},${driver.email}," +
                            "${driver.vehicleId},${driver.licenseNumber},${driver.currentLocation.name}," +
                            "${driver.isAvailable},${driver.earnings.toPlainString()}," +
                            "${driver.totalRating},${driver.ratingCount}"
                )
                writer.newLine()
            }
        }
    }

    override fun load(): List<Driver> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",")
                val driver = Driver(
                    name = parts[1],
                    phone = parts[2],
                    email = parts[3],
                    vehicleId = parts[4],
                    licenseNumber = parts[5],
                    currentLocation = Location.valueOf(parts[6]),
                    userId = parts[0]
                )

                // Put the driver back the way he was, using the same methods the app uses
                driver.setAvailability(true) //true as of now because the driver is set to false without a ride or parcel being stored in the persisted
                driver.updateEarnings(BigDecimal(parts[8]))
                driver.updateTotalRating(parts[9].toInt())
                driver.updateRatingCount(parts[10].toInt())

                driver
            }
    }
}
