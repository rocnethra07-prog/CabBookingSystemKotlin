package cab_booking.service

import cab_booking.exception.DriverNotFoundException
import cab_booking.exception.UnauthorizedParcelActionException
import cab_booking.model.Driver
import cab_booking.model.Parcel
import cab_booking.model.User
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.DriverRepo
import cab_booking.repository.ParcelRepo
import cab_booking.service.pricing.ParcelFareCalculator
import java.math.BigDecimal
import java.time.LocalDateTime

object ParcelService {
    // Sending and receiving a parcel are the same booking mechanics - only who the
    // "other party" at the far end is changes. See ParcelMode for details.
    fun bookParcel(
        customer: User,
        pickupLocation: Location,
        dropLocation: Location,
        vehicleCategory: VehicleCategory,
        parcelMode: ParcelMode,
        contactName: String,
        contactPhone: String,
        weightKg: BigDecimal,
        parcelCategory: ParcelCategory
    ): Parcel {

        val driver = DriverService.findAvailableDriver(vehicleCategory, pickupLocation)

        val parcel = Parcel(
            customerId = customer.userId,
            driverId = driver.userId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            vehicleCategory = vehicleCategory,
            fare = ParcelFareCalculator.calculateParcelFare(vehicleCategory, pickupLocation, dropLocation,
                LocalDateTime.now(),parcelCategory),
            modeOfParcel = parcelMode,
            contactName = contactName,
            contactPhone = contactPhone,
            weightKg = weightKg,
            category = parcelCategory
        )

        ParcelRepo.save(parcel)

        driver.setAvailability(false)

        return parcel
    }

    fun estimateParcelFares(pickupLocation: Location, dropLocation: Location, parcelCategory: ParcelCategory) : Map<VehicleCategory, BigDecimal> {
        val map = mutableMapOf<VehicleCategory, BigDecimal>()
        VehicleCategory.entries.forEach {
            map[it] = ParcelFareCalculator.calculateParcelFare(it ,pickupLocation, dropLocation, LocalDateTime.now(),parcelCategory)
        }
        return map
    }

    fun getDriverForParcel(parcel: Parcel): Driver =
        DriverRepo.findByKey(parcel.driverId) ?: throw DriverNotFoundException("Driver not found for ID: ${parcel.driverId}")

    fun hasActiveParcel(customerId: String): Boolean =
        ParcelRepo.hasCurrentParcelOfCustomer(customerId)

    fun hasActiveParcelForDriver(driverId: String) : Boolean =
        ParcelRepo.hasCurrentParcelOfDriver(driverId)

    fun getCurrentParcel(customerId: String): Parcel =
        ParcelRepo.findCurrentParcelOfCustomer(customerId)

    fun getCurrentParcelOfDriver(driverId: String): Parcel =
        ParcelRepo.findCurrentParcelOfDriver(driverId)

    fun cancelParcel(
        parcel: Parcel,
        customer: User
    ) {
        if (parcel.customerId != customer.userId) {
            throw UnauthorizedParcelActionException("Only the customer who booked this parcel can cancel it.")
        }
        DriverService.cancelParcel(parcel, getDriverForParcel(parcel))
    }
}