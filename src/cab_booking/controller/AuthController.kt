package cab_booking.controller

import cab_booking.config.AdminSeeder
import cab_booking.model.User
import cab_booking.model.types.UserRole
import cab_booking.service.AuthService
import cab_booking.console.ConsoleInput
import cab_booking.exception.AccountLockedException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.UserNotFoundException

object AuthController{

    fun login() : User?{

        val email = ConsoleInput.promptEmail()
        val password = ConsoleInput.promptPassword()

        try {
            val user: User = AuthService.loginUser(email, password)
            println("\nWelcome back, " + user.name)
            return user

        }
        catch (e: UserNotFoundException) {
            println("[!] Login failed: ${e.message}")
            return null
        }
        catch (e: AccountLockedException){
            println("""
                
                Your account has been temporarily locked
                due to multiple failed login attempts.
            """.trimIndent()
            )
            println("${e.message}")

            if(!e.isAdmin) {
                println(
                    """
                Need help?
                Admin : ${AdminSeeder.ADMIN_NAME}
                Phone : ${AdminSeeder.ADMIN_PHONE}
                Email : ${AdminSeeder.ADMIN_EMAIL}
            """.trimIndent()
                )
            }
            return null
        }
        catch (e: InvalidCredentialsException){
            println("[!] Login failed: ${e.message}")
            return null
        }
        catch (e : CredentialsNotFoundException){
            println("[!] Authentication failed: ${e.message}")
            return null
        }
    }

    fun register() : User?{
        println("""
            You can register only as a Rider.

            To become a Driver, please contact:

            Admin : ${AdminSeeder.ADMIN_NAME}
            Phone : ${AdminSeeder.ADMIN_PHONE}
            Email : ${AdminSeeder.ADMIN_EMAIL}
        """.trimIndent())
        val name = ConsoleInput.promptName()
        val phone = ConsoleInput.promptPhone()
        var email: String
        while (true) {
            email = ConsoleInput.promptEmail()

            //Pre-check for UX
            if(AuthService.isEmailRegistered(email)){
                println("! This email is already registered. Please use a different email !")
                continue
            }
            break
        }

        val password = ConsoleInput.promptPassword()

        try {
            val user: User = AuthService.registerUser(name, phone, email, password, UserRole.RIDER )

            println("""
                
                Welcome ${user.name}
                Your rider account has been created successfully
                You can now book rides
            """.trimIndent())
            return user
        }
        catch (e : IllegalArgumentException) {
            println("[!] Registration failed (invalid input) : ${e.message}")
            return null
        }
    }
}