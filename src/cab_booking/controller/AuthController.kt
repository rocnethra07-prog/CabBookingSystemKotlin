package cab_booking.controller

import cab_booking.exception.CabBookingException
import cab_booking.model.User
import cab_booking.model.types.UserRole
import cab_booking.service.AuthService
import cab_booking.util.InputUtil

class AuthController(
    private val authService: AuthService
) {

    fun login() : User?{

        val email = InputUtil.getEmail()
        val password = InputUtil.getPassword()

        try {
            val user: User = authService.loginUser(email, password)
            println("\nWelcome back, " + user.name)
            return user
        }
        catch (e: CabBookingException) {
            println("[!] Login failed: " + e.message)
            return null
        }
    }

    fun register() : User?{
        val name = InputUtil.getName()
        val phone = InputUtil.getPhone()
        var email: String
        while (true) {
            email = InputUtil.getEmail()

            //Pre-check for UX
            if(authService.isEmailRegistered(email)){
                println("! This email is already registered. Please use a different email !")
                continue
            }
            break
        }

        val password = InputUtil.getPassword()

        try {
            val user: User = authService.registerUser(name, phone, email, password, UserRole.RIDER )
            println("\n  Account created successfully.\n  Welcome, " + user.name + "!")
            return user
        }
        catch (e : CabBookingException) {
            println("[!] Registration failed: " + e.message)
            return null
        }
    }
}