package cab_booking.storage

import cab_booking.model.Driver
import cab_booking.model.types.Location
import java.math.BigDecimal

object DriverFileStorage : FileStorage<Driver>("data/drivers.csv") {

    override fun toLine(item: Driver) =
        "${item.userId},${item.name},${item.phoneNumber},${item.email}," +
                "${item.assignedVehicleId},${item.licenseNumber},${item.currentLocation.name}," +
                "${item.isAvailable},${item.totalEarnings.toPlainString()}," +
                "${item.totalRating},${item.totalRatingCount}"

    override fun fromLine(parts: List<String>): Driver {
        val driver = Driver(
            userId = parts[0],
            name = parts[1],
            phoneNumber = parts[2],
            email = parts[3],
            assignedVehicleId = parts[4],
            licenseNumber = parts[5],
            currentLocation = Location.valueOf(parts[6])
        )

        driver.setAvailability(parts[7].toBoolean())
        driver.updateTotalEarnings(BigDecimal(parts[8]))
        driver.updateTotalRating(parts[9].toInt())
        driver.updateTotalRatingCount(parts[10].toInt())

        return driver
    }
}
