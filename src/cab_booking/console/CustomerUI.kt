package cab_booking.console

import cab_booking.console.input.InputReader
import cab_booking.controller.CustomerController
import cab_booking.exception.ActiveRideNotFoundException
import cab_booking.exception.AvailableDriversNotFoundException
import cab_booking.exception.CompletedRideNotFoundException
import cab_booking.exception.DistanceNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.VehicleNotFoundException
import cab_booking.exception.ActiveParcelNotFoundException
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.model.Customer
import cab_booking.model.types.Location
import cab_booking.console.input.ConsoleFormatter
import cab_booking.exception.InvalidBookingStateException
import cab_booking.exception.OperationCancelledException
import cab_booking.model.ParcelDelivery

object CustomerUI {
    fun customerDashboard(customer: Customer){

        promptPendingRating(customer)
        autoShowActiveRide(customer)
        autoShowActiveParcelDelivery(customer)

        while (true) {
            ConsoleFormatter.header("CUSTOMER MENU")
            println(
                """
                1. Book a Ride
                2. Send a Parcel
                3. My Ride
                4. My Parcel 
                5. Account
                0. Logout
                """.trimIndent()
            )
            when (readln().trim()) {

                "1" -> bookRide(customer)
                "2" -> sendParcel(customer)
                "3" -> viewCurrentRide(customer)
                "4" -> viewCurrentParcelDelivery(customer)
                "5" -> accountMenu(customer)
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    //PENDING RATING (shown once, on login)
    private fun promptPendingRating(rider: Customer) {

        try {
            val ride = CustomerController.getLastCompletedRideOfRider(rider.userId)

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
        ride: Ride, rider: Customer
    ) {
        val driver = CustomerController.getDriverForRide(ride)

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
        rider: Customer,
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
            CustomerController.rateDriver(
                ride,
                rider,
                rating
            )
            println("\nThank you for rating ${driver.name}")
            println("★".repeat(rating) + "☆".repeat(5 - rating))

        }
        catch (e: UnauthorizedRideActionException) {
            println("\n[x] We couldn't submit your rating.  ${e.message}")
        }
        catch (e : InvalidBookingStateException){
            println("\n[x] We couldn't submit your rating.  ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("\n[x] We couldn't submit your rating.  ${e.message}")
        }
        catch (e : IllegalArgumentException){
            println("\n[x] Invalid Input, We couldn't submit your rating${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nSkipped rating.")
        }
    }

    private fun bookRide(rider: Customer) {

        if (CustomerController.hasActiveRideOfRider(rider.userId)) {
            println("\nYou already have an active ride. Finish or cancel it before booking another.")
            return
        }

        if (CustomerController.hasActiveParcelDeliveryOfCustomer(rider.userId)) {
            println("\nYou already have an active parcel. Finish or cancel it before booking another.\n")
            return
        }
        try {
            println("\n========== BOOK RIDE ==========")

            val pickup = InputReader.chooseLocation("\nPickup Location: ")

            var drop: Location

            while (true) {

                drop = InputReader.chooseLocation("\nDrop Location: ")

                if (pickup == drop) {
                    println("\n[x] Pickup and Drop locations cannot be the same.\n")
                    continue
                }
                break
            }

            val fareEstimates = try {
                CustomerController.estimateRideFares(pickup, drop)
            } catch (e: DistanceNotFoundException) {
                println("\n[x] ${e.message}")
                return
            }

            while (true) {

                val vehicleCategory = InputReader.chooseVehicleCategoryByFare(fareEstimates)

                println("\nFinding a nearby $vehicleCategory for you...\n")

                try {

                    val ride = CustomerController.bookRide(
                        rider,
                        pickup,
                        drop,
                        vehicleCategory
                    )

                    val driver = CustomerController.getDriverForRide(ride)

                    ConsoleFormatter.subHeader("RIDE CONFIRMED")
                    println(ride)
                    println(
                        """
                        
                    Driver Details :
                    Driver : ${driver.name}
                    Phone  : ${driver.phoneNumber}
                    """.trimIndent()
                    )

                    return

                } catch (e: AvailableDriversNotFoundException) {
                    println("\n[x] ${e.message}")
                    if (InputReader.promptConfirmation("\nOr Try another vehicle type? (Y/N): ")) {
                        continue
                    }
                    return
                } catch (e: DistanceNotFoundException) {
                    println("\n[x] ${e.message}")
                    return
                } catch (e: DriverNotFoundException) {
                    println("\n[x] ${e.message}")
                    return
                } catch (e: VehicleNotFoundException) {
                    println("\n[x] ${e.message}")
                    return
                } catch (e: IllegalArgumentException) {
                    println("\n[x] ${e.message}")
                    return
                }
            }
        }
        catch (_: OperationCancelledException) {
            println("\nBooking cancelled.")
        }
    }

    // Called both from the "My Ride" menu item and automatically on login
    fun viewCurrentRide(rider: Customer){
        try {
            val ride = CustomerController.getCurrentBookedRide(rider.userId)
            val driver = CustomerController.getDriverForRide(ride)
            ConsoleFormatter.header("YOUR RIDE")
            println(ride)
            println()
            println("Driver : ${driver.name}")
            println("Phone  : ${driver.phoneNumber}")
            rideMenu(ride, rider, driver)
        }
        catch (e: ActiveRideNotFoundException) {
            println("\n${e.message}")
        }
    }

    private fun autoShowActiveRide(rider: Customer) {
        if(CustomerController.hasActiveRideOfRider(rider.userId)) {
            viewCurrentRide(rider)
        }
    }

    private fun rideMenu(ride: Ride, rider: Customer, driver: Driver) {
        while (true) {
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> {
                    ConsoleFormatter.header("YOUR RIDE")
                    println(ride)
                    println()
                    println("Driver : ${driver.name}")
                    println("Phone  : ${driver.phoneNumber}")

                    currentRideOptions(ride, rider)
                    return
                }
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }
    private fun currentRideOptions(ride: Ride, rider: Customer) {

        while (true) {

            println("\n1. Cancel Ride")
            println("0. Back")

            when (readln().trim()) {

                "1" -> {
                    cancelRide(ride, rider)
                    return
                }

                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    private fun cancelRide(
        ride: Ride, rider: Customer
    ) {
        try {
            CustomerController.cancelRide(ride, rider)
            println("\nRide cancelled successfully.")
        }
        catch (e: UnauthorizedRideActionException) {
            println("\n[x] ${e.message}")
        }
        catch (e : InvalidBookingStateException){
            println("\n[x] ${e.message}")
        }
        catch (e : DriverNotFoundException){
            println("\n[x] ${e.message}")
        }
    }

    // ==================== PARCELS ====================

    private fun sendParcel(customer: Customer) {

        if (CustomerController.hasActiveParcelDeliveryOfCustomer(customer.userId)) {
            println("\nYou already have an active parcel. Finish or cancel it before booking another.\n")
            return
        }

        if (CustomerController.hasActiveRideOfRider(customer.userId)) {
            println("\nYou already have an active ride. Finish or cancel it before booking another.")
            return
        }

        try {
            ConsoleFormatter.header("SEND A PARCEL")

            val pickup = InputReader.chooseLocation("\nPickup Location (where you are): ")

            var drop: Location
            while (true) {
                drop = InputReader.chooseLocation("\nDrop Location (where it's going): ")
                if (pickup == drop) {
                    println("\n[x] Pickup and Drop locations cannot be the same.")
                    continue
                }
                break
            }

            val contactPersonName = InputReader.promptName(
                prompt = "Name of the receiver: "
            )
            val contactPersonPhoneNumber = InputReader.promptPhoneNumber(
                prompt = "Phone of the receiver: "
            )

            val fareEstimates = try {
                CustomerController.estimateParcelDeliveryFares(pickup, drop)
            }
            catch (e: DistanceNotFoundException) {
                println("\n[x] ${e.message}")
                return
            }

            while (true) {
                val vehicleCategory = InputReader.chooseVehicleCategoryByFare(fareEstimates,
                    prompt = "Choose a vehicle for your parcel",
                    showWeightLimit = true
                )

                println("\nFinding a nearby $vehicleCategory for you...\n")
                try {
                    val parcelDelivery = CustomerController.bookParcelDelivery(
                        customer, pickup, drop, vehicleCategory,contactPersonName, contactPersonPhoneNumber
                    )

                    val driver = CustomerController.getDriverForParcelDelivery(parcelDelivery)

                    ConsoleFormatter.subHeader("PARCEL DELIVERY BOOKED")
                    println(parcelDelivery)
                    println()
                    println("Driver  : ${driver.name}")
                    println("Phone   : ${driver.phoneNumber}")
                    return
                } catch (e: AvailableDriversNotFoundException) {
                    println("\n[x] ${e.message}")
                    if (InputReader.promptConfirmation("\nTry another vehicle type?: ")) continue
                    return
                } catch (e: DistanceNotFoundException) {
                    println("\n[x] ${e.message}")
                    return
                } catch (e: DriverNotFoundException) {
                    println("\n[x] ${e.message}")
                    return
                } catch (e: VehicleNotFoundException) {
                    println("\n[x] ${e.message}")
                    return
                } catch (e: IllegalArgumentException) {
                    println("\n[x] ${e.message}")
                    return
                }
            }
        }
        catch(_: OperationCancelledException) {
            println("\nBooking cancelled.")
        }
    }

    private fun viewCurrentParcelDelivery(customer: Customer) {
        try {
            val parcelDelivery = CustomerController.getCurrentParcelDeliveryForCustomer(customer.userId)
            val driver = CustomerController.getDriverForParcelDelivery(parcelDelivery)

            ConsoleFormatter.header("YOUR PARCEL")
            println(parcelDelivery)
            println()
            println("Driver  : ${driver.name}")
            println("Phone   : ${driver.phoneNumber}")
            parcelDeliveryMenu(parcelDelivery, customer)
        }
        catch (e: ActiveParcelNotFoundException) {
            println("\n[x] ${e.message}")
        }
    }

    private fun autoShowActiveParcelDelivery(customer: Customer) {
        if(CustomerController.hasActiveParcelDeliveryOfCustomer(customer.userId)){
            viewCurrentParcelDelivery(customer)
        }
    }

    private fun parcelDeliveryMenu(parcelDelivery: ParcelDelivery, customer: Customer) {
        while (true) {
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> {
                    currentParcelDeliveryOptions(parcelDelivery, customer)
                    return
                }
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    private fun currentParcelDeliveryOptions(parcelDelivery: ParcelDelivery, customer: Customer) {
        while (true) {
            println("\n  1. Cancel Parcel")
            println("  0. Back")
            print("Choose: ")

            when (readln().trim()) {
                "1" -> {
                    cancelParcelDelivery(parcelDelivery, customer)
                    return
                }
                "0" -> return
                else -> println("Invalid choice.")
            }
        }
    }

    private fun cancelParcelDelivery(parcelDelivery: ParcelDelivery, customer: Customer) {
        try {
            CustomerController.cancelParcelDelivery(parcelDelivery, customer)
            println("\nParcel delivery cancelled successfully.")
        }
        catch (e: UnauthorizedParcelActionException) {
            println("\n[x] ${e.message}")
        }
        catch (e: InvalidBookingStateException) {
            println("\n[x] ${e.message}")
        }
        catch (e: DriverNotFoundException) {
            println("\n[x] ${e.message}")
        }
    }

    private fun updateProfile(rider: Customer) {

        try {
            println("\n========== UPDATE PROFILE ==========")
            println("(Press Enter to keep the current value)\n")

            val name = InputReader.promptOptionalName(rider.name)

            val phoneNumber = InputReader.promptOptionalPhone(rider.phoneNumber)

            if (name == rider.name && phoneNumber == rider.phoneNumber) {
                println("\nNo changes made.")
                return
            }
            CustomerController.updateProfile(
                rider,
                name,
                phoneNumber
            )

            println(
                """
    
                Profile Updated Successfully
    
                Name  : ${rider.name}
                Phone : ${rider.phoneNumber}
                Email : ${rider.email}
                """.trimIndent()
            )

        }
        catch (e: IllegalArgumentException) {
            println("\n[x] Invalid Input, ${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled. No changes made.")
        }
    }

    private fun accountMenu(customer: Customer){
        while (true) {

            ConsoleFormatter.header("MY PROFILE")
            println(customer)
            println()
            println("1. Show Options")
            println("0. Close")
            println()

            when (readln().trim()) {
                "1" -> profileOptionsMenu(customer)
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    private fun profileOptionsMenu(customer: Customer) {
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
                else -> println("\n[x] Invalid choice.")
            }
        }
    }
}