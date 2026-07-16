package cab_booking.service

import cab_booking.model.User
import cab_booking.model.UserAuthInfo
import cab_booking.model.types.UserRole
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo
import exception.AuthenticationException
import exception.EmailAlreadyRegisteredException
import exception.InvalidCredentialsException
import exception.UserNotFoundException

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
        saveUserCredentials(user, password)
        return user
    }

     fun saveUserCredentials(user: User, password: String) {
        UserRepo.save(user)
        AuthRepo.save(UserAuthInfo(user.userId,password))
    }

    fun loginUser(email: String, password: String): User {
        val user: User? = UserRepo.findByEmail(email)

        if (user == null) {
            throw UserNotFoundException("Account does not exist. Please register.")
        }

        val userAuth: UserAuthInfo? = AuthRepo.findByUserId(user.userId)
        if(userAuth == null){
            throw AuthenticationException("Authentication details not found. Please register")
        }

        if(!userAuth.verifyPassword(password)){
            throw AuthenticationException("Invalid credentials.")
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
            throw AuthenticationException("Authentication details not found. Please register")
        }

        if(!userAuth.verifyPassword(currentPassword)){
            throw InvalidCredentialsException("Invalid credentials.")
        }

        userAuth.updatePassword(newPassword)
    }
}