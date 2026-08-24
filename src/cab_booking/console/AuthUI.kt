package cab_booking.console

import cab_booking.config.AdminSeeder
import cab_booking.console.input.ConsoleFormater
import cab_booking.console.input.InputReader
import cab_booking.controller.AuthController
import cab_booking.controller.DriverController
import cab_booking.exception.AccountLockedException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.OperationCancelledException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.User
import cab_booking.model.types.UserRole

object AuthUI {

    fun login() {

        try {
            ConsoleFormater.header("LOGIN")

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
            println("\n[x] Login failed: ${e.message}")
        }
        catch (e: CredentialsNotFoundException) {
            println("\n[x] Authentication failed: ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("\n[x] ${e.message}. Please try again.")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled.")
        }
    }

    fun register() {

        try {

            ConsoleFormater.header("REGISTER")

            println(
                """
            
            You can register only as a Rider.
            To become a Driver, please contact: ${AdminSeeder.ADMIN_EMAIL}

            """.trimIndent()
            )

            val name = InputReader.promptName()
            val phone = InputReader.promptPhone()

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
                val confirmPassword = InputReader.promptPassword(prompt = "Confirm Password : ")

                if (password != confirmPassword) {
                    println("\nPasswords do not match.")
                    continue
                }
                break
            }

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
                You can now book rides and send or receive parcels.
                """.trimIndent()
            )

            route(user)

        }
        catch (e: IllegalArgumentException) {
            println("\n[x] Registration failed (invalid input): ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("\n[x] ${e.message}. Please try again.")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled.")
        }
    }

    fun changePassword(user: User) {

        try {
            ConsoleFormater.header("CHANGE PASSWORD")

            val currentPassword = InputReader.promptPassword(prompt = "Current Password (or 'cancel' to go back): ")

            var newPassword: String
            while(true) {
                newPassword = InputReader.promptPassword()
                val confirmPassword = InputReader.promptPassword(prompt = "Confirm Password (or 'cancel' to go back): ")

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
    when (user.userRole) {
        UserRole.ADMIN -> AdminUI.adminDashboard()
        UserRole.DRIVER -> {
            val driver = DriverController.findDriverById(user.userId)
            DriverUI.driverDashboard(driver)
        }
        UserRole.CUSTOMER -> CustomerUI.customerDashboard(user)
    }
}