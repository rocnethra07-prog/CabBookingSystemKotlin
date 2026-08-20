package cab_booking.controller

import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.service.RiderService
import cab_booking.model.Ride
import cab_booking.model.types.CabType
import cab_booking.service.RideService

object RiderController{

    fun getLastCompletedRideOfRider(riderId: String) =
        RideService.getLastCompletedRide(riderId)

    fun getDriverForRide(ride: Ride) =
        RiderService.getDriverForRide(ride)

    fun rateDriver(ride: Ride, rider: User, rating: Int) =
        RiderService.rateDriver(ride,rider,rating)

    fun hasActiveRide(riderId: String) =
        RideService.hasActiveRide(riderId)

    fun bookRide(
        rider: User,
        pickup: Location,
        drop: Location,
        cabType: CabType
    ) =
        RiderService.bookRide(rider,pickup,drop,cabType)

    fun getCurrentBookedRide(riderId: String) =
        RideService.getCurrentBookedRide(riderId)

    fun cancelRide(ride: Ride, rider: User) =
        RiderService.cancelRide(ride, rider)

    fun getRiderRideHistory(riderId: String) =
        RideService.getRiderRideHistory(riderId)

    fun updateProfile(rider: User, name: String, phone: String) =
        RiderService.updateProfile(rider,name,phone)

}