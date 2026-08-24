package cab_booking.console

import cab_booking.console.input.InputReader
import cab_booking.controller.DriverController
import cab_booking.exception.ActiveParcelNotFoundException
import cab_booking.exception.ActiveRideNotFoundException
import cab_booking.exception.InvalidParcelStateException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.exception.VehicleNotFoundException
import cab_booking.model.Driver
import cab_booking.model.Parcel
import cab_booking.model.Ride
import cab_booking.console.input.ConsoleFormater
import cab_booking.exception.OperationCancelledException
import cab_booking.service.DriverService

object DriverUI {
    fun driverDashboard(driver: Driver){

        autoShowActiveRide(driver)
        autoShowActiveParcel(driver)

        while (true) {

            ConsoleFormater.header("DRIVER MENU")

            println(
                """
                1. My Ride
                2. My Parcel 
                3. Earnings
                4. Vehicle Details
                5. Account
                0. Logout
                """.trimIndent()
            )

            when (readln().trim()) {
                "1" -> activeRideMenu(driver)
                "2" -> activeParcelMenu(driver)
                "3" -> showEarnings(driver)
                "4" -> viewVehicleDetails(driver)
                "5" -> accountMenu(driver)
                "0" -> return

                else -> println("[x] Invalid choice.")
            }
        }
    }

    // CURRENT RIDE
    private fun activeRideMenu(driver: Driver){

        try {
            val ride = DriverController.getCurrentRideOfDriver(driver.userId)

            ConsoleFormater.header("CURRENT RIDE")
            println(ride)
            ConsoleFormater.divider()

            rideDetailsFlow(ride, driver)

        }
        catch (e : ActiveRideNotFoundException){
            println("[x] ${e.message}")
        }
    }

    private fun autoShowActiveRide(driver: Driver) {
        if(DriverService.hasActiveRideForDriver(driver.userId)){
            activeRideMenu(driver)
        }
    }

    private fun rideDetailsFlow(ride: Ride, driver: Driver) {
        while (true) {
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> {
                    rideActionMenu(ride, driver)
                    return
                }
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }

    private fun rideActionMenu(
        ride: Ride, driver: Driver
    ) {

        while (true) {
            println(
                """
                
                1. Start Ride
                2. Complete Ride
                3. Cancel Ride
                0. Back
                """.trimIndent()
            )
            print("Choose: ")
            try {
                when (readln().trim()) {

                    "1" -> {
                        startRide(ride, driver)
                        return
                    }

                    "2" -> {
                        completeRide(ride, driver)
                        return
                    }

                    "3" -> {
                        cancelRide(ride, driver)
                        return
                    }

                    "0" -> return

                    else -> println("[x] Invalid choice.")
                }
            }
            catch (e: UnauthorizedRideActionException) {
                println("[x] ${e.message}")
            }
            catch (e: InvalidRideStateException){
                println("[x] ${e.message}")
            }
            catch (e : IllegalArgumentException){
                println("[x] ${e.message}")
            }
        }
    }

    private fun startRide(ride: Ride, driver: Driver){
        DriverController.startRide(ride, driver)
        println("\nRide completed successfully!")
    }

    private fun completeRide(ride: Ride, driver: Driver) {
        DriverController.completeRide(ride, driver)
        println("\nRide completed successfully!")
        showEarnings(driver)
    }

    private fun cancelRide(ride: Ride, driver: Driver) {
        DriverController.cancelRide(ride, driver)
        println("\nRide cancelled successfully.")
    }

    private fun activeParcelMenu(driver: Driver) {
        try {
            val parcel = DriverController.getCurrentParcelOfDriver(driver.userId)

            ConsoleFormater.header("CURRENT PARCEL")
            println(parcel)
            ConsoleFormater.divider()

            parcelDetailsFlow(parcel, driver)
        }
        catch (e: ActiveParcelNotFoundException) {
            println("[!] ${e.message}")
        }
    }

    private fun autoShowActiveParcel(driver: Driver){
        if(DriverService.hasActiveParcelForDriver(driver.userId)){
            activeParcelMenu(driver)
        }
    }

    private fun parcelDetailsFlow(parcel: Parcel, driver: Driver) {
        while (true) {
            println("1. Show Details")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> {
                    parcelActionMenu(parcel, driver)
                    return
                }
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }
    private fun parcelActionMenu(parcel: Parcel, driver: Driver) {
        while (true) {
            println(
                """
 
                1. Pick Up Parcel
                2. Deliver Parcel
                3. Cancel Parcel
                0. Back
                """.trimIndent()
            )
            print("Choose: ")

            try {
                when (readln().trim()) {
                    "1" -> {
                        pickUpParcel(parcel, driver)
                        return
                    }
                    "2" -> {
                        deliverParcel(parcel, driver)
                        return
                    }
                    "3" -> {
                        cancelParcel(parcel, driver)
                        return
                    }
                    "0" -> return
                    else -> println("Invalid choice.")
                }
            }
            catch (e: UnauthorizedParcelActionException) {
                println("[x] ${e.message}")
            }
            catch (e: InvalidParcelStateException) {
                println("[x] ${e.message}")
            }
            catch (e: IllegalArgumentException) {
                println("[x] ${e.message}")
            }
        }
    }

    private fun pickUpParcel(parcel: Parcel, driver: Driver) {
        DriverController.pickUpParcel(parcel, driver)
        println("\nParcel picked up.")
    }

    private fun deliverParcel(parcel: Parcel, driver: Driver) {
        DriverController.deliverParcel(parcel, driver)
        println("\nParcel delivered successfully!")
        showEarnings(driver)
    }

    private fun cancelParcel(parcel: Parcel, driver: Driver) {
        DriverController.cancelParcel(parcel, driver)
        println("\nParcel cancelled successfully.")
    }

    fun showEarnings(driver: Driver){

        ConsoleFormater.header("DRIVER EARNINGS")

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
        try {

            ConsoleFormater.header("UPDATE PROFILE")

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
            println("[x] Invalid Input, " + e.message)
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled. No changes made.")
        }
    }

    private fun viewVehicleDetails(driver: Driver){
        try {
            ConsoleFormater.header("ASSIGNED VEHICLE")
            println(DriverController.getVehicleById(driver.vehicleId))
        }
        catch (e: VehicleNotFoundException) {
            println("[x] ${e.message}")
        }
    }

    private fun accountMenu(driver: Driver) {
        while (true) {

            while (true) {

                ConsoleFormater.header("MY PROFILE")
                println(driver)
                println()
                println("1. Show Options")
                println("0. Close")
                println()

                when (readln().trim()) {
                    "1" -> profileOptionsMenu(driver)
                    "0" -> return
                    else -> println("[x] Invalid choice.")
                }
            }
        }
    }

    private fun profileOptionsMenu(driver: Driver) {
        while (true) {
            println("\n1. Update Profile")
            println("2. Change Password")
            println("3. Show Earnings")
            println("0. Back")
            println()

            when (readln().trim()) {
                "1" -> {
                    updateProfile(driver)
                    return
                }
                "2" -> {
                    AuthUI.changePassword(driver)
                    return
                }
                "3" -> {
                    showEarnings(driver)
                    return
                }
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }
}


