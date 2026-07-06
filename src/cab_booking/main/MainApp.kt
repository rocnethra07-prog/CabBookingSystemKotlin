package cab_booking.main

import cab_booking.config.AdminSeeder
import cab_booking.controller.AdminController
import cab_booking.controller.AuthController
import cab_booking.controller.DriverController
import cab_booking.controller.RiderController
import cab_booking.model.User
import cab_booking.router.UserRouter
import cab_booking.service.AuthService
import cab_booking.service.DriverService

fun main(){

    val authService = AuthService()
    val driverService = DriverService()

    val authController = AuthController(authService)
    val adminController = AdminController()
    val driverController = DriverController()
    val riderController = RiderController()

    val router = UserRouter(adminController,driverController,riderController,driverService)

    AdminSeeder.seed(authService)

    println("\n---------------------------")
    println("--- CAB BOOKING SERVICE ---")
    println("---------------------------")

    var running = true
    while (running) {
        println("1. Login")
        println("2. Register as a Rider")
        println("0. Exit ")
        println("Choose: ")
        val choice = readln().trim()
        when (choice) {
            "1" -> handleSession({authController.login()}, router)
            "2" -> handleSession({authController.register()}, router)
            "0" -> {
                println("Goodbye! See you next ride.")
                running = false
            }

            else -> println("Invalid choice. Enter 1, 2 or 0.")
        }
    }
}


fun handleSession(action:() -> User?, userRouter: UserRouter){
    val user = action()
    if (user != null) {
        println("Welcome " + user.name + " !")
        userRouter.route(user)
    }
}