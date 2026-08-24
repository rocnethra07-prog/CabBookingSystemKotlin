package cab_booking.service
import cab_booking.repository.DriverRepo
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.Parcel
import cab_booking.model.Ride
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.RideStatus
import cab_booking.model.types.VehicleCategory
import cab_booking.service.ParcelService.createParcel
import cab_booking.service.pricing.RideFareCalculator
import java.math.BigDecimal
import java.time.LocalDateTime

object CustomerService {

    fun updateProfile(
        user: User,
        name: String,
        phone: String
    ) {
        user.updateName(name)
        user.updatePhone(phone)
    }

    fun bookRide(
        rider: User,
        pickupLocation: Location,
        dropLocation: Location,
        vehicleCategory: VehicleCategory
    ): Ride {

        val driver = DriverService.findAvailableDriver(vehicleCategory, pickupLocation)
        val ride = RideService.createRide(rider.userId, driver.userId, pickupLocation, dropLocation, vehicleCategory)
        markUnavailable(driver)
        return ride
    }

    fun bookParcel(
        customer: User,
        pickupLocation: Location,
        dropLocation: Location,
        vehicleCategory: VehicleCategory,
        parcelMode: ParcelMode,
        contactName: String,
        contactPhone: String,
        weightKg: BigDecimal,
        parcelCategory: ParcelCategory
    ): Parcel {

        val driver = DriverService.findAvailableDriver(vehicleCategory, pickupLocation)
        val parcel = createParcel(
            customer.userId, driver.userId,
            pickupLocation, dropLocation,
            vehicleCategory, parcelMode,
            contactName, contactPhone, weightKg, parcelCategory
        )
        markUnavailable(driver)
        return parcel
    }

    fun estimateRideFares(pickupLocation: Location, dropLocation: Location): Map<VehicleCategory, BigDecimal> {
        val map = mutableMapOf<VehicleCategory, BigDecimal>()
        VehicleCategory.entries.forEach {
            map[it] = RideFareCalculator.calculateRideFare(it, pickupLocation, dropLocation, LocalDateTime.now())
        }
        return map
    }

    fun getDriverForRide(ride: Ride): Driver =
        DriverRepo.findByKey(ride.driverId) ?: throw DriverNotFoundException("Driver not found for ID: ${ride.driverId}")

    fun cancelRide(
        ride: Ride,
        rider: User
    ) {
        if (ride.customerId != rider.userId) {
            throw UnauthorizedRideActionException("Only the rider who booked this ride can cancel it.")
        }
        DriverService.cancelRide(ride,getDriverForRide(ride))
    }

    private fun markUnavailable(driver: Driver){
        driver.setAvailability(false)
    }

    fun rateDriver(
        ride: Ride,
        rider: User,
        rating: Int
    ) {

        if (ride.customerId != rider.userId) {
            throw UnauthorizedRideActionException("Only the rider who booked this ride can rate it.")
        }

        if(ride.rating != 0) {
            throw InvalidRideStateException("Ride has already been rated.")
        }

        if(ride.rideStatus != RideStatus.COMPLETED) {
            throw InvalidRideStateException("Only completed rides can be rated.")
        }

        require(rating in 1..5) { "Rating must be between 1 and 5." }

        ride.setRatings(rating)
        val driver = DriverService.findDriverById(ride.driverId)
        addRating(driver, rating)
    }

    private fun addRating(driver: Driver, rating: Int) {
        require(rating in 1..5) { "Rating must be between 1 and 5." }
        driver.updateTotalRating(driver.totalRating + rating)
        driver.updateRatingCount(driver.ratingCount + 1)
    }
}