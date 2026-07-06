package cab_booking.controller

import cab_booking.exception.CabBookingException
import cab_booking.model.*
import cab_booking.service.AuthService
import cab_booking.service.RiderService
import cab_booking.util.InputUtil

class RiderController(
    private val riderService: RiderService = RiderService(),
    private val authService: AuthService = AuthService()
) {

    fun riderDashboard(rider: User) {

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

    // --------------------------------------------------
    // BOOK RIDE
    // --------------------------------------------------

    private fun bookRide(rider: User) {

        if (riderService.hasActiveRide(rider)) {
            println("You already have an active ride.")
            return
        }

        println("\n========== BOOK RIDE ==========")

        val pickup = InputUtil.selectLocation("Pickup Location")

        var drop: Location

        while (true) {

            drop = InputUtil.selectLocation("Drop Location")

            if (pickup != drop) break

            println("Pickup and Drop locations cannot be the same.")
        }

        val cabType = InputUtil.selectCabType()

        try {

            val ride = riderService.bookRide(
                rider,
                pickup,
                drop,
                cabType
            )

            val driver = riderService.getDriverForRide(ride)

            println("\nRide Booked Successfully\n")

            println(ride)

            println("\nDriver Details")

            println("Driver : ${driver.name}")
            println("Phone  : ${driver.phone}")

        } catch (e: CabBookingException) {

            println(e.message)
        }
    }

    // --------------------------------------------------
    // CURRENT RIDE
    // --------------------------------------------------

    private fun viewCurrentRide(rider: User) {

        val ride = riderService.getCurrentBookedRide(rider)

        if (ride == null) {

            println("\nNo active ride.")

            return
        }

        val driver = riderService.getDriverForRide(ride)

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
    // --------------------------------------------------
    // CANCEL RIDE
    // --------------------------------------------------

    private fun cancelRide(
        ride: Ride,
        rider: User
    ) {

        try {

            riderService.cancelRide(ride, rider)

            println("\nRide cancelled successfully.")

        } catch (e: CabBookingException) {

            println(e.message)
        }
    }

    // --------------------------------------------------
    // UPDATE PROFILE
    // --------------------------------------------------

    private fun updateProfile(rider: User) {

        println("\n========== UPDATE PROFILE ==========")
        println("(Press Enter to keep the current value)\n")

        val name = InputUtil.getOptionalName(
            currentValue = rider.name
        )

        val phone = InputUtil.getOptionalPhone(
            currentValue = rider.phone
        )

        if (name == rider.name && phone == rider.phone) {

            println("\nNo changes made.")

            return
        }

        try {

            riderService.updateProfile(
                rider,
                name,
                phone
            )

            println("\nProfile Updated Successfully\n")

            println("Name  : ${rider.name}")
            println("Phone : ${rider.phone}")
            println("Email : ${rider.email}")

        } catch (e: CabBookingException) {

            println(e.message)
        }
    }

    // --------------------------------------------------
    // RIDE HISTORY
    // --------------------------------------------------

    private fun viewRideHistory(rider: User) {

        val rides = riderService.getRidesByRider(rider)

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

    // --------------------------------------------------
    // RATE LAST RIDE
    // --------------------------------------------------

    private fun rateLastRide(rider: User) {

        val ride = riderService.getLastCompletedRide(rider)

        if (ride == null) {
            println("\nNo completed ride available for rating.")
            return
        }

        if (ride.rating != null) {
            println("\nThis ride has already been rated.")
            println("Rating : ${ride.rating}/5")
            return
        }

        val driver = riderService.getDriverForRide(ride)

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

            riderService.rateDriver(
                ride,
                rider,
                rating
            )

            println()

            println("Thank you for rating ${driver.name}")

            println("★".repeat(rating) + "☆".repeat(5 - rating))

        } catch (e: CabBookingException) {

            println(e.message)
        }
    }

    // --------------------------------------------------
    // CHANGE PASSWORD
    // --------------------------------------------------

    private fun changePassword(rider: User) {

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
                rider,
                currentPassword,
                newPassword
            )

            println("\nPassword changed successfully.")

        } catch (e: CabBookingException) {

            println(e.message)
        }
    }
}
