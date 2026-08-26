package cab_booking.service

import cab_booking.model.ParcelDelivery
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

    fun pickUpParcelDelivery(parcelDelivery: ParcelDelivery) =
        BookingService.start(parcelDelivery, "picked up")

    fun deliverParcelDelivery(parcelDelivery: ParcelDelivery) =
        BookingService.complete(parcelDelivery, "delivered")

    fun cancelParcelDelivery(parcelDelivery: ParcelDelivery) =
        BookingService.cancel(parcelDelivery, "parcel")
    

    fun hasActiveParcelDeliveryOfCustomer(customerId: String): Boolean =
        ParcelDeliveryRepo.hasCurrentParcelDeliveryOfCustomer(customerId)

    fun hasActiveParcelDeliveryOfDriver(driverId: String) : Boolean =
        ParcelDeliveryRepo.hasCurrentParcelDeliveryOfDriver(driverId)

    fun getCurrentParcelDeliveryOfCustomer(customerId: String): ParcelDelivery =
        ParcelDeliveryRepo.findCurrentParcelDeliveryOfCustomer(customerId)

    fun getCurrentParcelDeliveryOfDriver(driverId: String): ParcelDelivery =
        ParcelDeliveryRepo.findCurrentParcelDeliveryOfDriver(driverId)

}