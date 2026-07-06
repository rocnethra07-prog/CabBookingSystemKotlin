package cab_booking.model

import cab_booking.exception.CabBookingException
import cab_booking.util.Validator
import org.mindrot.jbcrypt.BCrypt

//Credentials related class
class UserAuthInfo(val userId: String, password: String) {

    private var passwordHash: String
    init {
        if(!Validator.isValidPassword(password)) {
            throw CabBookingException("Invalid password format.")
        }

        passwordHash = hash(password)
    }

    fun verifyPassword(password: String): Boolean {
        val isValid = matches(password)
        return isValid
    }

    private fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun matches(password: String): Boolean {
        return BCrypt.checkpw(password, passwordHash)
    }

    fun updatePassword(newPassword: String) {
        if(!Validator.isValidPassword(newPassword)) {
            throw CabBookingException("Invalid password format.")
        }
        passwordHash = hash(newPassword)
    }
}