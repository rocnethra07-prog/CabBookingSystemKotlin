package cab_booking.service

import cab_booking.exception.InvalidBookingStateException
import cab_booking.model.ParcelDelivery
import cab_booking.model.types.BookingStatus
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.ParcelDeliveryRepo
import cab_booking.service.pricing.ParcelFareCalculator
import java.math.BigDecimal
import java.time.LocalDateTime

object ParcelDeliveryService {
    // Sending and receiving a parcel are the same booking mechanics - only who the
    // "other party" at the far end is changes. See ParcelMode for details.

    fun createParcelDelivery(vehicleCategory: VehicleCategory,customerId: String, driverId: String, pickupLocation: Location, dropLocation: Location, receiverName: String, receiverPhoneNumber: String) : ParcelDelivery {
        val parcelDelivery = ParcelDelivery(
            customerId = customerId,
            driverId = driverId,
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            fare = ParcelFareCalculator.calculateBaseFare(vehicleCategory,pickupLocation,dropLocation, LocalDateTime.now()),
            receiverName = receiverName,
            receiverPhoneNumber = receiverPhoneNumber
        )
        ParcelDeliveryRepo.save(parcelDelivery)
        return parcelDelivery
    }


    fun estimateParcelDeliveryFares(pickupLocation: Location, dropLocation: Location) : Map<VehicleCategory, BigDecimal> {
        val map = mutableMapOf<VehicleCategory, BigDecimal>()
        VehicleCategory.entries.forEach {
            map[it] = ParcelFareCalculator.calculateBaseFare(it ,pickupLocation, dropLocation, LocalDateTime.now())
        }
        return map

    }

    fun markAsPickedUp(parcelDelivery: ParcelDelivery) {
        if (parcelDelivery.status != BookingStatus.BOOKED) {
            throw InvalidBookingStateException("Only booked parcels can be picked up.")
        }

        parcelDelivery.updateStatus(BookingStatus.STARTED)
    }

    fun markAsDelivered(parcelDelivery: ParcelDelivery) {
        if (parcelDelivery.status != BookingStatus.STARTED) {
            throw InvalidBookingStateException("Only picked-up parcels can be delivered.")
        }

        parcelDelivery.updateStatus(BookingStatus.COMPLETED)
        parcelDelivery.setCompletedAt(LocalDateTime.now())
    }

    fun markAsCancelled(parcelDelivery: ParcelDelivery) {
        if (parcelDelivery.status != BookingStatus.BOOKED) {
            throw InvalidBookingStateException("Only booked parcels can be cancelled.")
        }

        parcelDelivery.updateStatus(BookingStatus.CANCELLED)
        parcelDelivery.setCancelledAt(LocalDateTime.now())
    }

    fun hasActiveParcelDelivery(customerId: String): Boolean =
        ParcelDeliveryRepo.hasCurrentParcelDeliveryOfCustomer(customerId)

    fun hasActiveParcelForDriver(driverId: String) : Boolean =
        ParcelDeliveryRepo.hasCurrentParcelDeliveryOfDriver(driverId)

    fun getCurrentParcelDeliveryOfCustomer(customerId: String): ParcelDelivery =
        ParcelDeliveryRepo.findCurrentParcelDeliveryOfCustomer(customerId)

    fun getCurrentParcelDeliveryOfDriver(driverId: String): ParcelDelivery =
        ParcelDeliveryRepo.findCurrentParcelDeliveryOfDriver(driverId)

}