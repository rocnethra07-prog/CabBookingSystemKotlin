package cab_booking.controller

import cab_booking.model.User
import cab_booking.model.types.UserRole
import cab_booking.service.AuthService
import cab_booking.util.InputUtil
import cab_booking.exception.AuthenticationException
import cab_booking.exception.UserNotFoundException

class AuthController(
    private val authService: AuthService
) {

    fun login() : User?{

        val email = InputUtil.promptEmail()
        val password = InputUtil.promptPassword()

        try {
            val user: User = authService.loginUser(email, password)
            println("\nWelcome back, " + user.name)
            return user

        }
        catch (e: UserNotFoundException) {
            println("[!] Login failed: " + e.message)
            return null
        }
        catch (e : AuthenticationException){
            println("[!] Authentication failed: ${e.message}")
            return null
        }
    }

    fun register() : User?{
        val name = InputUtil.promptName()
        val phone = InputUtil.promptPhone()
        var email: String
        while (true) {
            email = InputUtil.promptEmail()

            //Pre-check for UX
            if(authService.isEmailRegistered(email)){
                println("! This email is already registered. Please use a different email !")
                continue
            }
            break
        }

        val password = InputUtil.promptPassword()

        try {
            val user: User = authService.registerUser(name, phone, email, password, UserRole.RIDER )
            println("\n  Account created successfully.\n  Welcome, " + user.name + "!")
            return user
        }
        catch (e : IllegalArgumentException) {
            println("[!] Invalid Input,")
            println("[!] Registration failed: ${e.message}")
            return null
        }
    }
}