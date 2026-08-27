package cab_booking.model

import cab_booking.model.types.VehicleCategory
import cab_booking.util.IdGenerator
import cab_booking.util.Validator


data class Vehicle(
    val model: String,
    val registrationNumber: String,
    val vehicleCategory: VehicleCategory,
    val vehicleId: String = IdGenerator.generateVehicleId()
) {
    init {
        require(Validator.isValidRegistrationNumber(registrationNumber)) { "Invalid registration number. Format: TN01AB0001." }
        require(model.isNotBlank()) { "Vehicle model cannot be blank." }
    }

    override fun toString(): String {
        return """
            Model                : $model
            Category             : $vehicleCategory (${vehicleCategory.vehicleType})
            Base Fare            : ₹${vehicleCategory.basePrice}
            Fare / km            : ₹${vehicleCategory.perKmRate}
            Registration Number  : $registrationNumber
        """.trimIndent()
    }
}