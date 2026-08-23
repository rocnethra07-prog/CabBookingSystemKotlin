package cab_booking.controller

import cab_booking.model.Driver
import cab_booking.model.Parcel
import cab_booking.model.Ride
import cab_booking.service.DriverService
import cab_booking.model.types.Location
import cab_booking.service.ParcelService
import cab_booking.service.RideService
import cab_booking.service.VehicleService

object DriverController{

    fun findDriverById(id: String) =
        DriverService.findDriverById(id)

    fun getCurrentRideOfDriver(driverId: String) =
        RideService.getCurrentRide(driverId)

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

    fun getCurrentParcelOfDriver(driverId: String) =
        ParcelService.getCurrentParcelOfDriver(driverId)

    fun pickUpParcel(parcel: Parcel, driver: Driver) =
        DriverService.pickUpParcel(parcel, driver)

    fun deliverParcel(parcel: Parcel, driver: Driver) =
        DriverService.deliverParcel(parcel, driver)

    fun cancelParcel(parcel: Parcel, driver: Driver) =
        DriverService.cancelParcel(parcel, driver)

}