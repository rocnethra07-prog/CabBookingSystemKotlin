package cab_booking.controller

import cab_booking.model.User
import cab_booking.model.types.UserRole
import cab_booking.service.AuthService
import cab_booking.service.UserService

object AuthController {

    fun login(email: String, password: String) =
        AuthService.loginUser(email, password)


    fun register(
        name: String,
        phone: String,
        email: String,
        password: String
    ): User {
        return AuthService.registerUser(
            name = name,
            phone = phone,
            email = email,
            password = password,
            role = UserRole.RIDER
        )
    }

    fun isEmailRegistered(email: String): Boolean {
        return UserService.isEmailRegistered(email)
    }

    fun changePassword(user: User, currentPassword: String, newPassword: String){
        AuthService.changePassword(user,currentPassword,newPassword)
    }
}