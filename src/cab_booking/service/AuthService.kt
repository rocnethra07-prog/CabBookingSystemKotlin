package cab_booking.service

import cab_booking.model.User
import cab_booking.auth.UserCredential
import cab_booking.model.types.UserRole
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.AccountLockedException

object AuthService{

    fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        role: UserRole
    ): User {
        val user = User(
            name = name,
            phone = phone,
            email = email,
            userRole = role)
        saveUserAndCredentials(user, password)
        return user
    }

     fun saveUserAndCredentials(user: User, password: String) {
        UserRepo.save(user)
        AuthRepo.save(UserCredential(user.userId,password))
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
        if(userAuth.isAccountLocked()){
            val minutesLeft = userAuth.remainingLockTime().toMinutes().plus(1)
            throw AccountLockedException(minutesLeft)
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
}