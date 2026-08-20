package cab_booking.console

import cab_booking.auth.UserCredential
import cab_booking.console.input.InputReader
import cab_booking.controller.AdminController
import cab_booking.controller.AuthController
import cab_booking.controller.DriverController
import cab_booking.controller.RiderController
import cab_booking.exception.CabNotFoundException
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.Cab
import cab_booking.model.Driver
import cab_booking.model.Ride

object AdminUI {
    fun adminDashboard(){
        while(true){
            println("""
                
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
    private fun driverManagementMenu(){
        while(true) {
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

        val name = InputReader.promptName()
        val phone = InputReader.promptPhone()

        var email: String
        while (true) {
            email = InputReader.promptEmail()
            if (AuthController.isEmailRegistered(email)) {
                println("Email already registered.")
                continue
            }
            break
        }

        val password = InputReader.promptPassword()
        val currentLocation = InputReader.chooseLocation()

        var licenseNumber: String
        while (true) {
            licenseNumber = InputReader.promptLicenseNumber()
            if (AdminController.isLicenseNumberTaken(licenseNumber)) {
                println("License number already exists.")
                continue
            }
            break
        }

        val cab = collectCabDetails()

        try {
            val driver = AdminController.addDriver(name, phone, email, password, currentLocation, licenseNumber, cab)
            println("\nDriver Registered Successfully.\n")
            println(driver)
        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, ${e.message}")
        }
    }

    private fun collectCabDetails(): Cab {
        val model = InputReader.promptNonEmptyInput(
            "Car Model : ",
            "Model cannot be empty."
        )

        val cabType = InputReader.chooseCabType()

        var registrationNumber: String
        while (true) {
            registrationNumber = InputReader.promptRegistrationNumber()
            if (AdminController.isRegistrationNumberTaken(registrationNumber)) {
                println("Registration number already exists.")
                continue
            }
            break
        }
        return AdminController.createCab(model, cabType, registrationNumber)
    }

    private fun deleteDriver() {
        println("\n========== DELETE DRIVER ==========")

        val driverId = InputReader.promptNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {
            val driver = DriverController.findDriverById(driverId)
            println("\nDriver Details")
            println(driver)

            if (!InputReader.promptConfirmation("Delete this driver?")) {
                println("Deletion cancelled.")
                return
            }

            if (AdminController.deleteDriver(driver)) {
                println("Driver deleted successfully.")
            }
            else {
                println("Driver has an active ride and cannot be deleted.")
            }
        }
        catch (e: DriverNotFoundException) {
            println("[!] ${e.message}")
        }
        catch (e: IllegalArgumentException) {
            println("[!] Invalid Input, ${e.message}")
        }
    }

    private fun viewAllDrivers() =
        displayDrivers(AdminController.getAllDrivers(), "ALL DRIVERS")

    private fun viewAvailableDrivers() =
        displayDrivers(AdminController.getAvailableDrivers(), "AVAILABLE DRIVERS")

    private fun viewUnavailableDrivers() =
        displayDrivers(AdminController.getUnavailableDrivers(), "UNAVAILABLE DRIVERS")

    private fun displayDrivers(drivers: List<Driver>, title: String) {
        if (drivers.isEmpty()) {
            println("\nNo drivers found.")
            return
        }

        println("\n========== $title ==========")

        drivers.forEach {
            displayDriverWithCab(it)
        }
    }

    private fun searchDriver() {
        val driverId = InputReader.promptNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {
            val driver = DriverController.findDriverById(driverId)
            displayDriverWithCab(driver)
        }
        catch (e: DriverNotFoundException) {
            println("[!] ${e.message}")
        }
    }

    private fun displayDriverWithCab(driver: Driver) {
        println("\nDriver Details")
        println(driver)

        try {
            println("\nAssigned Cab")
            println(AdminController.getCabForDriver(driver.cabId))
        }
        catch (e: CabNotFoundException) {
            println("[!] ${e.message}")
        }

        println("-".repeat(50))
    }

    private fun driverRideHistory() {
        val driverId = InputReader.promptNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        val rides = DriverController.getDriverRideHistory(driverId)
        displayRides(rides, "DRIVER RIDE HISTORY")
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
        val riders = AdminController.getAllRiders()

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
        val riderId = InputReader.promptNonEmptyInput(
            "Enter Rider ID : ",
            "Rider ID cannot be empty."
        )
        val rides = RiderController.getRiderRideHistory(riderId)
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
                "1" -> displayRides(AdminController.getAllRides(), "ALL RIDES")
                "2" -> displayRides(AdminController.getActiveRides(), "ACTIVE RIDES")
                "3" -> displayRides(AdminController.getCompletedRides(), "COMPLETED RIDES")
                "4" -> displayRides(AdminController.getCancelledRides(), "CANCELLED RIDES")
                "0" -> return
                else -> println("Invalid choice.")
            }
        }
    }

    private fun displayRides(rides: List<Ride>, title: String) {
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
        val cabs = AdminController.getAllCabs()

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
        val cabType = InputReader.chooseCabType()
        val cabs = AdminController.getCabsByType(cabType)

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

    // USER ACCOUNT MANAGEMENT

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
        val lockedAccounts = AdminController.getLockedAccounts()

        println("\n========== UNLOCK USER ACCOUNT ==========")

        if (lockedAccounts.isEmpty()) {
            println("No locked accounts found.")
            return
        }

        println("\nLocked Accounts:")
        printLockedAccounts(lockedAccounts)

        val userId = InputReader.promptNonEmptyInput(
            "\nEnter User ID : ",
            "User ID cannot be empty."
        )

        if (lockedAccounts.none { it.userId == userId }) {
            println("[!] Please enter a valid locked User ID.")
            return
        }

        try {
            AdminController.unlockUserAccount(userId)
            println("User account unlocked successfully.")
        }
        catch (e: CredentialsNotFoundException) {
            println("[!] Unable to unlock account. ${e.message}")
        }
    }

    private fun printLockedAccounts(accounts: List<UserCredential>) {
        accounts.forEachIndexed { index, account ->
            println("${index + 1}. ${account.userId} , Locked ~${account.remainingLockTime().toMinutes().plus(1)} min remaining")
        }
    }

    private fun viewLockedAccounts() {
        val lockedAccounts = AdminController.getLockedAccounts()

        if (lockedAccounts.isEmpty()) {
            println("\nNo locked user accounts found.")
            return
        }

        println("\n========== LOCKED USER ACCOUNTS ID ==========")

        lockedAccounts.forEach {
            println("User ID: ${it.userId} | Locked, ~${it.remainingLockTime().toMinutes().plus(1)} min remaining")
            println("-".repeat(50))
        }
    }

    private fun searchUser() {
        println("\n========== SEARCH USER ==========")

        val userId = InputReader.promptNonEmptyInput(
            "Enter User ID : ",
            "User ID cannot be empty."
        )

        try {
            val user = AdminController.findUserById(userId)

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