package cab_booking.main

import cab_booking.config.AdminSeeder
import cab_booking.config.DriverSeeder
import cab_booking.controller.AuthController
import cab_booking.exception.DriverNotFoundException
import cab_booking.model.User
import cab_booking.router.UserRouter
fun main(){

    AdminSeeder.seed()
    DriverSeeder.seed()

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
            "1" -> handleSession({AuthController.login()})
            "2" -> handleSession({AuthController.register()})
            "0" -> {
                println("Goodbye! See you next ride.")
                running = false
            }

            else -> println("Invalid choice. Enter 1, 2 or 0.")
        }
    }
}


fun handleSession(action:() -> User?){
        val user = action()
        if (user != null) {
            println("Welcome " + user.name + " !")
            try {
                UserRouter.route(user)
            }
            catch (e : DriverNotFoundException){
                println("[!] ${e.message}. Please try again")
                return
            }
        }
}