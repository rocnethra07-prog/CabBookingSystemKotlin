package cab_booking.service

import cab_booking.model.User
import cab_booking.auth.UserCredential
import cab_booking.model.types.UserRole
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.UserNotFoundException
import cab_booking.exception.AccountLockedException

object AuthService{
    fun isEmailRegistered(email: String) : Boolean =
        UserRepo.existsByEmail(email)


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
        saveUserCredentials(user, password)
        return user
    }

     fun saveUserCredentials(user: User, password: String) {
        UserRepo.save(user)
        AuthRepo.save(UserCredential(user.userId,password))
    }

    fun loginUser(email: String, password: String): User {
        val user: User? = UserRepo.findByEmail(email)

        if (user == null) {
            throw UserNotFoundException("Account does not exist. Please register.")
        }

        val userAuth: UserCredential? = AuthRepo.findByUserId(user.userId)
        if(userAuth == null){
            throw CredentialsNotFoundException()
        }

        checkIsAccountLocked(userAuth)

        if(!userAuth.verifyPassword(password)){
            checkIsAccountLocked(userAuth)
            throw InvalidCredentialsException()
        }
        return user
    }

    private fun checkIsAccountLocked(userAuth: UserCredential){
        if(userAuth.isAccountLocked){
            val minutesLeft = userAuth.remainingLockTime()?.toMinutes()?.plus(1) ?: 0
            throw AccountLockedException(
                "Try again in ~$minutesLeft minute(s)"
            )
        }
    }

    fun changePassword(
        user: User,
        currentPassword: String,
        newPassword: String
    ) {

        val userAuth: UserCredential? = AuthRepo.findByUserId(user.userId)
        if(userAuth == null){
            throw CredentialsNotFoundException()
        }

        if(!userAuth.verifyPassword(currentPassword)){
            throw InvalidCredentialsException()
        }

        userAuth.updatePassword(newPassword)
    }
}