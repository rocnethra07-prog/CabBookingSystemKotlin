package cab_booking.console

import cab_booking.config.AdminSeeder
import cab_booking.console.input.ConsoleFormatter
import cab_booking.console.input.InputReader
import cab_booking.controller.AuthController
import cab_booking.exception.AccountLockedException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.OperationCancelledException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.Admin
import cab_booking.model.Customer
import cab_booking.model.Driver
import cab_booking.model.User

object AuthUI {

    fun login() {

        try {
            ConsoleFormatter.header("LOGIN")

            val email = InputReader.promptEmail()
            val password = InputReader.promptPassword()

            val user = AuthController.login(email, password)

            println("\nWelcome back, ${user.name}!")

            route(user)

        }
        catch (e: UserNotFoundException) {
            println("\n[x] Login failed: ${e.message}")
        }
        catch (e: AccountLockedException) {
            println(
                """
                
                Your account has been temporarily locked due to multiple failed login attempts.
                Need Help? Contact : ${AdminSeeder.ADMIN_EMAIL}
         
                ${e.message}
                
            """.trimIndent()
            )
        }
        catch (e: InvalidCredentialsException) {
            println("\n[x] ${e.message}")
        }
        catch (e: CredentialsNotFoundException) {
            println("\n[x] ${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled.")
        }
    }

    fun register() {

        try {

            ConsoleFormatter.header("REGISTER")

            println(
                """
            
            You can register only as a Rider.
            To become a Driver, please contact: ${AdminSeeder.ADMIN_EMAIL}

            """.trimIndent()
            )

            val name = InputReader.promptName()
            val phoneNumber = InputReader.promptPhoneNumber()

            var email: String

            while (true) {
                email = InputReader.promptEmail()
                if (AuthController.isEmailRegistered(email)) {
                    println("\n[x] This email is already registered. Please use a different email.")
                    continue
                }
                break
            }

            var password: String
            while(true) {
                password = InputReader.promptPassword()
                val confirmPassword = InputReader.promptPassword(prompt = "Confirm Password")

                if (password != confirmPassword) {
                    println("\nPasswords do not match.")
                    continue
                }
                break
            }

            val user = AuthController.register(
                name = name,
                phoneNumber = phoneNumber,
                email = email,
                password = password
            )

            println(
                """
                
                Welcome ${user.name}
                Your rider account has been created successfully.
                You can now book rides and send or receive parcels.
                """.trimIndent()
            )

            route(user)

        }
        catch (e: IllegalArgumentException) {
            println("\n[x] Registration failed (invalid input): ${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled.")
        }
    }

    fun changePassword(user: User) {

        try {
            ConsoleFormatter.header("CHANGE PASSWORD")

            val currentPassword = InputReader.promptPassword(prompt = "Current Password")

            var newPassword: String
            while(true) {
                newPassword = InputReader.promptPassword()
                val confirmPassword = InputReader.promptPassword(prompt = "Confirm Password")

                if (newPassword != confirmPassword) {
                    println("\nPasswords do not match.")
                    continue
                }
                break
            }
            AuthController.changePassword(
                user,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        }
        catch (e : CredentialsNotFoundException){
            println("\n[x] Authentication failed: ${e.message}")
        }
        catch (e: AccountLockedException){
            println("\n[x] ${e.message}")
        }
        catch (e: InvalidCredentialsException) {
            println("\n[x] ${e.message}")
        }
        catch (e: IllegalArgumentException) {
            println("\n[x] ${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled. Your password was not changed.")
        }
    }
}

private fun route(user: User) {
    when (user) {
        is Admin -> AdminUI.adminDashboard()
        is Driver -> DriverUI.driverDashboard(user)
        is Customer -> CustomerUI.customerDashboard(user)
    }

}