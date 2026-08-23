package cab_booking.service

import cab_booking.exception.AvailableDriversNotFoundException
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidParcelStateException
import cab_booking.model.Driver
import cab_booking.model.types.Location
import cab_booking.model.Ride
import cab_booking.model.types.RideStatus
import cab_booking.repository.DriverRepo
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Parcel
import cab_booking.model.Vehicle
import cab_booking.model.types.ParcelStatus
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo
import java.math.BigDecimal
import java.time.LocalDateTime

object DriverService {

    fun isLicenseNumberExists(licenseNumber: String): Boolean =
        DriverRepo.existsByLicense(licenseNumber)

    fun createDriver(name: String, phone: String, email: String, password: String, location: Location, licenseNumber: String, vehicle: Vehicle): Driver {

        val driver = Driver(
            name = name,
            phone = phone,
            email = email,
            vehicleId = vehicle.vehicleId,
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

    fun hasActiveRideForDriver(driverId: String) =
        RideService.hasActiveRideForDriver(driverId)

    fun hasActiveParcelForDriver(driverId: String) =
        ParcelService.hasActiveParcelForDriver(driverId)

    fun deleteDriver(driver: Driver): Boolean {

        if (hasActiveRideForDriver(driver.userId) || hasActiveParcelForDriver(driver.userId)) {
            return false
        }

        VehicleService.deleteVehicle(driver.vehicleId)
        DriverRepo.deleteByKey(driver.userId)
        UserRepo.deleteByEmail(driver.email)
        AuthRepo.deleteByKey(driver.userId)

        return true
    }

    fun findAvailableDriver(
        vehicleCategory: VehicleCategory,
        pickupLocation: Location
    ): Driver {

        val matchingDrivers = getAvailableDrivers()
            .filter { driver ->
                VehicleService.getVehicleById(driver.vehicleId).vehicleCategory == vehicleCategory
            }

        if (matchingDrivers.isEmpty()) {
            throw AvailableDriversNotFoundException("[!]No $vehicleCategory drivers are available right now")
        }

        return matchingDrivers.firstOrNull {
            it.currentLocation == pickupLocation
        } ?: matchingDrivers.first()
    }


    fun updateProfile(
        driver: Driver,
        name: String,
        phone: String,
        location: Location
    ) {
        driver.updateName(name)
        driver.updatePhone(phone)
        driver.updateCurrentLocation(location)
    }

    fun startRide(
        ride: Ride,
        driver: Driver
    ){
        markRideAsOngoing(ride)
        driver.updateCurrentLocation(ride.pickupLocation)
    }

    fun completeRide(
        ride: Ride,
        driver: Driver
    ) {
        endRide(ride, driver){ ride -> markRideAsCompleted(ride)}
        driver.updateCurrentLocation(ride.dropLocation)
        addEarnings(driver,ride.fare)
    }

    fun cancelRide(
        ride: Ride,
        driver: Driver
    ) {
       endRide(ride, driver){ ride -> markRideAsCancelled(ride)}
    }

    private fun endRide(
        ride: Ride,
        driver : Driver,
        action: (Ride) -> Unit
    ){
        validateRideOwnership(ride, driver)
        action(ride)
        markAvailable(driver)
    }

    private fun markRideAsOngoing(ride: Ride){
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw InvalidRideStateException("Only booked rides can be started.")
        }

        ride.updateRideStatus(RideStatus.STARTED)
    }

    private fun markAvailable(driver: Driver) {
        driver.setAvailability(true)
    }

    private fun markRideAsCompleted(ride: Ride){
        if(ride.rideStatus != RideStatus.STARTED) {
            throw InvalidRideStateException("Only ongoing rides can be completed.")
        }

        ride.updateRideStatus(RideStatus.COMPLETED)
        ride.setCompletedAt(LocalDateTime.now())
    }

    private fun markRideAsCancelled(ride: Ride){
        if(ride.rideStatus != RideStatus.BOOKED) {
            throw InvalidRideStateException("Only booked rides can be cancelled.")
        }

        ride.updateRideStatus(RideStatus.CANCELLED)
        ride.setCancelledAt(LocalDateTime.now())
    }

    private fun validateRideOwnership(
        ride: Ride,
        driver: Driver
    ) {

        if (ride.driverId != driver.userId) {
            throw UnauthorizedRideActionException("Only the assigned driver can perform this action.")
        }
    }

    fun getAverageRatingOfDriver(driver: Driver) : Double{
        return if (driver.ratingCount == 0){
            0.0
        } else {
            driver.totalRating.toDouble() / driver.ratingCount
        }
    }

    private fun addEarnings(driver: Driver, amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Amount must be greater than zero." }
        driver.updateEarnings(driver.earnings + amount)
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
    fun pickUpParcel(parcel: Parcel, driver: Driver) {
        validateParcelOwnership(parcel, driver)
        if (parcel.parcelStatus != ParcelStatus.BOOKED) {
            throw InvalidParcelStateException("Only booked parcels can be picked up.")
        }
        parcel.updateParcelStatus(ParcelStatus.PICKED_UP)
        driver.updateCurrentLocation(parcel.pickupLocation)
    }

    fun deliverParcel(parcel: Parcel, driver: Driver) {
        validateParcelOwnership(parcel, driver)
        if (parcel.parcelStatus != ParcelStatus.PICKED_UP) {
            throw InvalidParcelStateException("Only picked-up parcels can be delivered.")
        }
        parcel.updateParcelStatus(ParcelStatus.DELIVERED)
        parcel.setDeliveredAt(LocalDateTime.now())
        driver.updateCurrentLocation(parcel.dropLocation)
        addEarnings(driver, parcel.fare)
        driver.setAvailability(true)
    }

    fun cancelParcel(parcel: Parcel, driver: Driver) {
        validateParcelOwnership(parcel, driver)
        if (parcel.parcelStatus != ParcelStatus.BOOKED) {
            throw InvalidParcelStateException("Only booked parcels can be cancelled.")
        }
        parcel.updateParcelStatus(ParcelStatus.CANCELLED)
        parcel.setCancelledAt(LocalDateTime.now())
        driver.setAvailability(true)
    }

    private fun validateParcelOwnership(parcel: Parcel, driver: Driver) {
        if (parcel.driverId != driver.userId) {
            throw UnauthorizedParcelActionException("Only the assigned driver can perform this action.")
        }
    }

}