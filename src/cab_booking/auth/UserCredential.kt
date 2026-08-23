package cab_booking.auth

import cab_booking.exception.AccountLockedException
import cab_booking.util.Validator
import org.mindrot.jbcrypt.BCrypt
import java.time.Duration
import java.time.LocalDateTime

//Credentials related class
class UserCredential(val userId: String, password: String) {

    companion object{
        private const val MAX_FAILED_ATTEMPTS = 3
        private val LOCK_DURATION = Duration.ofMinutes(15)
    }

    private var passwordHash: String
    private var failedAttempts: Int = 0

    fun isAccountLocked() : Boolean {
        refreshLockStatus()
        return lockedAt != null
    }

    private var lockedAt : LocalDateTime? = null

    init {
        require(Validator.isValidPassword(password)) { "Invalid password format." }
        passwordHash = hash(password)
    }

    fun verifyPassword(password: String): Boolean {
        if (isAccountLocked()){
            val minutesLeft = remainingLockTime().toMinutes()
            val secondsLeft = remainingLockTime().seconds % 60
            throw AccountLockedException(minutesLeft, secondsLeft)
        }

        val isValid = matches(password)

        if(isValid){
            resetFailedAttempts()
        }
        else{
            failedAttempts++
            if(failedAttempts >= MAX_FAILED_ATTEMPTS){
                lockAccount()
            }
        }

        return isValid
    }

    fun remainingLockTime(): Duration {
        refreshLockStatus()

        val since = lockedAt ?: return Duration.ZERO
        val remaining =  LOCK_DURATION - Duration.between(since, LocalDateTime.now())
        return if(remaining.isZero ||remaining.isNegative) Duration.ZERO else remaining
    }

    private fun lockAccount(){
        lockedAt = LocalDateTime.now()
    }

    private fun refreshLockStatus() {
        val since = lockedAt ?: return

        if (Duration.between(since, LocalDateTime.now()) >= LOCK_DURATION) {
            unlockAccount()
        }
    }

    private fun resetFailedAttempts(){
        failedAttempts = 0
    }

    fun unlockAccount(){
        lockedAt = null
        resetFailedAttempts()
    }

    // Lets an admin lock an account directly, independent of failed login attempts.
    fun forceLock() {
        lockAccount()
    }

    private fun hash(password: String) =
        BCrypt.hashpw(password, BCrypt.gensalt())


    private fun matches(password: String) =
        BCrypt.checkpw(password, passwordHash)


    fun updatePassword(newPassword: String) {
        require(Validator.isValidPassword(newPassword)) { "Invalid password format." }
        require(!BCrypt.checkpw(newPassword, passwordHash)) {
            "New password must be different from the old password."
        }
        passwordHash = hash(newPassword)
    }
}