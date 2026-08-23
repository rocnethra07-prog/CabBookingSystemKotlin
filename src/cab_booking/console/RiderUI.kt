package cab_booking.console

import cab_booking.console.input.InputReader
import cab_booking.controller.RiderController
import cab_booking.exception.ActiveRideNotFoundException
import cab_booking.exception.AvailableDriversNotFoundException
import cab_booking.exception.CompletedRideNotFoundException
import cab_booking.exception.DistanceNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.VehicleNotFoundException
import cab_booking.exception.ActiveParcelNotFoundException
import cab_booking.exception.InvalidParcelStateException
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.Parcel
import cab_booking.model.Ride
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelMode
import cab_booking.console.input.ConsoleFormat

object RiderUI {
    fun riderDashboard(customer: User){
        promptPendingRating(customer)

        while (true) {
            ConsoleFormat.header("RIDER MENU")
            println(
                """
                1. Book a Ride
                2. Send / Receive Parcel
                3. My Ride
                4. My Parcel 
                5. Account
                0. Logout
                """.trimIndent()
            )
            when (readln().trim()) {

                "1" -> bookRide(customer)
                "2" -> bookParcel(customer)
                "3" -> viewCurrentRide(customer)
                "4" -> viewCurrentParcel(customer)
                "5" -> accountMenu(customer)
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }

    private fun promptPendingRating(rider: User) {

        try {
            val ride = RiderController.getLastCompletedRideOfRider(rider.userId)

            if (ride.rating != 0) {
                return
            }

            println("\nYou haven't rated your last ride yet.")

            if (InputReader.promptConfirmation("Would you like to rate it now?")) {
                showRatingScreen(ride, rider)
            }

        }
        catch (_ : CompletedRideNotFoundException){}
    }


    private fun showRatingScreen(
        ride: Ride, rider: User
    ) {
        val driver = RiderController.getDriverForRide(ride)

        println(
            """
    
            ========== RATE RIDE ==========
            Driver : ${driver.name}
            Pickup : ${ride.pickupLocation}
            Drop   : ${ride.dropLocation}
            Fare   : ₹${ride.fare}
            """.trimIndent()
        )

        submitRating(ride, rider, driver)
    }

    private fun submitRating(
        ride: Ride,
        rider: User,
        driver: Driver
    ) {

        println(
            """
                
            1 ★  Poor
            2 ★★ Fair
            3 ★★★ Good
            4 ★★★★ Very Good
            5 ★★★★★ Excellent    
               
            """.trimIndent()
        )

        var rating: Int

        while (true) {

            print("\nEnter Rating (1-5): ")

            rating = readln().toIntOrNull() ?: run {
                println("Invalid rating.")
                continue
            }

            if (rating !in 1..5) {
                println("Rating must be between 1 and 5.")
                continue
            }
            break
        }

        try {

            RiderController.rateDriver(
                ride,
                rider,
                rating
            )
            println("\nThank you for rating ${driver.name}")
            println("★".repeat(rating) + "☆".repeat(5 - rating))

        }
        catch (e: UnauthorizedRideActionException) {
            println("[x] Unable to submit your rating at the moment ${e.message}")
        }
        catch (e : InvalidRideStateException){
            println("[x] Unable to submit your rating at the moment ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("[x] Unable to submit your rating at the moment ${e.message}")
        }
        catch (e : IllegalArgumentException){
            println("[x] Invalid Input, Unable to submit rating at the moment${e.message}")
        }
    }

    private fun bookRide(rider: User) {

        if (RiderController.hasActiveRide(rider.userId)) {
            println("You already have an active ride.")
            return
        }

        println("\n========== BOOK RIDE ==========")

        val pickup = InputReader.chooseLocation("Pickup Location")

        var drop: Location

        while (true) {

            drop = InputReader.chooseLocation("Drop Location")

            if (pickup == drop) {
                println("Pickup and Drop locations cannot be the same.")
                continue
            }
            break
        }

        val fareEstimates = try {
            RiderController.estimateRideFares(pickup, drop)
        }
        catch (e: DistanceNotFoundException) {
            println("[x] ${e.message}")
            return
        }

        while (true) {

            val vehicleCategory = InputReader.chooseVehicleCategoryByFare(fareEstimates)

            println("\nFinding a nearby $vehicleCategory for you...")

            try {

                val ride = RiderController.bookRide(
                    rider,
                    pickup,
                    drop,
                    vehicleCategory
                )

                val driver = RiderController.getDriverForRide(ride)

                ConsoleFormat.subHeader("RIDE CONFIRMED")
                println(ride)
                println(
                    """
                        
                    Driver Details :
                    Driver : ${driver.name}
                    Phone  : ${driver.phone}
                    """.trimIndent()
                )

                return

            }
            catch(e: AvailableDriversNotFoundException){
                println("[x] ${e.message}")
                if(InputReader.promptConfirmation("Try another vehicle type? (Y/N): ")){
                    continue
                }
                return
            }
            catch(e: DistanceNotFoundException) {
                println("[x] ${e.message}")
                return
            }
            catch (e: DriverNotFoundException) {
                println("[x] ${e.message}")
                return
            }
            catch (e: VehicleNotFoundException) {
                println("[x] ${e.message}")
                return
            }
            catch (e: IllegalArgumentException) {
                println("[x] ${e.message}")
                return
            }
        }
    }

    fun viewCurrentRide(rider: User){

        try {
            val ride = RiderController.getCurrentBookedRide(rider.userId)

            val driver = RiderController.getDriverForRide(ride)

            println(
                """
   
            ========== CURRENT RIDE ==========
            Pickup Location : ${ride.pickupLocation}
            Drop Location   : ${ride.dropLocation}
            Fare            : ₹${ride.fare}
            Driver          : ${driver.name}
            Phone           : ${driver.phone}
            Status          : ${ride.rideStatus}
            """.trimIndent()
            )

            currentRideMenu(ride, rider)
        }
        catch (e : ActiveRideNotFoundException){
            println("[!] ${e.message}")
        }
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

    private fun cancelRide(
        ride: Ride, rider: User
    ) {
        try {
            RiderController.cancelRide(ride, rider)
            println("\nRide cancelled successfully.")
        }
        catch (e: UnauthorizedRideActionException) {
            println("[x] ${e.message}")
        }
        catch (e : InvalidRideStateException){
            println("[x] ${e.message}")
        }
        catch (e : DriverNotFoundException){
            println("[x] ${e.message}")
        }
    }

    // ==================== PARCELS ====================

    private fun bookParcel(customer: User) {

        if (RiderController.hasActiveParcel(customer.userId)) {
            println("\nYou already have an active parcel. Finish or cancel it before booking another.")
            return
        }

        ConsoleFormat.header("SEND / RECEIVE A PARCEL")

        val parcelMode = InputReader.chooseParcelMode()

        val pickupLabel = if (parcelMode == ParcelMode.SEND) "Pickup Location (where you are)" else "Pickup Location (where the parcel currently is)"
        val dropLabel = if (parcelMode == ParcelMode.SEND) "Drop Location (where it's going)" else "Drop Location (where you are)"

        val pickup = InputReader.chooseLocation(pickupLabel)

        var drop: Location
        while (true) {
            drop = InputReader.chooseLocation(dropLabel)
            if (pickup == drop) {
                println("Pickup and Drop locations cannot be the same.")
                continue
            }
            break
        }

        val contactLabel = if (parcelMode == ParcelMode.SEND) "recipient" else "pickup contact"
        val contactName = InputReader.promptName(
            prompt = "Name of the $contactLabel: ",
            errorMessage = "Name must contain minimum 3 characters. Please try again"
        )
        val contactPhone = InputReader.promptPhone(
            prompt = "Phone of the $contactLabel: "
        )

        val category = InputReader.chooseParcelCategory()
        val weight = InputReader.promptWeight()

        val fareEstimates = try {
            RiderController.estimateParcelFares(pickup, drop, category)
        }
        catch (e: DistanceNotFoundException) {
            println("[x] ${e.message}")
            return
        }

        while (true) {
            val vehicleCategory = InputReader.chooseVehicleCategoryByFare(fareEstimates)

            println("\nFinding a nearby $vehicleCategory for you...")
            try {
                val parcel = RiderController.bookParcel(
                    customer, pickup, drop, vehicleCategory, parcelMode, contactName, contactPhone, weight, category
                )

                val driver = RiderController.getDriverForParcel(parcel)

                ConsoleFormat.subHeader("PARCEL BOOKED")
                println(parcel)
                println()
                println("Driver  : ${driver.name}")
                println("Phone   : ${driver.phone}")
                return
            }
            catch (e: AvailableDriversNotFoundException) {
                println("[x] ${e.message}")
                if (InputReader.promptConfirmation("Try another vehicle type?")) continue
                return
            }
            catch (e: DistanceNotFoundException) {
                println("[x] ${e.message}")
                return
            }
            catch (e: DriverNotFoundException) {
                println("[x] ${e.message}")
                return
            }
            catch (e: VehicleNotFoundException) {
                println("[x] ${e.message}")
                return
            }
            catch (e: IllegalArgumentException) {
                println("[x] ${e.message}")
                return
            }
        }
    }

    private fun viewCurrentParcel(customer: User) {
        try {
            val parcel = RiderController.getCurrentParcel(customer.userId)
            val driver = RiderController.getDriverForParcel(parcel)

            ConsoleFormat.header("CURRENT PARCEL")
            println(parcel)
            println()
            println("Driver  : ${driver.name}")
            println("Phone   : ${driver.phone}")

            currentParcelMenu(parcel, customer)
        }
        catch (e: ActiveParcelNotFoundException) {
            println("[x] ${e.message}")
        }
    }

    private fun currentParcelMenu(parcel: Parcel, customer: User) {
        while (true) {
            println("\n  1. Cancel Parcel")
            println("  0. Back")
            print("Choose: ")

            when (readln().trim()) {
                "1" -> {
                    cancelParcel(parcel, customer)
                    return
                }
                "0" -> return
                else -> println("Invalid choice.")
            }
        }
    }

    private fun cancelParcel(parcel: Parcel, customer: User) {
        try {
            RiderController.cancelParcel(parcel, customer)
            println("\nParcel cancelled successfully.")
        }
        catch (e: UnauthorizedParcelActionException) {
            println("[x] ${e.message}")
        }
        catch (e: InvalidParcelStateException) {
            println("[x] ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("[x] ${e.message}")
        }
    }

    private fun updateProfile(rider: User) {

        println("\n========== UPDATE PROFILE ==========")
        println("(Press Enter to keep the current value)\n")

        val name = InputReader.promptOptionalName(rider.name)

        val phone = InputReader.promptOptionalPhone(rider.phone)

        if (name == rider.name && phone == rider.phone) {
            println("\nNo changes made.")
            return
        }

        try {
            RiderController.updateProfile(
                rider,
                name,
                phone
            )

            println(
                """
    
                Profile Updated Successfully
    
                Name  : ${rider.name}
                Phone : ${rider.phone}
                Email : ${rider.email}
                """.trimIndent()
            )

        }
        catch (e: IllegalArgumentException) {
            println("[x] Invalid Input, ${e.message}")
        }
    }

    private fun accountMenu(customer: User){
        while (true) {

            ConsoleFormat.header("ACCOUNT MENU")
            println(
                """
                    1. Update Profile
                    2. Change Password
                    0. Back
                """.trimIndent()
            )

            when (readln().trim()) {
                "1" -> updateProfile(customer)
                "2" -> AuthUI.changePassword(customer)
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }

}