package cab_booking.service

import cab_booking.auth.UserCredential
import cab_booking.model.types.CabType
import cab_booking.model.types.RideStatus
import cab_booking.model.types.UserRole
import cab_booking.exception.AuthenticationException
import cab_booking.exception.CabNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.UserNotFoundException
import cab_booking.model.Cab
import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.repository.AuthRepo
import cab_booking.repository.CabRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.RideRepo
import cab_booking.repository.UserRepo

object AdminService{
    fun isEmailRegistered(email: String): Boolean =
        UserRepo.existsByEmail(email)

    fun isLicenseNumberExists(licenseNumber: String): Boolean =
        DriverRepo.existsByLicense(licenseNumber)

    fun isRegistrationNumberExists(registrationNumber: String): Boolean =
        CabRepo.existsByRegistrationNumber(registrationNumber)

    // Driver Management
    fun addDriver(name: String, phone: String, email: String, password: String, location: Location, licenseNumber: String, cab: Cab): Driver {

        val driver = Driver(
            name = name,
            phone = phone,
            email = email,
            cabId = cab.cabId,
            licenseNumber = licenseNumber,
            currentLocation = location
        )

        try {

            DriverRepo.save(driver)
            CabRepo.save(cab)
            AuthService.saveUserCredentials(
                driver, password
            )

            return driver

        }
        catch (e: IllegalArgumentException) {
            DriverRepo.deleteByKey(driver.userId)
            CabRepo.deleteByKey(cab.cabId)
            throw e
        }
    }

    fun createCab(model: String, registrationNumber: String, cabType : CabType) : Cab{
        return Cab(
            registrationNumber = registrationNumber,
            model = model,
            cabType = cabType
        )
    }

    fun deleteDriver(driver: Driver): Boolean {

        val activeRide = RideRepo.findCurrentRideOfDriver(driver.userId)

        if (activeRide != null) {
            return false
        }

        CabRepo.deleteByKey(driver.cabId)
        DriverRepo.deleteByKey(driver.userId)
        UserRepo.deleteByEmail(driver.email)
        AuthRepo.findByUserId(driver.userId)?.let {
            AuthRepo.deleteByKey(it.userId)
        }

        return true
    }

    fun findDriverById(driverId: String): Driver =
        DriverRepo.findByKey(driverId) ?: throw DriverNotFoundException("Driver not found for ID: $driverId")

    fun getAllDrivers(): List<Driver> =
        DriverRepo.findAll()

    fun getAvailableDrivers(): List<Driver> =
        DriverRepo.findAvailableDrivers()

    fun getUnavailableDrivers(): List<Driver> =
        DriverRepo.findUnavailableDrivers()

    fun getCabForDriver(driver: Driver): Cab =
        CabRepo.findByKey(driver.cabId) ?: throw CabNotFoundException("Cab not found for ID: ${driver.cabId}")

    fun getDriverRideHistory(driverId: String): List<Ride> =
        RideRepo.findRidesByDriver(driverId)

    // Rider Management
    fun getAllRiders(): List<User> =
        UserRepo.findAll()
            .filter { it.userRole == UserRole.RIDER }

    fun getRiderRideHistory(riderId: String): List<Ride> =
        RideRepo.findRidesByRider(riderId)

    // Ride Management
    fun getAllRides(): List<Ride> = RideRepo.findAll()

    fun getRidesByStatus(status: RideStatus): List<Ride> =
        RideRepo.findRidesByStatus(status)

    fun getActiveRides(): List<Ride> =
        getRidesByStatus(RideStatus.BOOKED)

    fun getCompletedRides(): List<Ride> =
        getRidesByStatus(RideStatus.COMPLETED)

    fun getCancelledRides(): List<Ride> =
        getRidesByStatus(RideStatus.CANCELLED)

    // Cab Management
    fun getAllCabs(): List<Cab> =
        CabRepo.findAll()

    fun getCabsByType(cabType: CabType): List<Cab> =
        CabRepo.findByCabType(cabType)

    fun unlockUserAccount(userId: String) {

        val auth = AuthRepo.findByUserId(userId)
            ?: throw AuthenticationException("User authentication details not found.")

        if (!auth.isAccountLocked) {
            throw AuthenticationException("User account is already unlocked.")
        }

        auth.unlockAccount()
    }

    fun getLockedAccounts(): List<UserCredential> =
        AuthRepo.getLockedAccounts()


    fun findUserById(userId: String): User =
         UserRepo.findByUserId(userId)
            ?: throw UserNotFoundException("User not found for ID: $userId")
}