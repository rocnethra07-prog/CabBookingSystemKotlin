package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.model.types.ParcelDeliveryType
import cab_booking.util.Validator
import java.math.BigDecimal

class ParcelRide(
     riderId: String,
     driverId: String,
     pickupLocation: Location,
     dropLocation: Location,
     fare: BigDecimal,
     val parcelId : String,
     val contactName: String,
     val contactPhone: String,
     val parcelDeliveryType: ParcelDeliveryType
) : Ride(riderId, driverId, pickupLocation, dropLocation, fare) {

    init {
        require(parcelId.isNotBlank()){ "Parcel ID cannot be blank. "}
        require(Validator.isValidName(contactName)) { "Contact name must contain minimum 3 characters. Contact name cannot be blank." }
        require(Validator.isValidPhone(contactPhone)) { "Invalid contact phone number format." }
    }

}