package cab_booking.controller

import cab_booking.model.Customer
import cab_booking.model.ParcelDelivery
import cab_booking.model.types.Location
import cab_booking.service.CustomerService
import cab_booking.model.Ride
import cab_booking.model.types.VehicleCategory
import cab_booking.service.ParcelDeliveryService
import cab_booking.service.RideService

object CustomerController{

    fun getLastCompletedRideOfCustomer(customerId: String) =
        RideService.getLastCompletedRideOfCustomer(customerId)

    fun getDriverOfRide(ride: Ride) =
        CustomerService.getDriverOfRide(ride)

    fun getDriverOfParcelDelivery(parcelDelivery: ParcelDelivery) =
        CustomerService.getDriverOfParcelDelivery(parcelDelivery)

    fun rateDriver(ride: Ride, customer: Customer, rating: Int) =
        CustomerService.rateDriver(ride,customer,rating)

    fun hasActiveRideOfCustomer(customerId: String) =
        RideService.hasActiveRideOfCustomer(customerId)

    fun bookRide(
        customer: Customer,
        pickup: Location,
        drop: Location,
        vehicleCategory: VehicleCategory
    ) =
        CustomerService.bookRide(customer,pickup,drop,vehicleCategory)

    fun estimateRideFares(pickup: Location, drop: Location) =
        RideService.estimateRideFares(pickup, drop)

    fun getCurrentBookedRideOfCustomer(customerId: String) =
        RideService.getCurrentRideOfCustomer(customerId)

    fun cancelRide(ride: Ride, customer: Customer) =
        CustomerService.cancelRide(ride, customer)

    fun updateProfile(customer: Customer, name: String, phoneNumber: String) =
        CustomerService.updateProfile(customer,name,phoneNumber)


    fun bookParcelDelivery(
        customer: Customer,
        pickup: Location,
        drop: Location,
        vehicleCategory: VehicleCategory,
        contactName: String,
        contactPhone: String,
    ) =
        CustomerService.bookParcelDelivery(customer, pickup, drop, vehicleCategory,contactName, contactPhone)

    fun estimateParcelDeliveryFares(pickup: Location, drop: Location) =
        ParcelDeliveryService.estimateParcelDeliveryFares(pickup, drop)

    fun hasActiveParcelDeliveryOfCustomer(customerId: String) =
        ParcelDeliveryService.hasActiveParcelDeliveryOfCustomer(customerId)

    fun getCurrentParcelDeliveryOfCustomer(customerId: String) =
        ParcelDeliveryService.getCurrentParcelDeliveryOfCustomer(customerId)

    fun cancelParcelDelivery(parcelDelivery: ParcelDelivery, customer: Customer) =
        CustomerService.cancelParcelDelivery(parcelDelivery, customer)
}