package cab_booking.console

import cab_booking.config.AdminSeeder
import cab_booking.console.input.ConsoleFormat
import cab_booking.console.input.InputReader
import cab_booking.controller.AuthController
import cab_booking.controller.DriverController
import cab_booking.exception.AccountLockedException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.User
import cab_booking.model.types.UserRole

object AuthUI {

    fun login() {

        ConsoleFormat.header("LOGIN")

        val email = InputReader.promptEmail()
        val password = InputReader.promptPassword()

        try {

            val user = AuthController.login(email, password)

            println("\nWelcome back, ${user.name}!")

            route(user)

        }
        catch (e: UserNotFoundException) {
            println("[x] Login failed: ${e.message}")
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
            println("[x] Login failed: ${e.message}")
        }
        catch (e: CredentialsNotFoundException) {
            println("[x] Authentication failed: ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("[x] ${e.message}. Please try again.")
        }
    }

    fun register() {

        ConsoleFormat.header("REGISTER")

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
                println("[!] This email is already registered. Please use a different email.")
                continue
            }
            break
        }

        val password = InputReader.promptPassword()

        try {

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
            println("[x] Registration failed (invalid input): ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("[x] ${e.message}. Please try again.")
        }
    }

    fun changePassword(user: User) {

        ConsoleFormat.header("CHANGE PASSWORD")

        val currentPassword = InputReader.promptPassword(prompt = "Current Password : ")
        val newPassword = InputReader.promptPassword(prompt = "New Password     : ")
        val confirmPassword = InputReader.promptPassword(prompt = "Confirm Password : ")

        if (newPassword != confirmPassword) {
            println("\nPasswords do not match.")
            return
        }

        try {
            AuthController.changePassword(
                user,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        }
        catch (e : CredentialsNotFoundException){
            println("[x] Authentication failed: ${e.message}")
        }
        catch (e: AccountLockedException){
            println("[x] ${e.message}")
        }
        catch (e: InvalidCredentialsException) {
            println("[x] ${e.message}")
        }
        catch (e: IllegalArgumentException) {
            println("[x] ${e.message}")
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
        UserRole.CUSTOMER -> RiderUI.riderDashboard(user)
    }
}