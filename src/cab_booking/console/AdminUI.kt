package cab_booking.console

import cab_booking.auth.UserCredential
import cab_booking.console.input.InputReader
import cab_booking.controller.AdminController
import cab_booking.controller.AuthController
import cab_booking.controller.DriverController
import cab_booking.exception.CredentialsNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.Driver
import cab_booking.util.ConsoleFormatter
import cab_booking.exception.OperationCancelledException
import cab_booking.exception.VehicleNotFoundException
import cab_booking.model.Vehicle

object AdminUI {
    fun adminDashboard() {
        while (true) {
            ConsoleFormatter.header("ADMIN MENU")
            println(
                """
                1. Drivers Management
                2. Vehicles Management   
                3. Account Security Management
                0. Logout
            """.trimIndent()
            )

            when (readln().trim()) {
                "1" -> driversMenu()
                "2" -> vehiclesMenu()
                "3" -> accountSecurityMenu()
                "0" -> {
                    println("\nLogged out successfully.")
                    return
                }

                else -> println("\n[x] Invalid choice.")
            }
        }
    }


    // DRIVER MANAGEMENT
    private fun driversMenu() {
        while (true) {
            println(
                """
                
                ========== DRIVER MANAGEMENT ==========
                1. Add Driver
                2. Remove Driver
                3. All Drivers
                4. Available Drivers
                5. Unavailable Drivers
                0. Back
                """.trimIndent()
            )

            when (readln().trim()) {
                "1" -> addDriver()
                "2" -> deleteDriver()
                "3" -> displayDrivers(AdminController.getAllDrivers(), "ALL DRIVERS")
                "4" -> displayDrivers(AdminController.getAvailableDrivers(), "AVAILABLE DRIVERS")
                "5" -> displayDrivers(AdminController.getUnavailableDrivers(), "UNAVAILABLE DRIVERS")
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    private fun addDriver() {
        try {
            ConsoleFormatter.header("ADD DRIVER")

            println("Driver Details")
            ConsoleFormatter.divider()

            val name = InputReader.promptName("Enter the Driver's Name")
            val phone = InputReader.promptPhoneNumber("Enter the Driver's Phone Number")

            var email: String
            while (true) {
                email = InputReader.promptEmail("Enter the Driver's Email")
                if (AuthController.isEmailRegistered(email)) {
                    println("\n[x] This email is already registered. Please use a different email.\n")
                    continue
                }
                break
            }

            val password = InputReader.promptPassword("Enter the Driver's Password")
            val currentLocation = InputReader.chooseLocation("Choose the Driver's Current Location")

            var licenseNumber: String
            while (true) {
                licenseNumber = InputReader.promptLicenseNumber("Enter the Driver's License Number")
                if (AdminController.isLicenseNumberTaken(licenseNumber)) {
                    println("\n[x] This license number is already registered. Please enter a different one.\n")
                    continue
                }
                break
            }
            val vehicle = collectVehicleDetails()
            val driver =
                AdminController.addDriver(name, phone, email, password, currentLocation, licenseNumber, vehicle)

            ConsoleFormatter.subHeader("DRIVER ADDED")
            println(driver)
            println()
            println(vehicle)
        }
        catch (e: IllegalArgumentException) {
            println("\n[x] Could not add driver: ${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled. No driver was added.")
        }
    }

    private fun collectVehicleDetails(): Vehicle {
        println("\nVehicle Details")
        ConsoleFormatter.divider()

        val vehicleModel =
            InputReader.promptNonEmptyInput("Enter Vehicle Model (e.g. Honda City)", "Vehicle model cannot be blank.")

        var registrationNumber: String
        while (true) {
            registrationNumber = InputReader.promptRegistrationNumber("Enter Vehicle Registration Number")
            if (AdminController.isRegistrationNumberTaken(registrationNumber)) {
                println("\n[x] This registration number is already registered. Please enter a different one. ")
                continue
            }
            break
        }

        val vehicleCategory = InputReader.chooseVehicleCategory()
        val vehicle = AdminController.createVehicle(vehicleModel, registrationNumber, vehicleCategory)

        return vehicle
    }

    private fun deleteDriver() {

        try {
            ConsoleFormatter.header("REMOVE DRIVER")

            val userId = InputReader.promptNonEmptyInput(
                "Enter Driver's User ID",
                "User ID cannot be blank."
            )

            val driver = DriverController.findDriverById(userId)
            println("\nDriver Details")
            println(driver)

            if (!InputReader.promptConfirmation("Remove this driver?")) {
                println("\nCancelled.")
                return
            }

            if (AdminController.deleteDriver(driver)) {
                println("\nDriver removed successfully.")
            } else {
                println("\n[x] This driver has an ongoing ride or parcel and cannot be removed right now.")
            }
        } catch (e: DriverNotFoundException) {
            println("\n[x] ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("\n[x] Invalid Input, ${e.message}")
        } catch (_: OperationCancelledException) {
            println("\nCancelled. No driver was removed.")
        }
    }

    private fun displayDrivers(drivers: List<Driver>, title: String) {
        if (drivers.isEmpty()) {
            println("\nNo drivers found.")
            return
        }

        ConsoleFormatter.header(title)
        drivers.forEach {
            displayDriverWithVehicle(it)
        }
    }

    private fun displayDriverWithVehicle(driver: Driver) {
        ConsoleFormatter.header("Driver Details")
        println(driver)
        ConsoleFormatter.divider()

        try {
            ConsoleFormatter.header("Assigned Vehicle")
            println(DriverController.getVehicleById(driver.assignedVehicleId))
        } catch (e: VehicleNotFoundException) {
            println("\n[x] ${e.message}")
        }
        ConsoleFormatter.divider()
    }

    private fun vehiclesMenu() {
        while (true) {
            ConsoleFormatter.header("VEHICLES")
            println("""
               1. All Vehicles
               2. By Category
               0. Back
            """.trimIndent()
            )

            when (readln().trim()) {
                "1" -> listVehicles(AdminController.getAllVehicles(), "ALL VEHICLES")
                "2" -> {
                    try {
                        val category = InputReader.chooseVehicleCategory()
                        listVehicles(AdminController.getVehiclesByCategory(category), "$category VEHICLES")
                    }
                    catch (_: OperationCancelledException) {}
                }
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    private fun listVehicles(vehicles: List<Vehicle>, title: String) {
        if (vehicles.isEmpty()) {
            println("\nNo vehicles found.")
            return
        }

        ConsoleFormatter.header(title)
        vehicles.forEach {
            println(it)
            ConsoleFormatter.divider()
        }
    }

    // USER ACCOUNT MANAGEMENT

    private fun accountSecurityMenu() {
        while (true) {
            ConsoleFormatter.header("ACCOUNT SECURITY")
            println(
                """
                1. Locked Accounts
                2. Lock an Account
                3. Unlock an Account
                0. Back
                """.trimIndent()
            )
            when (readln().trim()) {
                "1" -> displayLockedAccounts()
                "2" -> lockAccount()
                "3" -> unlockAccount()
                "0" -> return
                else -> println("\n[x] Invalid choice.")
            }
        }
    }

    private fun lockAccount() {
        try {
            ConsoleFormatter.header("LOCK AN ACCOUNT")

            val userId = InputReader.promptNonEmptyInput("Enter User ID: ", "User ID cannot be blank.")

            val user = AdminController.findUserById(userId)

            if (AdminController.isAccountLocked(userId)) {
                println("\n${user.name}'s account is already locked.")
                return
            }

            if (!InputReader.promptConfirmation("Lock ${user.name}'s account?")) {
                println("\nCancelled.")
                return
            }

            AdminController.lockUserAccount(userId)
            println("\n${user.name}'s account has been locked.")
        } catch (e: UserNotFoundException) {
            println("\n[x] ${e.message}")
        } catch (e: CredentialsNotFoundException) {
            println("\n[x] ${e.message}")
        } catch (_: OperationCancelledException) {
            println("\nCancelled.")
        }
    }

    private fun unlockAccount() {
        val lockedAccounts = AdminController.getLockedAccounts()

        ConsoleFormatter.header("UNLOCK AN ACCOUNT")

        if (lockedAccounts.isEmpty()) {
            println("No locked accounts found.")
            return
        }

        ConsoleFormatter.subHeader("\nLocked Accounts:")
        printLockedAccounts(lockedAccounts)

        try {
            val userId = InputReader.promptNonEmptyInput(
                "\nEnter User ID : ",
                "User ID cannot be empty."
            )

            if (lockedAccounts.none { it.userId == userId }) {
                println("\n[x] Please enter a valid locked User ID.")
                return
            }
            val user = AdminController.findUserById(userId)

            if (!AdminController.isAccountLocked(userId)) {
                println("\n${user.name}'s account is not locked.")
                return
            }

            AdminController.unlockUserAccount(userId)
            println("\n${user.name}'s account has been unlocked.")
        }
        catch (e: UserNotFoundException) {
            println("\n[x] ${e.message}")
        }
        catch (e: CredentialsNotFoundException) {
            println("\n[x] We couldn't unlock this account. ${e.message}")
        }
        catch (_: OperationCancelledException) {
            println("\nCancelled.")
        }
    }

    private fun printLockedAccounts(accounts: List<UserCredential>) {
        accounts.forEach {
            val minutesLeft = it.remainingLockTime().toMinutes()
            val secondsLeft = it.remainingLockTime().seconds % 60
            println("User ID : ${it.userId}  |  Locked, ~$minutesLeft min $secondsLeft sec remaining")
        }
    }

    private fun displayLockedAccounts() {
        val lockedAccounts = AdminController.getLockedAccounts()

        if (lockedAccounts.isEmpty()) {
            println("\nNo locked user accounts right now.")
            return
        }

        ConsoleFormatter.header("LOCKED ACCOUNTS")

        lockedAccounts.forEach {
            try {
                val user = AdminController.findUserById(it.userId)
                val minutesLeft = it.remainingLockTime().toMinutes()
                val secondsLeft = it.remainingLockTime().seconds % 60
                println("User ID: ${it.userId} | Name: ${user.name} | Locked, ~$minutesLeft min $secondsLeft sec remaining")
                println("-".repeat(50))
            } catch (_: UserNotFoundException) {
                println("\n[x] Unknown user  ID: ${it.userId}")
            }
        }
    }
}