package cab_booking.controller

import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.service.AuthService
import cab_booking.service.RiderService
import cab_booking.util.InputUtil
import cab_booking.exception.AuthenticationException
import cab_booking.exception.CabNotFoundException
import cab_booking.exception.DistanceNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.DriverUnavailableException
import cab_booking.exception.InvalidCredentialsException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.Ride

object RiderController{

    fun riderDashboard(rider: User) {

        promptPendingRating(rider)

        while (true) {
            println("\n========== RIDER MENU ==========")
            println("1. Book Ride")
            println("2. View Current Ride")
            println("3. View Ride History")
            println("4. Update Profile")
            println("5. Rate Last Ride")
            println("6. Change Password")
            println("0. Logout")

            when (readln().trim()) {

                "1" -> bookRide(rider)

                "2" -> viewCurrentRide(rider)

                "3" -> viewRideHistory(rider)

                "4" -> updateProfile(rider)

                "5" -> rateLastRide(rider)

                "6" -> changePassword(rider)

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun promptPendingRating(rider: User) {

        val ride = RiderService.getLastCompletedRide(rider)

        if (ride == null || ride.rating != null) {
            return
        }

        println("\nYou haven't rated your last ride yet.")

        if (InputUtil.promptConfirmation("Would you like to rate it now?")) {
            showRatingScreen(ride, rider)
        }
    }

    // BOOK RIDE
    private fun bookRide(rider: User) {

        if (RiderService.hasActiveRide(rider)) {
            println("You already have an active ride.")
            return
        }

        println("\n========== BOOK RIDE ==========")

        val pickup = InputUtil.chooseLocation("Pickup Location")

        var drop: Location

        while (true) {

            drop = InputUtil.chooseLocation("Drop Location")

            if (pickup != drop) break

            println("Pickup and Drop locations cannot be the same.")
        }

        while (true) {

            val cabType = InputUtil.chooseCabType()

            try {

                val ride = RiderService.bookRide(
                    rider,
                    pickup,
                    drop,
                    cabType
                )

                val driver = RiderService.getDriverForRide(ride)

                println("\nRide Booked Successfully.\n")
                println(ride)

                println("\nDriver Details")
                println("Driver : ${driver.name}")
                println("Phone  : ${driver.phone}")

                return

            } catch (e: DriverUnavailableException) {

                println("[!] ${e.message}")

                if (!InputUtil.promptConfirmation("Try another cab type? (Y/N): ")) {
                    return
                }

            } catch(e: DistanceNotFoundException){

                println("[!] ${e.message}")

            }catch (e: DriverNotFoundException) {

                println("[!] ${e.message}")
            }
            catch (e: CabNotFoundException){
                println("[!] ${e.message}")
            }
            catch (e: IllegalArgumentException) {

                println("[!] ${e.message}")
            }
        }
    }

    // CURRENT RIDE
    private fun viewCurrentRide(rider: User) {

        val ride = RiderService.getCurrentBookedRide(rider)

        if (ride == null) {
            println("\nNo active ride.")
            return
        }

        val driver = RiderService.getDriverForRide(ride)

        println("\n========== CURRENT RIDE ==========")

        println("Pickup Location : ${ride.pickupLocation}")
        println("Drop Location   : ${ride.dropLocation}")
        println("Fare            : ₹${ride.fare}")
        println("Driver          : ${driver.name}")
        println("Phone           : ${driver.phone}")
        println("Status          : ${ride.rideStatus}")

        currentRideMenu(ride, rider)
    }

    private fun currentRideMenu(
        ride: Ride,
        rider: User
    ) {

        while (true) {

            println("\n1. Cancel Ride")
            println("0. Back")

            when (readln().trim()) {

                "1" -> {
                    cancelRide(ride, rider)
                    return
                }

                "0" -> return
                else -> println("Invalid choice.")
            }
        }
    }

    // CANCEL RIDE
    private fun cancelRide(
        ride: Ride,
        rider: User
    ) {
        try {
            RiderService.cancelRide(ride, rider)
            println("\nRide cancelled successfully.")
        }
        catch (e: UnauthorizedRideActionException) {
            println("[!] ${e.message}")
        }
        catch (e : InvalidRideStateException){
            println("[!] ${e.message}")
        }
        catch (e : DriverNotFoundException){
            println("[!] ${e.message}")
        }
    }

    // UPDATE PROFILE
    private fun updateProfile(rider: User) {

        println("\n========== UPDATE PROFILE ==========")
        println("(Press Enter to keep the current value)\n")

        val name = InputUtil.promptOptionalName(rider.name)

        val phone = InputUtil.promptOptionalPhone(rider.phone)

        if (name == rider.name && phone == rider.phone) {
            println("\nNo changes made.")
            return
        }

        try {
            RiderService.updateProfile(
                rider,
                name,
                phone
            )

            println("\nProfile Updated Successfully\n")
            println("Name  : ${rider.name}")
            println("Phone : ${rider.phone}")
            println("Email : ${rider.email}")

        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, ${e.message}")
        }
    }

    // RIDE HISTORY

    private fun viewRideHistory(rider: User) {

        val rides = RiderService.getRidesByRider(rider)

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

    // RATE LAST RIDE
    private fun rateLastRide(rider: User) {

        val ride = RiderService.getLastCompletedRide(rider)

        if (ride == null) {
            println("\nNo completed ride available for rating.")
            return
        }

        if (ride.rating != null) {
            println("\nThis ride has already been rated.")
            println("Rating : ${ride.rating}/5")
            return
        }

        showRatingScreen(ride, rider)
    }

    private fun showRatingScreen(
        ride: Ride,
        rider: User
    ) {
        val driver = RiderService.getDriverForRide(ride)

        println("\n========== RATE RIDE ==========")
        println("Driver : ${driver.name}")
        println("Pickup : ${ride.pickupLocation}")
        println("Drop   : ${ride.dropLocation}")
        println("Fare   : ₹${ride.fare}")

        submitRating(ride, rider, driver)
    }

    private fun submitRating(
        ride: Ride,
        rider: User,
        driver: Driver
    ) {

        println()
        println("1 ★  Poor")
        println("2 ★★ Fair")
        println("3 ★★★ Good")
        println("4 ★★★★ Very Good")
        println("5 ★★★★★ Excellent")

        var rating: Int

        while (true) {

            print("\nEnter Rating (1-5): ")

            rating = readln().toIntOrNull() ?: run {
                println("Invalid rating.")
                continue
            }

            if (rating in 1..5) break

            println("Rating must be between 1 and 5.")
        }

        try {

            RiderService.rateDriver(
                ride,
                rider,
                rating
            )
            println("\nThank you for rating ${driver.name}")
            println("★".repeat(rating) + "☆".repeat(5 - rating))

        }
        catch (e: UnauthorizedRideActionException) {
            println("[!] ${e.message}")

        }
        catch (e: DriverNotFoundException) {
            println("[!] Unable to submit your rating at the moment")
            println(e.message)
        }
        catch (e : IllegalArgumentException){
            println("[!] ${e.message}")
        }
    }

    // CHANGE PASSWORD
    private fun changePassword(rider: User) {

        println("\n========== CHANGE PASSWORD ==========")

        val currentPassword = InputUtil.promptPassword(
            prompt = "Current Password : "
        )

        val newPassword = InputUtil.promptPassword(
            prompt = "New Password     : "
        )

        val confirmPassword = InputUtil.promptPassword(
            prompt = "Confirm Password : "
        )

        if (newPassword != confirmPassword) {
            println("\nPasswords do not match.")
            return
        }

        try {
            AuthService.changePassword(
                rider,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        }
        catch (e : AuthenticationException){
            println("[!] Authentication failed: ${e.message}")
        }
        catch (e: InvalidCredentialsException) {
            println("[!] ${e.message}")
        }
    }
}
