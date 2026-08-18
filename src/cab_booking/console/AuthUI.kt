package cab_booking.console

import cab_booking.config.AdminSeeder
import cab_booking.console.input.InputReader
import cab_booking.controller.AuthController
import cab_booking.exception.AccountLockedException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.User

object AuthUI {

    fun login(): User? {

        val email = InputReader.promptEmail()
        val password = InputReader.promptPassword()

        return try {

            val user = AuthController.login(email, password)

            println("\nWelcome back, ${user.name}!")

            user

        }
        catch (e: UserNotFoundException) {

            println("[!] Login failed: ${e.message}")
            null

        }
        catch (e: AccountLockedException) {

            println(
                """
                
                Your account has been temporarily locked
                due to multiple failed login attempts.
                
                ${e.message}
                """.trimIndent()
            )
            null

        }
        catch (e: InvalidCredentialsException) {

            println("[!] Login failed: ${e.message}")
            null

        }
        catch (e: CredentialsNotFoundException) {

            println("[!] Authentication failed: ${e.message}")
            null
        }
    }

    fun register(): User? {

        println(
            """
            
            You can register only as a Rider.
            
            To become a Driver, please contact:
            
            Admin : ${AdminSeeder.ADMIN_NAME}
            Phone : ${AdminSeeder.ADMIN_PHONE}
            Email : ${AdminSeeder.ADMIN_EMAIL}
            """.trimIndent()
        )

        val name = InputReader.promptName()
        val phone = InputReader.promptPhone()

        var email: String

        while (true) {
            email = InputReader.promptEmail()
            if (AuthController.isEmailRegistered(email)) {
                println("[!] This email is already registered. Please use a different email.")
                continue
            }
            break
        }

        val password = InputReader.promptPassword()

        return try {

            val user = AuthController.register(
                name = name,
                phone = phone,
                email = email,
                password = password
            )

            println(
                """
                
                Welcome ${user.name}
                Your rider account has been created successfully.
                You can now book rides.
                """.trimIndent()
            )

            user

        }
        catch (e: IllegalArgumentException) {

            println("[!] Registration failed (invalid input): ${e.message}")

            null
        }
    }
}