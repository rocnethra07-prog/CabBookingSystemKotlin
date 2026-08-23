package cab_booking.storage

import cab_booking.model.Vehicle
import cab_booking.model.types.VehicleCategory
import java.io.File

class VehicleFileStorage(private val filePath: String) : FileStorage<Vehicle> {

    override fun save(items: List<Vehicle>) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { writer ->
            items.forEach { vehicle ->
                writer.write(
                    "${vehicle.vehicleId},${vehicle.model},${vehicle.registrationNumber}," +
                            "${vehicle.vehicleCategory}"
                )
                writer.newLine()
            }
        }
    }

    override fun load(): List<Vehicle> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",")
                Vehicle(
                    model = parts[1],
                    registrationNumber = parts[2],
                    vehicleCategory = VehicleCategory.valueOf(parts[3]),
                    vehicleId = parts[0]
                )
            }
    }
}
