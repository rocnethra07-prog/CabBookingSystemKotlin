package cab_booking.util

object Validator {

    private val emailRegex =
        Regex("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val phoneNumberRegex =
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

    fun isValidPhoneNumber(phoneNumber: String) =
        phoneNumber.matches(phoneNumberRegex)

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