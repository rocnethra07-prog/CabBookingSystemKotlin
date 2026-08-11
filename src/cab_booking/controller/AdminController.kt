package cab_booking.controller

import cab_booking.model.Ride
import cab_booking.service.AdminService
import cab_booking.console.ConsoleInput
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.CabNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.Cab
import cab_booking.model.Driver

object AdminController{

    fun adminDashboard() {

        while (true) {

            println(
                """
    
                ========== ADMIN MENU ==========
                1. Driver Management
                2. Rider Management
                3. Ride Management
                4. Cab Management
                5. User Account Management
                0. Logout
                """.trimIndent()
            )
            when (readln().trim()) {

                "1" -> driverManagementMenu()

                "2" -> riderManagementMenu()

                "3" -> rideManagementMenu()

                "4" -> cabManagementMenu()

                "5" -> userAccountManagement()

                "0" -> {
                    println("\nLogged out successfully.")
                    return
                }

                else -> println("Invalid choice.")
            }
        }
    }

    // DRIVER MANAGEMENT

    private fun driverManagementMenu() {

        while (true) {

            println(
                """
    
                ========== DRIVER MANAGEMENT ==========
                1. Add Driver
                2. Delete Driver
                3. View All Drivers
                4. View Available Drivers
                5. View Unavailable Drivers
                6. Search Driver
                7. Driver Ride History
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> addDriver()

                "2" -> deleteDriver()

                "3" -> viewAllDrivers()

                "4" -> viewAvailableDrivers()

                "5" -> viewUnavailableDrivers()

                "6" -> searchDriver()

                "7" -> driverRideHistory()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun addDriver() {

        println("\n========== ADD DRIVER ==========")

        // ask for driver details
        val name = ConsoleInput.promptName()

        val phone = ConsoleInput.promptPhone()

        var email: String

        while (true) {

            email = ConsoleInput.promptEmail()

            if (!AdminService.isEmailRegistered(email))
                break

            println("Email already registered.")
        }

        val password = ConsoleInput.promptPassword()

        val currentLocation = ConsoleInput.chooseLocation()

        var licenseNumber: String

        while (true) {

            licenseNumber = ConsoleInput.promptLicenseNumber()

            if (!AdminService.isLicenseNumberExists(licenseNumber))
                break

            println("License number already exists.")
        }

        // create a cab for the driver
        val cab = collectCabDetails()

        try {
            val driver = AdminService.addDriver(name, phone, email, password,currentLocation, licenseNumber, cab)
            println("\nDriver Registered Successfully.\n")
            println(driver)
        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, ${e.message}")
        }
    }

    private fun collectCabDetails() : Cab {
        val model = ConsoleInput.promptNonEmptyInput(
            "Car Model : ",
            "Model cannot be empty."
        )

        var registrationNumber: String

        while (true) {

            registrationNumber = ConsoleInput.promptRegistrationNumber()

            if (!AdminService.isRegistrationNumberExists(registrationNumber))
                break

            println("Registration number already exists.")
        }

        val cabType = ConsoleInput.chooseCabType()
        val newCab = AdminService.createCab(model, registrationNumber, cabType)
        return newCab
    }

    private fun deleteDriver() {

        println("\n========== DELETE DRIVER ==========")

        val driverId = ConsoleInput.promptNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {
            val driver = AdminService.findDriverById(driverId)
            println("\nDriver Details")
            println(driver)

            if (!ConsoleInput.promptConfirmation("Delete this driver?")) {
                println("Deletion cancelled.")
                return
            }

            if (AdminService.deleteDriver(driver)) {
                println("Driver deleted successfully.")
            } else {
                println("Driver has an active ride and cannot be deleted.")
            }

        }
        catch (e : DriverNotFoundException) {
            println("[!] ${e.message}")
        }
        catch (e : IllegalArgumentException){
            println("[!] Invalid Input, ${e.message}")
        }
    }

    private fun viewAllDrivers() {

        val drivers = AdminService.getAllDrivers()

        if (drivers.isEmpty()) {
            println("\nNo drivers found.")
            return
        }

        println("\n========== ALL DRIVERS ==========")

        drivers.forEach {
            displayDriverWithCab(it)
        }
    }

    private fun viewAvailableDrivers() {

        val drivers = AdminService.getAvailableDrivers()

        if (drivers.isEmpty()) {
            println("\nNo available drivers.")
            return
        }

        println("\n========== AVAILABLE DRIVERS ==========")

        drivers.forEach {
            displayDriverWithCab(it)
        }
    }

    private fun viewUnavailableDrivers() {

        val drivers = AdminService.getUnavailableDrivers()

        if (drivers.isEmpty()) {
            println("\nNo unavailable drivers.")
            return
        }

        println("\n========== UNAVAILABLE DRIVERS ==========")

        drivers.forEach {
            displayDriverWithCab(it)
        }
    }

    private fun searchDriver() {

        val driverId = ConsoleInput.promptNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {
            val driver = AdminService.findDriverById(driverId)

            println("\nDriver Details")
            println(driver)

            println("\nAssigned Cab")
            println(AdminService.getCabForDriver(driver))

        }
        catch (e: DriverNotFoundException) {
            println("[!] ${e.message}")
        }
        catch (e: CabNotFoundException){
            println("[!] ${e.message}")
        }
    }

    private fun displayDriverWithCab(driver: Driver) {
        println("\nDriver Details")
        println(driver)

        try {
            println("\nAssigned Cab")
            println(AdminService.getCabForDriver(driver))
        }
        catch (e: CabNotFoundException) {
            println("[!] ${e.message}")
        }

        println("-".repeat(50))
    }


    private fun driverRideHistory() {

        val driverId = ConsoleInput.promptNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        val rides = AdminService.getDriverRideHistory(driverId)
        if (rides.isEmpty()) {
            println("\nNo rides found.")
            return
        }
        println("\n========== DRIVER RIDE HISTORY ==========")

        rides.forEach {
            println(it)
            println("-".repeat(50))
        }

    }

    // RIDER MANAGEMENT
    private fun riderManagementMenu() {

        while (true) {

            println(
                """
    
                ========== RIDER MANAGEMENT ==========
                1. View All Riders
                2. Rider Ride History
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> viewAllRiders()

                "2" -> viewRiderRideHistory()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun viewAllRiders() {

        val riders = AdminService.getAllRiders()

        if (riders.isEmpty()) {
            println("\nNo riders found.")
            return
        }

        println("\n========== ALL RIDERS ==========")

        riders.forEach {
            println(it)
            println("-".repeat(50))
        }
    }

    private fun viewRiderRideHistory() {

        val riderId = ConsoleInput.promptNonEmptyInput(
            "Enter Rider ID : ",
            "Rider ID cannot be empty."
        )
        val rides = AdminService.getRiderRideHistory(riderId)

        displayRides(rides, "RIDER RIDE HISTORY")

    }

    // RIDE MANAGEMENT

    private fun rideManagementMenu() {

        while (true) {

            println(
                """
    
                ========== RIDE MANAGEMENT ==========
                1. View All Rides
                2. Active Rides
                3. Completed Rides
                4. Cancelled Rides
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> viewAllRides()

                "2" -> viewActiveRides()

                "3" -> viewCompletedRides()

                "4" -> viewCancelledRides()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun viewAllRides() = displayRides(
        AdminService.getAllRides(),
        "ALL RIDES"
    )

    private fun viewActiveRides() = displayRides(
        AdminService.getActiveRides(),
        "ACTIVE RIDES"
    )

    private fun viewCompletedRides() = displayRides(
        AdminService.getCompletedRides(),
        "COMPLETED RIDES"
    )

    private fun viewCancelledRides() = displayRides(
        AdminService.getCancelledRides(),
        "CANCELLED RIDES"
    )

    private fun displayRides(
        rides: List<Ride>,
        title: String
    ) {

        if (rides.isEmpty()) {
            println("\nNo rides found.")
            return
        }

        println("\n========== $title ==========")

        rides.forEach {
            println(it)
            println("-".repeat(50))
        }
    }



    // CAB MANAGEMENT

    private fun cabManagementMenu() {

        while (true) {

            println(
                """
    
                ========== CAB MANAGEMENT ==========
                1. View All Cabs
                2. View Cabs By Type
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> viewAllCabs()

                "2" -> viewCabsByType()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun viewAllCabs() {

        val cabs = AdminService.getAllCabs()

        if (cabs.isEmpty()) {
            println("\nNo cabs available.")
            return
        }

        println("\n========== ALL CABS ==========")

        cabs.forEach {
            println(it)
            println("-".repeat(50))
        }
    }

    private fun viewCabsByType() {

        val cabType = ConsoleInput.chooseCabType()

        val cabs = AdminService.getCabsByType(cabType)

        if (cabs.isEmpty()) {
            println("\nNo $cabType cabs found.")
            return
        }

        println("\n========== $cabType CABS ==========")

        cabs.forEach {
            println(it)
            println("-".repeat(50))
        }
    }

    private fun userAccountManagement() {

        while (true) {

            println(
                """
    
                ========== USER ACCOUNT MANAGEMENT ==========
                1. Unlock User Account
                2. View Locked Accounts
                3. Search User
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {

                "1" -> unlockUserAccount()

                "2" -> viewLockedAccounts()

                "3" -> searchUser()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun unlockUserAccount() {

        val lockedAccounts = AdminService.getLockedAccounts()

        println("\n========== UNLOCK USER ACCOUNT ==========")

        if (lockedAccounts.isEmpty()) {
            println("No locked accounts found.")
            return
        }

        println("\nLocked Accounts:")
        lockedAccounts.forEachIndexed { index, account ->
            println("${index + 1}. ${account.userId} , Locked ~${account.remainingLockTime()} min remaining")
        }

        val userId = ConsoleInput.promptNonEmptyInput(
            "\nEnter User ID : ",
            "User ID cannot be empty."
        )

        if (lockedAccounts.none { it.userId == userId }) {
            println("[!] Please enter a valid locked User ID.")
            return
        }

        try {
            AdminService.unlockUserAccount(userId)
            println("User account unlocked successfully.")
        }
        catch (e: CredentialsNotFoundException) {
            println("[!] Unable to unlock account. ${e.message}")
        }
    }

    private fun viewLockedAccounts() {

        val lockedAccounts = AdminService.getLockedAccounts()

        if (lockedAccounts.isEmpty()) {
            println("\nNo locked user accounts found.")
            return
        }

        println("\n========== LOCKED USER ACCOUNTS ID ==========")

        lockedAccounts.forEach {
            println("User ID: $it.userId | Locked, ~${it.remainingLockTime()} min remaining")
            println("-".repeat(50))
        }
    }

    private fun searchUser() {

        println("\n========== SEARCH USER ==========")

        val userId = ConsoleInput.promptNonEmptyInput(
            "Enter User ID : ",
            "User ID cannot be empty."
        )

        try {

            val user = AdminService.findUserById(userId)

            println("\n========== USER DETAILS ==========")
            println(user)

        }
        catch (e: UserNotFoundException) {
            println("[!] ${e.message}")
        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, ${e.message}")
        }
    }
}