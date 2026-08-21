package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.Ride
import cab_booking.service.DriverService
import cab_booking.model.types.Location
import cab_booking.service.CabService
import cab_booking.service.RideService

object DriverController{

    fun findDriverById(id: String) =
        DriverService.findDriverById(id)

    fun getCurrentRideOfDriver(driverId: String) =
        RideService.getCurrentRide(driverId)

    fun getCabById(cabId: String) =
        CabService.getCabById(cabId)

    fun updateProfile(driver: Driver, name: String, phone: String, location: Location) =
        DriverService.updateProfile(driver,name,phone,location)

    fun startRide(ride: Ride, driver: Driver) =
        DriverService.startRide(ride, driver)

    fun completeRide(ride: Ride, driver: Driver) =
        DriverService.completeRide(ride,driver)

    fun cancelRide(ride: Ride, driver: Driver) =
        DriverService.cancelRide(ride,driver)

    fun getDriverRideHistory(driverId: String) =
        RideService.getDriverRideHistory(driverId)

    fun getAverageRatingOfDriver(driver: Driver) =
        DriverService.getAverageRatingOfDriver(driver)

}