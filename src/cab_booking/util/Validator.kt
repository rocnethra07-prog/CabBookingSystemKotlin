package cab_booking.util

object Validator {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val phoneRegex = Regex("\\d{10}")

    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_])(?=\\S+$).{8,}$")

    fun isValidName(name: String) =
        name.trim().length >= 3

    fun isValidEmail(email: String) =
        email.matches(emailRegex)

    fun isValidPhone(phone: String) =
        phone.matches(phoneRegex)

    fun isValidPassword(password: String) =
        password.matches(passwordRegex)
}