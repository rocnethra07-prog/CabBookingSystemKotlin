package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.service.AuthService
import cab_booking.service.DriverService
import cab_booking.model.types.Location
import cab_booking.service.RideService

object DriverController{

    fun findDriverById(id: String) =
        DriverService.findDriverById(id)

    fun getCurrentRideOfDriver(driver: Driver) =
        RideService.getCurrentRide(driver)

    fun updateProfile(driver: Driver, name: String, phone: String, location: Location) =
        DriverService.updateProfile(driver,name,phone,location)

    fun completeRide(ride: Ride, driver: Driver) =
        DriverService.completeRide(ride,driver)

    fun cancelRide(ride: Ride, driver: Driver) =
        DriverService.cancelRide(ride,driver)

    fun getRidesByDriver(driver: Driver) =
        RideService.getRidesByDriver(driver)

    fun getAverageRatingOfDriver(driver: Driver) =
        DriverService.getAverageRatingOfDriver(driver)

    fun changePassword(driver: Driver, currentPassword: String, newPassword: String){
        AuthService.changePassword(driver,currentPassword,newPassword)
    }
}