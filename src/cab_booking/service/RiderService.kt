package cab_booking.service
import cab_booking.exception.AvailableDriversNotFoundException
import cab_booking.repository.DriverRepo
import cab_booking.repository.RideRepo
import cab_booking.service.pricing.FareCalculator
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidRideStateException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.model.User
import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.model.types.RideStatus
import java.time.LocalTime

object RiderService {

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
        cabType: CabType
    ): Ride {

        val driver = findAvailableDriver(cabType, pickupLocation)

        val ride = Ride(
            riderId = rider.userId,
            driverId = driver.userId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            fare = FareCalculator.calculateFare(cabType, pickupLocation, dropLocation, LocalTime.now())
        )

        RideRepo.save(ride)

        markUnavailable(driver)

        return ride
    }

    private fun findAvailableDriver(
        cabType: CabType,
        pickupLocation: Location
    ): Driver {

        val matchingDrivers = DriverService.getAvailableDrivers()
            .filter { driver ->
                val cab = CabService.getCabById(driver.cabId)
                cab.cabType == cabType
            }

        if (matchingDrivers.isEmpty()) {
            throw AvailableDriversNotFoundException("[!]No $cabType drivers are available right now")
        }

        return matchingDrivers.firstOrNull {
            it.currentLocation == pickupLocation
        } ?: matchingDrivers.first()
    }

    fun getDriverForRide(ride: Ride): Driver =
        DriverRepo.findByKey(ride.driverId) ?: throw DriverNotFoundException("Driver not found for ID: ${ride.driverId}")

    fun cancelRide(
        ride: Ride,
        rider: User
    ) {
        if (ride.riderId != rider.userId) {
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

        if (ride.riderId != rider.userId) {
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