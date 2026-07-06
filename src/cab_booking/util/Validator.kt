package cab_booking.util

import cab_booking.exception.CabBookingException

//object instead of class
//So I can call Validator.isValidName() instead of creating Validator()

object Validator {

    fun validateString(value: String?, fieldName: String) {
        if (value.isNullOrBlank()) {
            throw CabBookingException("$fieldName is required.")
        }
    }
    fun isValidName(name: String): Boolean{
        return name.isNotBlank() && name.trim().length >= 3
    }

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    fun isValidEmail(email: String): Boolean{
        return email.isNotBlank() && email.matches(emailRegex)
    }

    private val phoneRegex = Regex("\\d{10}")
    fun isValidPhone(phone: String): Boolean{
        return phone.isNotBlank() && phone.matches(phoneRegex)
    }

    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_])(?=\\S+$).{8,}$")
    fun isValidPassword(password: String): Boolean{
        return password.isNotBlank() && password.matches(passwordRegex)
    }
}