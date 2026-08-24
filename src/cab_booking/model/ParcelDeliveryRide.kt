package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.model.types.ParcelDeliveryType
import cab_booking.util.Validator
import java.math.BigDecimal

class ParcelDeliveryRide(
     riderId: String,
     driverId: String,
     pickupLocation: Location,
     dropLocation: Location,
     fare: BigDecimal,
     val parcelId : String,
     val contactPersonName: String,
     val contactPersonPhoneNumber: String,
     val parcelDeliveryType: ParcelDeliveryType
) : Ride(riderId, driverId, pickupLocation, dropLocation, fare) {

    init {
        require(parcelId.isNotBlank()){ "Parcel ID cannot be blank. "}
        require(Validator.isValidName(contactPersonName)) { "Contact name must contain minimum 3 characters. Contact name cannot be blank." }
        require(Validator.isValidPhone(contactPersonPhoneNumber)) { "Invalid contact phone number format." }
    }

}