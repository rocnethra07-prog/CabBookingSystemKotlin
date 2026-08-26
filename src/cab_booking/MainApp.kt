package cab_booking

import cab_booking.config.AdminSeeder
import cab_booking.config.DriverSeeder
import cab_booking.console.AuthUI
import cab_booking.storage.DataStore

fun main() {

    DataStore.loadAll()

    AdminSeeder.seed()
    DriverSeeder.seed()

    println("\n------------------------------------")
    println("Cab Booking & Parcel Delivery Service")
    println("---------------------------")


    while (true) {
        println("\n1. Login")
        println("2. Register")
        println("0. Exit")
        println("Choose: ")

        when (readln().trim()) {

            "1" -> AuthUI.login()

            "2" -> AuthUI.register()

            "0" -> {
                DataStore.saveAll()
                println("Goodbye! See you next ride.")
                return
            }

            else -> {
                println("Invalid choice. Enter 1, 2 or 0.")
            }
        }
    }
}