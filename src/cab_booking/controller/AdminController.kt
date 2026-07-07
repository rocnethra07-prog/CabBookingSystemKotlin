package cab_booking.controller

import cab_booking.exception.CabBookingException
import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.service.AdminService
import cab_booking.util.InputUtil
import cab_booking.builder.DriverRegistrationData

class AdminController(
    private val adminService: AdminService
) {

    fun adminDashboard() {

        while (true) {

            println("\n========== ADMIN MENU ==========")
            println("1. Driver Management")
            println("2. Rider Management")
            println("3. Ride Management")
            println("4. Cab Management")
            println("0. Logout")

            when (readln().trim()) {

                "1" -> driverManagementMenu()

                "2" -> riderManagementMenu()

                "3" -> rideManagementMenu()

                "4" -> cabManagementMenu()

                "0" -> {
                    println("\nLogged out successfully.")
                    return
                }

                else -> println("Invalid choice.")
            }
        }
    }

    // --------------------------------------------------------
    // DRIVER MANAGEMENT
    // --------------------------------------------------------

    private fun driverManagementMenu() {

        while (true) {

            println("\n========== DRIVER MANAGEMENT ==========")
            println("1. Add Driver")
            println("2. Delete Driver")
            println("3. View All Drivers")
            println("4. View Available Drivers")
            println("5. View Unavailable Drivers")
            println("6. Search Driver")
            println("7. Driver Ride History")
            println("0. Back")

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

        val name = InputUtil.getName()

        val phone = InputUtil.getPhone()

        var email: String

        while (true) {

            email = InputUtil.getEmail()

            if (!adminService.isEmailRegistered(email))
                break

            println("Email already registered.")
        }

        val password = InputUtil.getPassword()

        val currentLocation = InputUtil.selectLocation()

        var licenseNumber: String

        while (true) {

            licenseNumber = InputUtil.getNonEmptyInput(
                "License Number : ",
                "License number cannot be empty."
            )

            if (!adminService.isLicenseNumberExists(licenseNumber))
                break

            println("License number already exists.")
        }

        val model = InputUtil.getNonEmptyInput(
            "Car Model : ",
            "Model cannot be empty."
        )

        var registrationNumber: String

        while (true) {

            registrationNumber = InputUtil.getNonEmptyInput(
                "Registration Number : ",
                "Registration number cannot be empty."
            )

            if (!adminService.isRegistrationNumberExists(registrationNumber))
                break

            println("Registration number already exists.")
        }

        val cabType = InputUtil.selectCabType()

        val driverData = DriverRegistrationData(
            name = name,
            phone = phone,
            email = email,
            password = password,
            currentLocation = currentLocation,
            licenseNumber = licenseNumber,
            model = model,
            registrationNumber = registrationNumber,
            cabType = cabType
        )

        try {

            val driver = adminService.addDriver(driverData)

            println("\nDriver Registered Successfully.\n")

            println(driver)

        } catch (e: CabBookingException) {

            println(e.message)
        }
    }

    private fun deleteDriver() {

        println("\n========== DELETE DRIVER ==========")

        val driverId = InputUtil.getNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {

            val driver = adminService.findDriverById(driverId)

            println("\nDriver Details")
            println(driver)

            if (!InputUtil.getYesOrNo("Delete this driver?")) {
                println("Deletion cancelled.")
                return
            }

            if (adminService.deleteDriver(driverId)) {
                println("Driver deleted successfully.")
            } else {
                println("Driver has an active ride and cannot be deleted.")
            }

        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun viewAllDrivers() {

        val drivers = adminService.getAllDrivers()

        if (drivers.isEmpty()) {
            println("\nNo drivers found.")
            return
        }

        println("\n========== ALL DRIVERS ==========")

        drivers.forEach {
            println(it)
            println("-".repeat(50))
        }
    }

    private fun viewAvailableDrivers() {

        val drivers = adminService.getAvailableDrivers()

        if (drivers.isEmpty()) {
            println("\nNo available drivers.")
            return
        }

        println("\n========== AVAILABLE DRIVERS ==========")

        drivers.forEach {
            println(it)
            println("-".repeat(50))
        }
    }

    private fun viewUnavailableDrivers() {

        val drivers = adminService.getUnavailableDrivers()

        if (drivers.isEmpty()) {
            println("\nNo unavailable drivers.")
            return
        }

        println("\n========== UNAVAILABLE DRIVERS ==========")

        drivers.forEach {
            println(it)
            println("-".repeat(50))
        }
    }

    private fun searchDriver() {

        val driverId = InputUtil.getNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {

            val driver = adminService.findDriverById(driverId)

            println("\nDriver Details")
            println(driver)

            println("\nAssigned Cab")
            println(adminService.getCabForDriver(driver))

        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun driverRideHistory() {

        val driverId = InputUtil.getNonEmptyInput(
            "Enter Driver ID : ",
            "Driver ID cannot be empty."
        )

        try {

            val rides = adminService.getDriverRideHistory(driverId)

            if (rides.isEmpty()) {
                println("\nNo rides found.")
                return
            }

            println("\n========== DRIVER RIDE HISTORY ==========")

            rides.forEach {
                println(it)
                println("-".repeat(50))
            }

        } catch (e: Exception) {
            println(e.message)
        }
    }

    // --------------------------------------------------------
    // RIDER MANAGEMENT
    // --------------------------------------------------------

    private fun riderManagementMenu() {

        while (true) {

            println("\n========== RIDER MANAGEMENT ==========")
            println("1. View All Riders")
            println("2. Rider Ride History")
            println("0. Back")

            when (readln().trim()) {

                "1" -> viewAllRiders()

                "2" -> viewRiderRideHistory()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun viewAllRiders() {

        val riders = adminService.getAllRiders()

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

        val riderId = InputUtil.getNonEmptyInput(
            "Enter Rider ID : ",
            "Rider ID cannot be empty."
        )

        try {

            val rides = adminService.getRiderRideHistory(riderId)

            if (rides.isEmpty()) {
                println("\nNo rides found.")
                return
            }

            println("\n========== RIDER RIDE HISTORY ==========")

            rides.forEach {
                println(it)
                println("-".repeat(50))
            }

        } catch (e: Exception) {
            println(e.message)
        }
    }

    // --------------------------------------------------------
    // RIDE MANAGEMENT
    // --------------------------------------------------------

    private fun rideManagementMenu() {

        while (true) {

            println("\n========== RIDE MANAGEMENT ==========")
            println("1. View All Rides")
            println("2. Active Rides")
            println("3. Completed Rides")
            println("4. Cancelled Rides")
            println("0. Back")

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
        adminService.getAllRides(),
        "ALL RIDES"
    )

    private fun viewActiveRides() = displayRides(
        adminService.getActiveRides(),
        "ACTIVE RIDES"
    )

    private fun viewCompletedRides() = displayRides(
        adminService.getCompletedRides(),
        "COMPLETED RIDES"
    )

    private fun viewCancelledRides() = displayRides(
        adminService.getCancelledRides(),
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

            println("\n========== CAB MANAGEMENT ==========")
            println("1. View All Cabs")
            println("2. View Cabs By Type")
            println("0. Back")

            when (readln().trim()) {

                "1" -> viewAllCabs()

                "2" -> viewCabsByType()

                "0" -> return

                else -> println("Invalid choice.")
            }
        }
    }

    private fun viewAllCabs() {

        val cabs = adminService.getAllCabs()

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

        val cabType = InputUtil.selectCabType()

        val cabs = adminService.getCabsByType(cabType)

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
}