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
import cab_booking.exception.OperationCancelledException

object CustomerUI {
    fun customerDashboard(customer: User){

        promptPendingRating(customer)
        autoShowActiveRide(customer)
        autoShowActiveParcel(customer)

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

    //PENDING RATING (shown once, on login)
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
        catch (_: OperationCancelledException) {}
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

        try {

            val rating = InputReader.promptRating()
            RiderController.rateDriver(
                ride,
                rider,
                rating
            )
            println("\nThank you for rating ${driver.name}")
            println("★".repeat(rating) + "☆".repeat(5 - rating))

        }
        catch (e: UnauthorizedRideActionException) {
            println("[x] We couldn't submit your rating.  ${e.message}")
        }
        catch (e : InvalidRideStateException){
            println("[x] We couldn't submit your rating.  ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("[x] We couldn't submit your rating.  ${e.message}")
        }
        catch (e : IllegalArgumentException){
            println("[x] Invalid Input, Unable to submit rating at the moment${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nSkipped rating.")
        }
    }

    private fun bookRide(rider: User) {

        if (RiderController.hasActiveRide(rider.userId)) {
            println("\nYou already have an active ride.")
            return
        }

        try {
            println("\n========== BOOK RIDE ==========")

            val pickup = InputReader.chooseLocation("Pickup Location")

            var drop: Location

            while (true) {

                drop = InputReader.chooseLocation("Drop Location")

                if (pickup == drop) {
                    println("\n[x] Pickup and Drop locations cannot be the same.\n")
                    continue
                }
                break
            }

            val fareEstimates = try {
                RiderController.estimateRideFares(pickup, drop)
            } catch (e: DistanceNotFoundException) {
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

                } catch (e: AvailableDriversNotFoundException) {
                    println("[x] ${e.message}")
                    if (InputReader.promptConfirmation("Try another vehicle type? (Y/N): ")) {
                        continue
                    }
                    return
                } catch (e: DistanceNotFoundException) {
                    println("[x] ${e.message}")
                    return
                } catch (e: DriverNotFoundException) {
                    println("[x] ${e.message}")
                    return
                } catch (e: VehicleNotFoundException) {
                    println("[x] ${e.message}")
                    return
                } catch (e: IllegalArgumentException) {
                    println("[x] ${e.message}")
                    return
                }
            }
        }
        catch (_: OperationCancelledException) {
            println("\nBooking cancelled.")
        }
    }

    // Called both from the "My Ride" menu item and automatically on login -
    // either way it first shows just a heads-up, then details, then actions.
    fun viewCurrentRide(rider: User){
        try {
            val ride = RiderController.getCurrentBookedRide(rider.userId)
            val driver = RiderController.getDriverForRide(ride)
            ConsoleFormat.header("YOUR RIDE")
            println(ride)
            println()
            println("Driver : ${driver.name}")
            println("Phone  : ${driver.phone}")
            rideDetailsFlow(ride, rider, driver)
        }
        catch (e: ActiveRideNotFoundException) {
            println("\n${e.message}")
        }
    }

    private fun autoShowActiveRide(rider: User) {
        if(RiderController.hasActiveRide(rider.userId)) {
            viewCurrentRide(rider)
        }
    }

    private fun rideDetailsFlow(ride: Ride, rider: User, driver: Driver) {
        while (true) {
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> {
                    ConsoleFormat.header("YOUR RIDE")
                    println(ride)
                    println()
                    println("Driver : ${driver.name}")
                    println("Phone  : ${driver.phone}")

                    currentRideMenu(ride, rider)
                    return
                }
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }
    private fun currentRideMenu(ride: Ride, rider: User) {

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

        try {
            ConsoleFormat.header("SEND / RECEIVE A PARCEL")

            val parcelMode = InputReader.chooseParcelMode()

            val pickupLabel =
                if (parcelMode == ParcelMode.SEND) "Pickup Location (where you are)" else "Pickup Location (where the parcel currently is)"
            val dropLabel =
                if (parcelMode == ParcelMode.SEND) "Drop Location (where it's going)" else "Drop Location (where you are)"

            val pickup = InputReader.chooseLocation(pickupLabel)

            var drop: Location
            while (true) {
                drop = InputReader.chooseLocation(dropLabel)
                if (pickup == drop) {
                    println("[x] Pickup and Drop locations cannot be the same.")
                    continue
                }
                break
            }

            val contactLabel = if (parcelMode == ParcelMode.SEND) "recipient" else "pickup contact"
            val contactName = InputReader.promptName(
                prompt = "Name of the $contactLabel: "
            )
            val contactPhone = InputReader.promptPhone(
                prompt = "Phone of the $contactLabel: "
            )

            val category = InputReader.chooseParcelCategory()
            val weight = InputReader.promptWeight()

            val fareEstimates = try {
                RiderController.estimateParcelFares(pickup, drop, category)
            } catch (e: DistanceNotFoundException) {
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
                } catch (e: AvailableDriversNotFoundException) {
                    println("[x] ${e.message}")
                    if (InputReader.promptConfirmation("Try another vehicle type?")) continue
                    return
                } catch (e: DistanceNotFoundException) {
                    println("[x] ${e.message}")
                    return
                } catch (e: DriverNotFoundException) {
                    println("[x] ${e.message}")
                    return
                } catch (e: VehicleNotFoundException) {
                    println("[x] ${e.message}")
                    return
                } catch (e: IllegalArgumentException) {
                    println("[x] ${e.message}")
                    return
                }
            }
        }
        catch(_: OperationCancelledException) {
            println("\nBooking cancelled.")
        }
    }

    private fun viewCurrentParcel(customer: User) {
        try {
            val parcel = RiderController.getCurrentParcel(customer.userId)
            val driver = RiderController.getDriverForParcel(parcel)

            ConsoleFormat.header("YOUR PARCEL")
            println(parcel)
            println()
            println("Driver  : ${driver.name}")
            println("Phone   : ${driver.phone}")
            parcelDetailsFlow(parcel, customer)
        }
        catch (e: ActiveParcelNotFoundException) {
            println("[x] ${e.message}")
        }
    }

    private fun autoShowActiveParcel(customer: User) {
        if(RiderController.hasActiveParcel(customer.userId)){
            viewCurrentParcel(customer)
        }
    }

    private fun parcelDetailsFlow(parcel: Parcel, customer: User) {
        while (true) {
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> {
                    currentParcelMenu(parcel, customer)
                    return
                }
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
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

        try {
            println("\n========== UPDATE PROFILE ==========")
            println("(Press Enter to keep the current value)\n")

            val name = InputReader.promptOptionalName(rider.name)

            val phone = InputReader.promptOptionalPhone(rider.phone)

            if (name == rider.name && phone == rider.phone) {
                println("\nNo changes made.")
                return
            }
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
        catch (_: OperationCancelledException) {
            println("\nCancelled. No changes made.")
        }
    }

    private fun accountMenu(customer: User){
        while (true) {

            ConsoleFormat.header("MY PROFILE")
            println(customer)
            println()
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> profileOptionsMenu(customer)
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }

    private fun profileOptionsMenu(customer: User) {
        while (true) {
            println("\n1. Update Profile")
            println("2. Change Password")
            println("0. Back")
            println()

            when (readln().trim()) {
                "1" -> {
                    updateProfile(customer)
                    return
                }
                "2" -> {
                    AuthUI.changePassword(customer)
                    return
                }
                "0" -> return
                else -> println("[x] Invalid choice.")
            }
        }
    }
}