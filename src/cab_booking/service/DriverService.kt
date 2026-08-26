package cab_booking.service

import cab_booking.exception.AvailableDriversNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.model.Driver
import cab_booking.model.types.Location
import cab_booking.model.Ride
import cab_booking.repository.DriverRepo
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.ParcelDelivery
import cab_booking.model.Vehicle
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.CredentialRepo
import cab_booking.repository.UserRepo
import java.math.BigDecimal

object DriverService {

    fun isLicenseNumberExists(licenseNumber: String): Boolean =
        DriverRepo.existsByLicense(licenseNumber)

    fun createDriver(name: String, phone: String, email: String, password: String, location: Location, licenseNumber: String, vehicle: Vehicle): Driver {

        val driver = Driver(
            name = name,
            phoneNumber = phone,
            email = email,
            assignedVehicleId = vehicle.vehicleId,
            licenseNumber = licenseNumber,
            currentLocation = location
        )

        try {

            DriverRepo.save(driver)
            AuthService.saveUserAndCredentials(
                driver, password
            )

            return driver

        }
        catch (e: IllegalArgumentException) {
            DriverRepo.deleteByKey(driver.userId)
            UserRepo.deleteByEmail(driver.email)
            VehicleService.deleteVehicle(vehicle.vehicleId)   // Remove the vehicle so that no vehicle is left without a driver
            throw e
        }
    }

    fun hasActiveRideOfDriver(driverId: String) =
        RideService.hasActiveRideOfDriver(driverId)

    fun hasActiveParcelDeliveryOfDriver(driverId: String) =
        ParcelDeliveryService.hasActiveParcelDeliveryOfDriver(driverId)

    fun deleteDriver(driver: Driver): Boolean {

        if (hasActiveRideOfDriver(driver.userId) || hasActiveParcelDeliveryOfDriver(driver.userId)) {
            return false
        }

        VehicleService.deleteVehicle(driver.assignedVehicleId)
        DriverRepo.deleteByKey(driver.userId)
        UserRepo.deleteByEmail(driver.email)
        CredentialRepo.deleteByKey(driver.userId)

        return true
    }

    fun findAvailableDriver(
        vehicleCategory: VehicleCategory,
        pickupLocation: Location
    ): Driver {

        val matchingDrivers = getAvailableDrivers()
            .filter { driver ->
                VehicleService.getVehicleById(driver.assignedVehicleId).vehicleCategory == vehicleCategory
            }

        if (matchingDrivers.isEmpty()) {
            throw AvailableDriversNotFoundException(vehicleCategory)
        }

        return matchingDrivers.firstOrNull {
            it.currentLocation == pickupLocation
        } ?: matchingDrivers.first()
    }


    fun updateProfile(
        driver: Driver,
        name: String,
        phoneNumber: String,
        location: Location
    ) {
        driver.updateName(name)
        driver.updatePhoneNumber(phoneNumber)
        driver.updateCurrentLocation(location)
    }

    fun startRide(
        ride: Ride,
        driver: Driver
    ){
        validateRideOwnership(ride, driver)
        RideService.startRide(ride)
        driver.updateCurrentLocation(ride.pickupLocation)
    }

    fun completeRide(
        ride: Ride,
        driver: Driver
    ) {
        validateRideOwnership(ride, driver)
        RideService.completeRide(ride)
        driver.updateCurrentLocation(ride.dropLocation)
        addEarnings(driver, ride.fare)
        markAvailable(driver)
    }

    fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {

        validateRideOwnership(ride, driver)
        RideService.cancelRide(ride)
        markAvailable(driver)
    }

    fun markAvailable(driver: Driver) {
        driver.setAvailability(true)
    }

    fun markUnavailable(driver: Driver) {
        driver.setAvailability(false)
    }

    fun getAverageRatingOfDriver(driver: Driver) : Double{
        return if (driver.totalRatingCount == 0){
            0.0
        } else {
            driver.totalRating.toDouble() / driver.totalRatingCount
        }
    }

    private fun addEarnings(driver: Driver, amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Amount must be greater than zero." }
        driver.updateTotalEarnings(driver.totalEarnings + amount)
    }

    fun addRating(driver: Driver, rating: Int) {
        require(rating in 1..5) { "Rating must be between 1 and 5." }
        driver.updateTotalRating(driver.totalRating + rating)
        driver.updateTotalRatingCount(driver.totalRatingCount + 1)
    }
    fun getAllDrivers(): List<Driver> =
        DriverRepo.findAll()

    fun getAvailableDrivers(): List<Driver> =
        DriverRepo.findAvailableDrivers()

    fun getUnavailableDrivers(): List<Driver> =
        DriverRepo.findUnavailableDrivers()

    fun findDriverById(driverId: String): Driver =
        DriverRepo.findByKey(driverId) ?: throw DriverNotFoundException("Driver not found for ID: $driverId")


    // Parcel actions
    fun pickUpParcelDelivery(parcelDelivery: ParcelDelivery, driver: Driver) {
        validateParcelDeliveryOwnership(parcelDelivery, driver)
        ParcelDeliveryService.pickUpParcelDelivery(parcelDelivery)
        driver.updateCurrentLocation(parcelDelivery.pickupLocation)
    }

    fun deliverParcelDelivery(parcelDelivery: ParcelDelivery, driver: Driver) {
        validateParcelDeliveryOwnership(parcelDelivery, driver)
        ParcelDeliveryService.deliverParcelDelivery(parcelDelivery)
        driver.updateCurrentLocation(parcelDelivery.dropLocation)
        addEarnings(driver, parcelDelivery.fare)
        markAvailable(driver)
    }

    fun cancelParcelDelivery(parcelDelivery: ParcelDelivery, driver: Driver) {
        validateParcelDeliveryOwnership(parcelDelivery, driver)
        ParcelDeliveryService.cancelParcelDelivery(parcelDelivery)
        markAvailable(driver)
    }

    private fun validateRideOwnership(ride: Ride, driver: Driver) {
        if (ride.driverId != driver.userId) {
            throw UnauthorizedRideActionException("Only the assigned driver can perform this action.")
        }
    }

    private fun validateParcelDeliveryOwnership(parcelDelivery: ParcelDelivery, driver: Driver) {
        if (parcelDelivery.driverId != driver.userId) {
            throw UnauthorizedParcelActionException("Only the assigned driver can perform this action.")
        }
    }

}