package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.service.AuthService
import cab_booking.service.DriverService
import cab_booking.console.ConsoleInput
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedRideActionException

object DriverController{

    fun driverDashboard(driver: Driver) {

        while (true) {

            println(
                """
    
                ========== DRIVER MENU ==========
                1. View Current Ride
                2. Update Profile
                3. Show Earnings
                4. View Ride History
                5. Change Password
                0. Logout
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> viewCurrentRide(driver)

                "2" -> updateProfile(driver)

                "3" -> showEarnings(driver)

                "4" -> viewRideHistory(driver)

                "5" -> changePassword(driver)

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    // CURRENT RIDE
    private fun viewCurrentRide(driver: Driver) {
        val ride = DriverService.getCurrentRide(driver)

        if (ride == null) {
            println("\nNo active ride at the moment.")
            return
        }

        println(
            """
    
            ========== CURRENT RIDE ==========
            Pickup Location : ${ride.pickupLocation}
            Drop Location   : ${ride.dropLocation}
            Fare            : ₹${ride.fare}
            Status          : ${ride.rideStatus}
            """.trimIndent()
        )

        rideActionMenu(ride, driver)
    }

    private fun rideActionMenu(
        ride: Ride,
        driver: Driver
    ) {

        while (true) {

            println(
                """
    
                1. Complete Ride
                2. Cancel Ride
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> {
                    completeRide(ride, driver)
                    return
                }

                "2" -> {
                    cancelRide(ride, driver)
                    return
                }

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }
    // UPDATE PROFILE
    private fun updateProfile(driver: Driver) {

        println("\n========== UPDATE PROFILE ==========")
        println("(Press Enter to keep the current value)\n")

        val name = ConsoleInput.promptOptionalName(driver.name)

        val phone = ConsoleInput.promptOptionalPhone(driver.phone)

        println("Current Location : ${driver.currentLocation}")

        var location = driver.currentLocation

        if (ConsoleInput.promptConfirmation("Update location?")) {
            location = ConsoleInput.chooseLocation("Select New Location")
        }

        if (
            name == driver.name &&
            phone == driver.phone &&
            location == driver.currentLocation
        ) {
            println("\nNo changes made.")
            return
        }

        try {

            DriverService.updateProfile(
                driver,
                name,
                phone,
                location
            )

            println(
                """
    
                Profile Updated Successfully
    
                Name             : ${driver.name}
                Phone            : ${driver.phone}
                Email            : ${driver.email}
                Location         : ${driver.currentLocation}
                License Number   : ${driver.licenseNumber}
                """.trimIndent()
            )

        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, " + e.message)
        }
    }

    // EARNINGS
    private fun showEarnings(driver: Driver) {

        println("\n========== DRIVER EARNINGS ==========")

        println("Total Earnings : ₹${driver.earnings}")

        val averageRating = DriverService.getAverageRatingOfDriver(driver)

        if (averageRating == 0.0) {
            println("Average Rating : No ratings yet")
        }
        else {
            println("Average Rating : %.2f".format(averageRating))
        }
    }

    // RIDE HISTORY
    private fun viewRideHistory(driver: Driver) {

        val rides = DriverService.getRidesByDriver(driver)

        if (rides.isEmpty()) {
            println("\nNo rides found.")
            return
        }

        println("\n========== RIDE HISTORY ==========")

        rides.forEach {
            println(it)
            println("-".repeat(50))
        }
    }
    // COMPLETE RIDE
    private fun completeRide(
        ride: Ride,
        driver: Driver
    ) {

        try {
            DriverService.completeRide(
                ride,
                driver
            )
            println("\nRide completed successfully!")
            showEarnings(driver)
        }
        catch (e: UnauthorizedRideActionException) {
            println("[!] ${e.message}")
        }
        catch (e: InvalidRideStateException){
            println("[!] ${e.message}")
        }
        catch (e : IllegalArgumentException){
            println("[!] ${e.message}")
        }
    }

    // CANCEL RIDE
    private fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {

        try {
            DriverService.cancelRide(
                ride,
                driver
            )
            println("\nRide cancelled successfully.")

        }
        catch (e: UnauthorizedRideActionException) {
            println("[!] ${e.message}")
        }
        catch (e: InvalidRideStateException){
            println("[!] ${e.message}")
        }
        catch (e : IllegalArgumentException){
            println("[!] ${e.message}")
        }
    }

    // CHANGE PASSWORD
    private fun changePassword(driver: Driver) {

        println("\n========== CHANGE PASSWORD ==========")

        val currentPassword = ConsoleInput.promptPassword(
            prompt = "Current Password : "
        )

        val newPassword = ConsoleInput.promptPassword(
            prompt = "New Password     : "
        )

        val confirmPassword = ConsoleInput.promptPassword(
            prompt = "Confirm Password : "
        )

        if (newPassword != confirmPassword) {
            println("\nPasswords do not match.")
            return
        }

        try {
            AuthService.changePassword(
                driver,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        }
        catch (e : CredentialsNotFoundException){
            println("[!] Authentication Exception, ${e.message}")
        }
        catch (e: InvalidCredentialsException) {
            println("[!] ${e.message}")
        }
    }
}