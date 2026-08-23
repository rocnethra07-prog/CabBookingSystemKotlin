package cab_booking.controller

import cab_booking.model.Parcel
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.service.CustomerService
import cab_booking.model.Ride
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.VehicleCategory
import cab_booking.service.ParcelService
import cab_booking.service.RideService
import java.math.BigDecimal

object CustomerController{

    fun getLastCompletedRideOfRider(riderId: String) =
        RideService.getLastCompletedRide(riderId)

    fun getDriverForRide(ride: Ride) =
        CustomerService.getDriverForRide(ride)

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
        CustomerService.estimateRideFares(pickup, drop)

    fun getCurrentBookedRide(riderId: String) =
        RideService.getCurrentBookedRide(riderId)

    fun cancelRide(ride: Ride, rider: User) =
        CustomerService.cancelRide(ride, rider)

    fun updateProfile(rider: User, name: String, phone: String) =
        CustomerService.updateProfile(rider,name,phone)


    fun bookParcel(
        customer: User,
        pickup: Location,
        drop: Location,
        vehicleCategory: VehicleCategory,
        parcelMode: ParcelMode,
        contactName: String,
        contactPhone: String,
        weightKg: BigDecimal,
        parcelCategory: ParcelCategory
    ) =
        ParcelService.bookParcel(customer, pickup, drop, vehicleCategory, parcelMode, contactName, contactPhone, weightKg, parcelCategory)

    fun estimateParcelFares(pickup: Location, drop: Location, parcelCategory: ParcelCategory) =
        ParcelService.estimateParcelFares(pickup, drop, parcelCategory)

    fun hasActiveParcel(customerId: String) =
        ParcelService.hasActiveParcel(customerId)

    fun getCurrentParcel(customerId: String) =
        ParcelService.getCurrentParcel(customerId)

    fun getDriverForParcel(parcel: Parcel) =
        ParcelService.getDriverForParcel(parcel)

    fun cancelParcel(parcel: Parcel, customer: User) =
        ParcelService.cancelParcel(parcel, customer)
}