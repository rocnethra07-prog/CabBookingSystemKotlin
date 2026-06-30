package cab_booking.builder

import cab_booking.exception.CabBookingException
import cab_booking.model.UserRole
import cab_booking.util.Validator

class UserRegistrationData private constructor(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: UserRole
) {
    class Builder{

        private var name: String? = null
        private var password: String? = null
        private var phone: String? = null
        private var email: String? = null
        private var role: UserRole? = null

        fun name(name: String) = apply {
            validateString(name, "Name")
            this.name = name
        }

        fun phone(phone: String) = apply {
            validateString(phone,"Phone")
            this.phone = phone
        }

        fun email(email: String) = apply {
            validateString(email, "Email")
            this.email = email
        }

        fun password(password: String) = apply {
            validateString(password, "Password")
            this.password = password
        }

        fun role(role: UserRole) = apply {
            this.role = role
        }

        fun build(): UserRegistrationData {
            return UserRegistrationData(
                name ?: throw CabBookingException("Name is required."),
                phone ?: throw CabBookingException("Phone is required."),
                email ?: throw CabBookingException("Email is required."),
                password ?: throw CabBookingException("Password is required."),
                role ?: throw CabBookingException("Role is required.")
            )
        }

        private fun validateString(value: String?, fieldName: String) {
            if (value.isNullOrBlank()) {
                throw CabBookingException("$fieldName is required.")
            }
        }

    }
}
