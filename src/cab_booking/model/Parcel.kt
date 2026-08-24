package cab_booking.model

import cab_booking.exception.InvalidParcelStateException
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.ParcelStatus
import cab_booking.util.IdGenerator
import cab_booking.util.Validator
import cab_booking.console.input.toDisplayString
import java.math.BigDecimal
import java.time.LocalDateTime

class Parcel(
    customerId: String,
    driverId: String,
    pickupLocation: Location,
    dropLocation: Location,
    fare: BigDecimal,
    val modeOfParcel: ParcelMode,
    val contactName: String,
    val contactPhone: String,
    val weightKg: BigDecimal,
    val category: ParcelCategory
) : Booking(customerId, driverId, pickupLocation, dropLocation, fare) {

    companion object {
        val MIN_WEIGHT_KG: BigDecimal = BigDecimal("0.1")
        val MAX_WEIGHT_KG: BigDecimal = BigDecimal("100.0")
    }

    val parcelId: String = IdGenerator.generateParcelId()

    var parcelStatus: ParcelStatus = ParcelStatus.BOOKED
        private set

    var deliveredAt: LocalDateTime? = null
        private set

    fun updateParcelStatus(newParcelStatus: ParcelStatus){
        if(
            (parcelStatus == ParcelStatus.DELIVERED || parcelStatus == ParcelStatus.CANCELLED) ||
            (parcelStatus != ParcelStatus.BOOKED) && ((newParcelStatus == ParcelStatus.CANCELLED) || (newParcelStatus == ParcelStatus.PICKED_UP)) ||
            (parcelStatus != ParcelStatus.PICKED_UP && newParcelStatus == ParcelStatus.DELIVERED)
            ){
            throw InvalidParcelStateException(
                "Cannot change parcel status from $parcelStatus to $newParcelStatus."
            )
        }

        parcelStatus = newParcelStatus
    }

    fun setDeliveredAt(deliveredAt: LocalDateTime) {
        this.deliveredAt = deliveredAt
    }

    init {
        require(Validator.isValidName(contactName)) { "Contact name must contain minimum 3 characters. Contact name cannot be blank." }

        require(Validator.isValidPhone(contactPhone)) { "Invalid contact phone number format." }

        require(weightKg in MIN_WEIGHT_KG..MAX_WEIGHT_KG) {
            "Parcel weight must be between $MIN_WEIGHT_KG kg and $MAX_WEIGHT_KG kg."
        }
    }

    override fun toString(): String {
        val contactLabel = if (modeOfParcel == ParcelMode.SEND) "Recipient       " else "Pickup Contact  "

        return """
            Type             : $modeOfParcel
            Pickup Location  : $pickupLocation
            Drop Location    : $dropLocation
            $contactLabel : $contactName ($contactPhone)
            Weight           : $weightKg kg
            Category         : $category
            Fare             : ₹$fare
            Status           : $parcelStatus
            Booked At        : ${bookedAt.toDisplayString()}
            Delivered At     : ${deliveredAt?.toDisplayString() ?: "-"}
            Cancelled At     : ${cancelledAt?.toDisplayString() ?: "-"}
        """.trimIndent()
    }

}