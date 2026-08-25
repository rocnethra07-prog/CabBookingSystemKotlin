package cab_booking.service
import cab_booking.repository.DriverRepo
import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.InvalidDispatchStateException
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.exception.UnauthorizedRideActionException
import cab_booking.model.Driver
import cab_booking.model.ParcelDelivery
import cab_booking.model.Ride
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.model.types.DispatchStatus
import cab_booking.model.types.VehicleCategory
import cab_booking.service.ParcelDeliveryService.createParcelDelivery

object CustomerService {

    fun updateProfile(
        user: User,
        name: String,
        phone: String
    ) {
        user.updateName(name)
        user.updatePhoneNumber(phone)
    }

    fun bookRide(
        rider: User,
        pickupLocation: Location,
        dropLocation: Location,
        vehicleCategory: VehicleCategory
    ): Ride {

        val driver = DriverService.findAvailableDriver(vehicleCategory, pickupLocation)
        val ride = RideService.createRide(rider.userId, driver.userId, pickupLocation, dropLocation, vehicleCategory)
        DriverService.markUnavailable(driver)
        return ride
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

    fun rateDriver(
        ride: Ride,
        rider: User,
        rating: Int
    ) {

        if (ride.customerId != rider.userId) {
            throw UnauthorizedRideActionException("Only the rider who booked this ride can rate it.")
        }

        if(ride.ratings != 0) {
            throw InvalidDispatchStateException("Ride has already been rated.")
        }

        if(ride.status != DispatchStatus.COMPLETED) {
            throw InvalidDispatchStateException("Only completed rides can be rated.")
        }

        RideService.rateRide(ride, rating)

        DriverService.addRatings(DriverService.findDriverById(ride.driverId), rating)

    }

    fun bookParcelDelivery(
        customer: User,
        pickupLocation: Location,
        dropLocation: Location,
        vehicleCategory: VehicleCategory,
        receiverName: String,
        receiverPhoneNumber: String,
    ): ParcelDelivery {

        val driver = DriverService.findAvailableDriver(vehicleCategory, pickupLocation)
        val parcel = createParcelDelivery(
            vehicleCategory,customer.userId, driver.userId,
            pickupLocation, dropLocation,
            receiverName, receiverPhoneNumber
        )
        DriverService.markUnavailable(driver)
        return parcel
    }

    fun cancelParcelDelivery(
        parcelDelivery: ParcelDelivery,
        customer: User
    ) {
        if (parcelDelivery.customerId != customer.userId) {
            throw UnauthorizedParcelActionException("Only the customer who booked this parcel can cancel it.")
        }
        DriverService.cancelParcelDelivery(parcelDelivery, getDriverForParcelDelivery(parcelDelivery))
    }

    fun getDriverForParcelDelivery(parcelDelivery: ParcelDelivery): Driver =
        DriverRepo.findByKey(parcelDelivery.driverId) ?: throw DriverNotFoundException("Driver not found for this parcel.")


}