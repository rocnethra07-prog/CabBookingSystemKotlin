package cab_booking.model

import cab_booking.model.types.VehicleCategory
import cab_booking.util.IdGenerator
import cab_booking.util.Validator

// One class for every vehicle a driver can register - bike, auto, or car.
// What used to be three lookalike subclasses (Bike/Auto/Cab, each tied to its
// own type-specific enum) is now one class carrying a VehicleCategory. The
// category already knows its VehicleType and its fare rule, so there is
// nothing left for a subclass to add.
class Vehicle(
    val model: String,
    val registrationNumber: String,
    val vehicleCategory: VehicleCategory
) {
    val vehicleId: String = IdGenerator.generateVehicleId()

    init {
        require(Validator.isValidRegistrationNumber(registrationNumber)) { "Invalid registration number. Format: TN01AB0001." }
        require(model.isNotBlank()) { "Vehicle model cannot be blank." }
    }

    override fun toString(): String {
        return """
            Vehicle ID           : $vehicleId
            Model                : $model
            Category             : $vehicleCategory (${vehicleCategory.vehicleType})
            Base Fare            : ₹${vehicleCategory.basePrice}
            Fare / km            : ₹${vehicleCategory.perKmRate}
            Registration Number  : $registrationNumber
        """.trimIndent()
    }
}