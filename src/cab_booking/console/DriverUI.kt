package cab_booking.console

import cab_booking.console.input.InputReader
import cab_booking.controller.DriverController
import cab_booking.exception.AccountLockedException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.Ride

object DriverUI {
    fun driverDashboard(driver: Driver){
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
    private fun viewCurrentRide(driver: Driver){
        val ride = DriverController.getCurrentRideOfDriver(driver)

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
        ride: Ride, driver: Driver
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

    private fun completeRide(ride: Ride, driver: Driver) {
        try {
            DriverController.completeRide(
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

    private fun cancelRide(ride: Ride, driver: Driver) {
        try {
            DriverController.cancelRide(
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

    fun showEarnings(driver: Driver){

        println("\n========== DRIVER EARNINGS ==========")

        println("Total Earnings : ₹${driver.earnings}")

        val averageRating = DriverController.getAverageRatingOfDriver(driver)

        if (averageRating == 0.0) {
            println("Average Rating : No ratings yet")
        }
        else {
            println("Average Rating : %.2f".format(averageRating))
        }
    }


    fun updateProfile(driver: Driver) {
        println("\n========== UPDATE PROFILE ==========")
        println("(Press Enter to keep the current value)\n")

        val name = InputReader.promptOptionalName(driver.name)

        val phone = InputReader.promptOptionalPhone(driver.phone)

        println("Current Location : ${driver.currentLocation}")

        var location = driver.currentLocation

        if (InputReader.promptConfirmation("Update location?")) {
            location = InputReader.chooseLocation("Select New Location")
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

            DriverController.updateProfile(
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

        } catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, " + e.message)
        }

    }

    private fun viewRideHistory(driver: Driver) {
        val rides = DriverController.getRidesByDriver(driver)

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

    // CHANGE PASSWORD
    private fun changePassword(driver: Driver) {

        println("\n========== CHANGE PASSWORD ==========")

        val currentPassword = InputReader.promptPassword(
            prompt = "Current Password : "
        )

        val newPassword = InputReader.promptPassword(
            prompt = "New Password     : "
        )

        val confirmPassword = InputReader.promptPassword(
            prompt = "Confirm Password : "
        )

        if (newPassword != confirmPassword) {
            println("\nPasswords do not match.")
            return
        }

        try {
            DriverController.changePassword(
                driver,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        }
        catch (e: CredentialsNotFoundException){
            println("[!] Authentication Exception, ${e.message}")
        }
        catch (e: AccountLockedException){
            println("[!] ${e.message}")
        }
        catch (e: InvalidCredentialsException) {
            println("[!] ${e.message}")
        }
        catch (e: IllegalArgumentException){
            println("[!] Invalid Input, ${e.message}")
        }
    }
}


