package cab_booking.builder

import cab_booking.model.types.CabType
import cab_booking.model.types.Location

data class DriverRegistrationData(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val currentLocation: Location,
    val licenseNumber: String,
    val model: String,
    val registrationNumber: String,
    val cabType: CabType
)