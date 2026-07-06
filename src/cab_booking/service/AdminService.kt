package cab_booking.service

import cab_booking.builder.DriverRegistrationData
import cab_booking.exception.CabBookingException
import cab_booking.model.*
import cab_booking.repository.*

class AdminService {

    // -------------------------------------------------
    // Validation
    // -------------------------------------------------

    fun isEmailRegistered(email: String): Boolean =
        UserRepo.existsByEmail(email)

    fun isLicenseNumberExists(licenseNumber: String): Boolean =
        DriverRepo.existsByLicense(licenseNumber)

    fun isRegistrationNumberExists(registrationNumber: String): Boolean =
        CabRepo.existsByRegistrationNumber(registrationNumber)

    // -------------------------------------------------
    // Driver Management
    // -------------------------------------------------

    fun addDriver(driverData: DriverRegistrationData): Driver {

        if (isEmailRegistered(driverData.email)) {
            throw CabBookingException("An account with this email already exists.")
        }

        if (isLicenseNumberExists(driverData.licenseNumber)) {
            throw CabBookingException("License number already exists.")
        }

        if (isRegistrationNumberExists(driverData.registrationNumber)) {
            throw CabBookingException("Registration number already exists.")
        }

        /*
            Driver must be created first because
            Cab requires Driver ID.
         */

        val driver = Driver(
            name = driverData.name,
            phone = driverData.phone,
            email = driverData.email,
            cabId = "",
            licenseNumber = driverData.licenseNumber,
            currentLocation = driverData.currentLocation
        )

        val cab = Cab(
            registrationNumber = driverData.registrationNumber,
            model = driverData.model,
            cabType = driverData.cabType,
            driverId = driver.userId
        )

        /*
            Since Driver requires Cab ID in constructor,
            recreate Driver with Cab ID.
         */

        val registeredDriver = Driver(
            name = driverData.name,
            phone = driverData.phone,
            email = driverData.email,
            cabId = cab.cabId,
            licenseNumber = driverData.licenseNumber,
            currentLocation = driverData.currentLocation
        )

        try {

            DriverRepo.save(registeredDriver)

            CabRepo.save(cab)

            AuthService().registerUserCredentials(
                registeredDriver,
                driverData.password
            )

            return registeredDriver

        } catch (e: Exception) {

            runCatching {
                DriverRepo.deleteByKey(registeredDriver.userId)
            }

            runCatching {
                CabRepo.deleteByKey(cab.cabId)
            }

            throw CabBookingException(e.message ?: "Unable to register driver.")
        }
    }

    fun deleteDriver(driverId: String): Boolean {

        val driver = DriverRepo.findByKey(driverId)

        val activeRide =
            RideRepo.findCurrentRideOfDriver(driverId)

        if (activeRide != null) {
            return false
        }

        CabRepo.findAll()
            .firstOrNull { it.driverId == driverId }
            ?.let {
                CabRepo.deleteByKey(it.cabId)
            }

        DriverRepo.deleteByKey(driverId)

        UserRepo.deleteByEmail(driver.email)

        AuthRepo.findByUserId(driver.userId)?.let {
            AuthRepo.deleteByKey(driver.userId)
        }

        return true
    }

    fun findDriverById(driverId: String): Driver =
        DriverRepo.findByKey(driverId)

    fun getAllDrivers(): List<Driver> =
        DriverRepo.findAll()

    fun getAvailableDrivers(): List<Driver> =
        DriverRepo.findAvailableDrivers()

    fun getUnavailableDrivers(): List<Driver> =
        DriverRepo.findUnavailableDrivers()

    fun getCabForDriver(driver: Driver): Cab =
        CabRepo.findAll().first {
            it.driverId == driver.userId
        }

    fun getDriverRideHistory(driverId: String): List<Ride> =
        RideRepo.findRidesByDriver(driverId)
    // -------------------------------------------------
    // Rider Management
    // -------------------------------------------------

    fun getAllRiders(): List<User> =
        UserRepo.findAll()
            .filter { it.userRole == UserRole.RIDER }

    fun getRiderRideHistory(riderId: String): List<Ride> =
        RideRepo.findRidesByRider(riderId)

    // -------------------------------------------------
    // Ride Management
    // -------------------------------------------------

    fun getAllRides(): List<Ride> =
        RideRepo.findAll()

    fun getRidesByStatus(status: RideStatus): List<Ride> =
        RideRepo.findRidesByStatus(status)

    fun getActiveRides(): List<Ride> =
        getRidesByStatus(RideStatus.BOOKED)

    fun getCompletedRides(): List<Ride> =
        getRidesByStatus(RideStatus.COMPLETED)

    fun getCancelledRides(): List<Ride> =
        getRidesByStatus(RideStatus.CANCELLED)

    // -------------------------------------------------
    // Cab Management
    // -------------------------------------------------

    fun getAllCabs(): List<Cab> =
        CabRepo.findAll()

    fun getCabsByType(cabType: CabType): List<Cab> =
        CabRepo.findByCabType(cabType)
}