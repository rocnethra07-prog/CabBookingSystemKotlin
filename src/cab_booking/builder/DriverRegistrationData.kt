package cab_booking.builder

import cab_booking.exception.CabBookingException
import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.util.Validator

//builder class replacement with data class: takes in all the parameters required to create a driver and checks for non-null and not blank values in init block
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
) {

    // init block: replaces builder's "required fields" checks.
    init {
        Validator.validateString(name, "Name")
        Validator.validateString(phone, "Phone")
        Validator.validateString(email, "Email")
        Validator.validateString(password, "Password")
        Validator.validateString(licenseNumber, "License Number")
        Validator.validateString(model, "Cab Model")
        Validator.validateString(registrationNumber, "Registration Number")

        // Non-null checks for object fields (Location, CabType).
        if (currentLocation == null) {
            throw CabBookingException("Location is required.")
        }
        if (cabType == null) {
            throw CabBookingException("Cab Type is required.")
        }
    }

}