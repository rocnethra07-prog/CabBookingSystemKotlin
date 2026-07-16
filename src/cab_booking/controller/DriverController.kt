package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.service.AuthService
import cab_booking.service.DriverService
import cab_booking.util.InputUtil
import exception.AuthenticationException
import exception.InvalidCredentialsException
import exception.UnauthorizedRideActionException

class DriverController(
    private val driverService: DriverService ,
    private val authService: AuthService
) {

    fun driverDashboard(driver: Driver) {

        while (true) {

            println("\n========== DRIVER MENU ==========")
            println("1. View Current Ride")
            println("2. Update Profile")
            println("3. Show Earnings")
            println("4. View Ride History")
            println("5. Change Password")
            println("0. Logout")

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
        val ride = driverService.getCurrentRide(driver)

        if (ride == null) {
            println("\nNo active ride at the moment.")
            return
        }

        println("\n========== CURRENT RIDE ==========")

        println("Pickup Location : ${ride.pickupLocation}")
        println("Drop Location   : ${ride.dropLocation}")
        println("Fare            : ₹${ride.fare}")
        println("Status          : ${ride.rideStatus}")

        rideActionMenu(ride, driver)
    }

    private fun rideActionMenu(
        ride: Ride,
        driver: Driver
    ) {

        while (true) {

            println("\n1. Complete Ride")
            println("2. Cancel Ride")
            println("0. Back")

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

        val name = InputUtil.getOptionalName(driver.name)

        val phone = InputUtil.getOptionalPhone(driver.phone)

        println("Current Location : ${driver.currentLocation}")

        var location = driver.currentLocation

        if (InputUtil.getYesOrNo("Update location?")) {
            location = InputUtil.selectLocation("Select New Location")
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

            driverService.updateProfile(
                driver,
                name,
                phone,
                location
            )

            println("\nProfile Updated Successfully\n")

            println("Name             : ${driver.name}")
            println("Phone            : ${driver.phone}")
            println("Email            : ${driver.email}")
            println("Location         : ${driver.currentLocation}")
            println("License Number   : ${driver.licenseNumber}")

        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, " + e.message)
        }
    }

    // EARNINGS
    private fun showEarnings(driver: Driver) {

        println("\n========== DRIVER EARNINGS ==========")

        println("Total Earnings : ₹${driver.earnings}")

        if (driver.averageRating == 0.0) {
            println("Average Rating : No ratings yet")
        } else {
            println("Average Rating : %.2f".format(driver.averageRating))
        }
    }

    // RIDE HISTORY
    private fun viewRideHistory(driver: Driver) {

        val rides = driverService.getRidesByDriver(driver)

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
            driverService.completeRide(
                ride,
                driver
            )
            println("\nRide completed successfully!")
            showEarnings(driver)
        }
        catch (e: UnauthorizedRideActionException) {
            println("[!] "+ e.message)
        }
        catch (e : IllegalArgumentException){
            println("[!] " +e.message)
        }
    }

    // CANCEL RIDE
    private fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {

        try {
            driverService.cancelRide(
                ride,
                driver
            )
            println("\nRide cancelled successfully.")

        }
        catch (e: UnauthorizedRideActionException) {
            println("[!] "+ e.message)
        }
        catch (e : IllegalArgumentException){
            println("[!] " +e.message)
        }
    }

    // CHANGE PASSWORD
    private fun changePassword(driver: Driver) {

        println("\n========== CHANGE PASSWORD ==========")

        val currentPassword = InputUtil.getPassword(
            prompt = "Current Password : "
        )

        val newPassword = InputUtil.getPassword(
            prompt = "New Password     : "
        )

        val confirmPassword = InputUtil.getPassword(
            prompt = "Confirm Password : "
        )

        if (newPassword != confirmPassword) {
            println("\nPasswords do not match.")
            return
        }

        try {
            authService.changePassword(
                driver,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        }
        catch (e : AuthenticationException){
            println("[!] Authentication Exception, "+ e.message)
        }
        catch (e: InvalidCredentialsException) {
            println("[!] " +e.message)
        }
    }
}