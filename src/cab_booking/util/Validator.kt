package cab_booking.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Validator {

    private val emailRegex =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val phoneRegex =
        Regex("^\\d{10}$")

    private val passwordRegex =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_])(?=\\S+$).{8,}$")

    private val registrationNumberRegex =
        Regex("^[A-Z]{2}\\d{2}[A-Z]{1,3}\\d{4}$")

    private val licenseNumberRegex =
        Regex("^[A-Z]{2}\\d{12}$")


    fun isValidName(name: String) =
        name.trim().length >= 3

    fun isValidEmail(email: String) =
        email.matches(emailRegex)

    fun isValidPhone(phone: String) =
        phone.matches(phoneRegex)

    fun isValidPassword(password: String) =
        password.matches(passwordRegex)

    fun isValidLicenseNumber(licenseNumber: String): Boolean =
        licenseNumber.trim()
            .uppercase()
            .matches(licenseNumberRegex)

    fun isValidRegistrationNumber(registrationNumber: String): Boolean =
        registrationNumber.trim()
            .uppercase()
            .matches(registrationNumberRegex)
}


private val DISPLAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

fun LocalDateTime.toDisplayString(): String = this.format(DISPLAY_FORMAT)
