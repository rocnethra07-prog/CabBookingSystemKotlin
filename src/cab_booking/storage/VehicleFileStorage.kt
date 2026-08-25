package cab_booking.storage

import cab_booking.model.Vehicle
import cab_booking.model.types.VehicleCategory

object VehicleFileStorage : FileStorage<Vehicle>("data/vehicles.csv") {

    override fun toLine(item: Vehicle) =
        "${item.vehicleId},${item.model},${item.registrationNumber},${item.vehicleCategory.name}"

    override fun fromLine(parts: List<String>) = Vehicle(
        vehicleId = parts[0],
        model = parts[1],
        registrationNumber = parts[2],
        vehicleCategory = VehicleCategory.valueOf(parts[3])
    )
}
