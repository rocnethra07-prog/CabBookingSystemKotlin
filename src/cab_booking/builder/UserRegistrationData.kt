package cab_booking.builder

import cab_booking.exception.CabBookingException
import cab_booking.model.UserRole

class UserRegistrationData private constructor(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: UserRole
) {

    class Builder {

        private var name: String? = null
        private var phone: String? = null
        private var email: String? = null
        private var password: String? = null
        private var role: UserRole? = null

        fun name(value: String) = apply {
            name = value.requireValue("Name")
        }

        fun phone(value: String) = apply {
            phone = value.requireValue("Phone")
        }

        fun email(value: String) = apply {
            email = value.requireValue("Email")
        }

        fun password(value: String) = apply {
            password = value.requireValue("Password")
        }

        fun role(value: UserRole) = apply {
            role = value
        }

        fun build() = UserRegistrationData(
            name = name.required("Name"),
            phone = phone.required("Phone"),
            email = email.required("Email"),
            password = password.required("Password"),
            role = role.required("Role")
        )

        private fun String.requireValue(field: String): String {
            if (isBlank()) {
                throw CabBookingException("$field is required.")
            }
            return trim()
        }

        private fun <T> T?.required(field: String): T =
            this ?: throw CabBookingException("$field is required.")
    }
}