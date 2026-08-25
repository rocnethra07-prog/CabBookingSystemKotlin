package cab_booking.service

import cab_booking.model.User
import cab_booking.auth.UserCredential
import cab_booking.repository.AuthRepo
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
        phone: String,
        email: String,
        password: String
    ) =
        registerUser(Customer(name = name, phoneNumber = phone, email = email), password)

    fun registerAdmin(
        name: String,
        phone: String,
        email: String,
        password: String
    ) =
        registerUser(Admin(name = name, phoneNumber = phone, email = email), password)

    fun saveUserAndCredentials(user: User, password: String) {
        UserRepo.save(user)
        AuthRepo.save(UserCredential.withPassword(user.userId,password))
    }

    fun loginUser(email: String, password: String): User {
        val user: User = UserRepo.findByEmail(email)
        val userAuth: UserCredential = AuthRepo.findByUserId(user.userId)
        checkIsAccountLocked(userAuth)

        if(!userAuth.verifyPassword(password)){
            throw InvalidCredentialsException()
        }
        return user
    }

    private fun checkIsAccountLocked(userAuth: UserCredential) {
        if(isAccountLocked(userAuth.userId)){
            val minutesLeft = userAuth.remainingLockTime().toMinutes()
            val secondsLeft = userAuth.remainingLockTime().seconds % 60
            throw AccountLockedException(minutesLeft, secondsLeft)
        }
    }

    fun changePassword(
        user: User,
        currentPassword: String,
        newPassword: String
    ) {

        val userAuth: UserCredential = AuthRepo.findByUserId(user.userId)

        if(!userAuth.verifyPassword(currentPassword)){
            throw InvalidCredentialsException()
        }

        userAuth.updatePassword(newPassword)
    }

    fun getLockedAccounts(): List<UserCredential> =
        AuthRepo.getLockedAccounts()

    fun unlockUserAccount(userId: String) {
        val auth = AuthRepo.findByUserId(userId)
        auth.unlockAccount()
    }

    fun lockUserAccount(userId: String) {
        val auth = AuthRepo.findByUserId(userId)
        auth.forceLock()
    }

    fun isAccountLocked(userId: String) : Boolean{
        val userAuth = AuthRepo.findByUserId(userId)
        return userAuth.isAccountLocked()
    }
}