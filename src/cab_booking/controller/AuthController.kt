package cab_booking.controller

import cab_booking.model.User
import cab_booking.service.AuthService
import cab_booking.service.UserService

object AuthController {

    fun login(email: String, password: String) =
        AuthService.loginUser(email, password)


    fun register(
        name: String,
        phoneNumber: String,
        email: String,
        password: String
    ) =
        AuthService.registerCustomer(
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            password = password
        )

    fun isEmailRegistered(email: String) =
        UserService.isEmailRegistered(email)


    fun changePassword(user: User, currentPassword: String, newPassword: String) =
        AuthService.changePassword(user,currentPassword,newPassword)

}