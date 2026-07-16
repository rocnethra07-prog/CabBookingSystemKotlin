package cab_booking.model

import cab_booking.util.Validator
import org.mindrot.jbcrypt.BCrypt

//Credentials related class
class UserAuthInfo(val userId: String, password: String) {

    private var passwordHash: String
    private var failedAttempts: Int = 0
    var isAccountLocked: Boolean = false
        private set

    init {
        require(Validator.isValidPassword(password)) { "Invalid password format." }
        passwordHash = hash(password)
    }

    fun verifyPassword(password: String): Boolean {
        if (isAccountLocked){
            return false
        }

        val isValid = matches(password)

        if(isValid){
            resetFailedAttempts()
        }
        else{
            failedAttempts++
            if(failedAttempts >= 3){
                lockAccount()
            }
        }

        return isValid
    }

    private fun lockAccount(){
        isAccountLocked = true
    }

    private fun resetFailedAttempts(){
        failedAttempts = 0
    }

    fun unlockAccount(){
        isAccountLocked = false
        resetFailedAttempts()
    }

    private fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun matches(password: String): Boolean {
        return BCrypt.checkpw(password, passwordHash)
    }

    fun updatePassword(newPassword: String) {
        require(Validator.isValidPassword(newPassword)) { "Invalid password format." }
        passwordHash = hash(newPassword)
    }
}