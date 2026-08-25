package cab_booking.service

import cab_booking.model.User
import cab_booking.auth.UserCredential
import cab_booking.repository.CredentialRepo
import cab_booking.repository.UserRepo
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.AccountLockedException
import cab_booking.model.Admin
import cab_booking.model.Customer

object AuthService{

    fun registerUser(user: User, password: String): User {
        saveUserAndCredentials(user, password)
        return user
    }

    fun registerCustomer(
        name: String,
        phoneNumber: String,
        email: String,
        password: String
    ) =
        registerUser(Customer(name = name, phoneNumber = phoneNumber, email = email), password)

    fun registerAdmin(
        name: String,
        phoneNumber: String,
        email: String,
        password: String
    ) =
        registerUser(Admin(name = name, phoneNumber = phoneNumber, email = email), password)

    fun saveUserAndCredentials(user: User, password: String) {
        UserRepo.save(user)
        CredentialRepo.save(UserCredential.withPassword(user.userId,password))
    }

    fun loginUser(email: String, password: String): User {
        val user: User = UserRepo.findByEmail(email)
        val userCredential: UserCredential = CredentialRepo.findByUserId(user.userId)
        checkIsAccountLocked(userCredential)

        if(!userCredential.verifyPassword(password)){
            throw InvalidCredentialsException()
        }
        return user
    }

    private fun checkIsAccountLocked(userCredential: UserCredential) {
        if(isAccountLocked(userCredential.userId)){
            val minutesLeft = userCredential.remainingLockTime().toMinutes()
            val secondsLeft = userCredential.remainingLockTime().seconds % 60
            throw AccountLockedException(minutesLeft, secondsLeft)
        }
    }

    fun changePassword(
        user: User,
        currentPassword: String,
        newPassword: String
    ) {

        val userCredential: UserCredential = CredentialRepo.findByUserId(user.userId)

        if(!userCredential.verifyPassword(currentPassword)){
            throw InvalidCredentialsException()
        }

        userCredential.updatePassword(newPassword)
    }

    fun getLockedAccounts(): List<UserCredential> =
        CredentialRepo.getLockedAccounts()

    fun unlockUserAccount(userId: String) {
        val userCredential = CredentialRepo.findByUserId(userId)
        userCredential.unlockAccount()
    }

    fun lockUserAccount(userId: String) {
        val userCredential = CredentialRepo.findByUserId(userId)
        userCredential.forceLock()
    }

    fun isAccountLocked(userId: String) : Boolean{
        val userCredential = CredentialRepo.findByUserId(userId)
        return userCredential.isAccountLocked()
    }
}