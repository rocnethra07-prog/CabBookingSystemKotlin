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

    fun createParcel(customerId: String, driverId: String, pickupLocation: Location, dropLocation: Location,
                     vehicleCategory: VehicleCategory, parcelMode: ParcelMode, contactName: String, contactPhone: String, weightKg: BigDecimal, parcelCategory: ParcelCategory) : Parcel {
        val parcel = Parcel(
            customerId = customerId,
            driverId = driverId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            vehicleCategory = vehicleCategory,
            fare = ParcelFareCalculator.calculateParcelFare(
                vehicleCategory, pickupLocation, dropLocation,
                LocalDateTime.now(), parcelCategory, weightKg
            ),
            modeOfParcel = parcelMode,
            contactName = contactName,
            contactPhone = contactPhone,
            weightKg = weightKg,
            category = parcelCategory
        )

        ParcelRepo.save(parcel)
        return parcel
    }


    // Only the vehicles that can physically take this weight are priced, so the
    // customer is never offered a bike for a 40 kg parcel. A 3 kg parcel sees all
    // five categories; a 40 kg parcel sees Mini, Sedan and SUV; 80 kg sees only SUV.
    fun estimateParcelFares(pickupLocation: Location, dropLocation: Location, parcelCategory: ParcelCategory, weightKg: BigDecimal) : Map<VehicleCategory, BigDecimal> {
        val map = mutableMapOf<VehicleCategory, BigDecimal>()
        VehicleCategory.categoriesFor(weightKg).forEach {
            map[it] = ParcelFareCalculator.calculateParcelFare(it ,pickupLocation, dropLocation, LocalDateTime.now(),parcelCategory, weightKg)
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