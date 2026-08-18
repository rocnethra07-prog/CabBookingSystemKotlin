package cab_booking

import cab_booking.config.AdminSeeder
import cab_booking.config.DriverSeeder
import cab_booking.console.AdminUI
import cab_booking.controller.DriverController
import cab_booking.console.AuthUI
import cab_booking.console.DriverUI
import cab_booking.console.RiderUI
import cab_booking.exception.DriverNotFoundException
import cab_booking.model.User
import cab_booking.model.types.UserRole

fun main() {

    AdminSeeder.seed()
    DriverSeeder.seed()

    println("\n---------------------------")
    println("--- CAB BOOKING SERVICE ---")
    println("---------------------------")


    while (true) {
        println("\n1. Login")
        println("2. Register")
        println("0. Exit")
        println("Choose: ")

        when (readln().trim()) {

            "1" -> handleSession { AuthUI.login() }

            "2" -> handleSession { AuthUI.register() }

            "0" -> {
                println("Goodbye! See you next ride.")
                return
            }

            else -> {
                println("Invalid choice. Enter 1, 2 or 0.")
            }
        }
    }
}

private fun handleSession(action: () -> User?) {
    val user = action()

    if(user != null) {
        println("\nWelcome ${user.name}!")

        try {
            route(user)
        }
        catch (e: DriverNotFoundException) {
            println("[!] ${e.message}. Please try again.")
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
        UserRole.RIDER -> RiderUI.riderDashboard(user)
    }
}