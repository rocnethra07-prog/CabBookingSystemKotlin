package cab_booking.service

import cab_booking.exception.CabBookingException
import cab_booking.model.User
import cab_booking.model.UserAuthInfo
import cab_booking.model.types.UserRole
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo

class AuthService() {
    fun isEmailRegistered(email: String) : Boolean{
        return UserRepo.existsByEmail(email)
    }

    fun registerUser(
        name: String,
        phone: String,
        email: String,
        password: String,
        role: UserRole
    ): User {
        val user = User(
            name,
            phone,
            email,
            role)
        registerUserCredentials(user, password)
        return user
    }

    fun registerUserCredentials(user: User, password: String) {
        //Double check for proper data consistency
        if (isEmailRegistered(user.email)) {
            throw CabBookingException("An account with this email already exists.")
        }
        saveUserCredentials(user, password)
    }

    private fun saveUserCredentials(user: User, password: String) {
        UserRepo.save(user)
        AuthRepo.save(UserAuthInfo(user.userId,password))
    }

    fun loginUser(email: String, password: String): User {
        val user: User? = UserRepo.findByEmail(email)

        if (user == null) {
            throw CabBookingException("Account does not exist. Please register.")
        }

        val userAuth: UserAuthInfo? = AuthRepo.findByUserId(user.userId)
        if(userAuth == null){
            throw CabBookingException("Authentication details not found. Please register")
        }

        if(!userAuth.verifyPassword(password)){
            throw CabBookingException("Invalid credentials.")
        }
        return user
    }

    fun changePassword(
        user: User,
        currentPassword: String,
        newPassword: String
    ) {

        val userAuth: UserAuthInfo? = AuthRepo.findByUserId(user.userId)
        if(userAuth == null){
            throw CabBookingException("Authentication details not found. Please register")
        }

        if(!userAuth.verifyPassword(currentPassword)){
            throw CabBookingException("Invalid credentials.")
        }

        userAuth.updatePassword(newPassword)
    }
}