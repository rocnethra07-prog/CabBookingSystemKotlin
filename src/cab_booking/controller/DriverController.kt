package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.ParcelDelivery
import cab_booking.model.Ride
import cab_booking.service.DriverService
import cab_booking.model.types.Location
import cab_booking.service.ParcelDeliveryService
import cab_booking.service.RideService
import cab_booking.service.VehicleService

object DriverController{

    fun findDriverById(id: String) =
        DriverService.findDriverById(id)

    fun getCurrentRideOfDriver(driverId: String) =
        RideService.getCurrentRideOfDriver(driverId)

    fun getVehicleById(vehicleId: String) =
        VehicleService.getVehicleById(vehicleId)

    fun updateProfile(driver: Driver, name: String, phone: String, location: Location) =
        DriverService.updateProfile(driver,name,phone,location)

    fun startRide(ride: Ride, driver: Driver) =
        DriverService.startRide(ride, driver)

    fun completeRide(ride: Ride, driver: Driver) =
        DriverService.completeRide(ride,driver)

    fun cancelRide(ride: Ride, driver: Driver) =
        DriverService.cancelRide(ride,driver)

    fun getAverageRatingOfDriver(driver: Driver) =
        DriverService.getAverageRatingOfDriver(driver)

    fun getCurrentParcelDeliveryOfDriver(driverId: String) =
        ParcelDeliveryService.getCurrentParcelDeliveryOfDriver(driverId)

    fun pickUpParcelDelivery(parcelDelivery: ParcelDelivery, driver: Driver) =
        DriverService.pickUpParcelDelivery(parcelDelivery, driver)

    fun deliverParcelDelivery(parcelDelivery: ParcelDelivery, driver: Driver) =
        DriverService.deliverParcelDelivery(parcelDelivery, driver)

    fun cancelParcelDelivery(parcelDelivery: ParcelDelivery, driver: Driver) =
        DriverService.cancelParcelDelivery(parcelDelivery, driver)

}