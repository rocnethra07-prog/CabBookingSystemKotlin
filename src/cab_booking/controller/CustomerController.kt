package cab_booking.controller

import cab_booking.model.ParcelDelivery
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.service.CustomerService
import cab_booking.model.Ride
import cab_booking.model.types.VehicleCategory
import cab_booking.service.ParcelDeliveryService
import cab_booking.service.RideService

object CustomerController{

    fun getLastCompletedRideOfRider(riderId: String) =
        RideService.getLastCompletedRide(riderId)

    fun getDriverForRide(ride: Ride) =
        CustomerService.getDriverForRide(ride)

    fun getDriverForParcelDelivery(parcelDelivery: ParcelDelivery) =
        CustomerService.getDriverForParcelDelivery(parcelDelivery)

    fun rateDriver(ride: Ride, rider: User, rating: Int) =
        CustomerService.rateDriver(ride,rider,rating)

    fun hasActiveRide(riderId: String) =
        RideService.hasActiveRideForRider(riderId)

    fun bookRide(
        rider: User,
        pickup: Location,
        drop: Location,
        vehicleCategory: VehicleCategory
    ) =
        CustomerService.bookRide(rider,pickup,drop,vehicleCategory)

    fun estimateRideFares(pickup: Location, drop: Location) =
        RideService.estimateRideFares(pickup, drop)

    fun getCurrentBookedRide(riderId: String) =
        RideService.getCurrentRideOfRider(riderId)

    fun cancelRide(ride: Ride, rider: User) =
        CustomerService.cancelRide(ride, rider)

    fun updateProfile(rider: User, name: String, phone: String) =
        CustomerService.updateProfile(rider,name,phone)


    fun bookParcelDelivery(
        customer: User,
        pickup: Location,
        drop: Location,
        vehicleCategory: VehicleCategory,
        contactName: String,
        contactPhone: String,
    ) =
        CustomerService.bookParcelDelivery(customer, pickup, drop, vehicleCategory,contactName, contactPhone)

    fun estimateParcelDeliveryFares(pickup: Location, drop: Location) =
        ParcelDeliveryService.estimateParcelDeliveryFares(pickup, drop)

    fun hasActiveParcelDelivery(customerId: String) =
        ParcelDeliveryService.hasActiveParcelDelivery(customerId)

    fun getCurrentParcelDeliveryForCustomer(customerId: String) =
        ParcelDeliveryService.getCurrentParcelDeliveryOfCustomer(customerId)

    fun cancelParcelDelivery(parcelDelivery: ParcelDelivery, customer: User) =
        CustomerService.cancelParcelDelivery(parcelDelivery, customer)
}