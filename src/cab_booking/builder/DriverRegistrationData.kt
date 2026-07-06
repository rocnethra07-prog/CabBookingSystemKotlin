package cab_booking.builder

import cab_booking.exception.CabBookingException
import cab_booking.model.CabType
import cab_booking.model.Location

class DriverRegistrationData private constructor(
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

    class Builder {

        private var name: String? = null
        private var phone: String? = null
        private var email: String? = null
        private var password: String? = null
        private var currentLocation: Location? = null
        private var licenseNumber: String? = null
        private var model: String? = null
        private var registrationNumber: String? = null
        private var cabType: CabType? = null

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

        fun currentLocation(value: Location) = apply {
            currentLocation = value
        }

        fun licenseNumber(value: String) = apply {
            licenseNumber = value.requireValue("License Number")
        }

        fun model(value: String) = apply {
            model = value.requireValue("Model")
        }

        fun registrationNumber(value: String) = apply {
            registrationNumber = value.requireValue("Registration Number")
        }

        fun cabType(value: CabType) = apply {
            cabType = value
        }

        fun build() = DriverRegistrationData(
            name = name.required("Name"),
            phone = phone.required("Phone"),
            email = email.required("Email"),
            password = password.required("Password"),
            currentLocation = currentLocation.required("Current Location"),
            licenseNumber = licenseNumber.required("License Number"),
            model = model.required("Model"),
            registrationNumber = registrationNumber.required("Registration Number"),
            cabType = cabType.required("Cab Type")
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