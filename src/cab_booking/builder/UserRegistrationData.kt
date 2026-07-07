package cab_booking.builder

import cab_booking.exception.CabBookingException
import cab_booking.model.types.UserRole
import cab_booking.util.Validator

// Useful when user object fields grow later
// Data class holding user registration fields.
data class UserRegistrationData(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: UserRole
) {

    // init block runs after construction
    // This is where we enforce "not blank" and "not null" rules.
    init {
        Validator.validateString(name, "Name")
        Validator.validateString(phone, "Phone")
        Validator.validateString(email, "Email")
        Validator.validateString(password, "Password")
        //role is non-nullable type (no check needed)
    }
}