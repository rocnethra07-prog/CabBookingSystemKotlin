package cab_booking.controller

import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.service.AuthService
import cab_booking.service.RiderService
import cab_booking.model.Ride
import cab_booking.model.types.CabType
import cab_booking.service.RideService

object RiderController{

    fun getLastCompletedRideOfRider(rider: User) =
        RideService.getLastCompletedRide(rider)

    fun getDriverForRide(ride: Ride) =
        RiderService.getDriverForRide(ride)

    fun rateDriver(ride: Ride, rider: User, rating: Int) =
        RiderService.rateDriver(ride,rider,rating)

    fun hasActiveRide(rider: User) =
        RideService.hasActiveRide(rider)

    fun bookRide(
        rider: User,
        pickup: Location,
        drop: Location,
        cabType: CabType
    ) =
        RiderService.bookRide(rider,pickup,drop,cabType)

    fun getCurrentBookedRide(rider: User) =
        RideService.getCurrentBookedRide(rider)

    fun cancelRide(ride: Ride, rider: User) =
        RiderService.cancelRide(ride, rider)

    fun getRidesByRider(rider: User) =
        RideService.getRidesByRider(rider)

    fun updateProfile(rider: User, name: String, phone: String) =
        RiderService.updateProfile(rider,name,phone)

    fun changePassword(rider: User, currentPassword: String, newPassword: String){
        AuthService.changePassword(rider,currentPassword,newPassword)
    }
}
