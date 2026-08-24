package cab_booking.model.types
// SEND    -> customer is at the pickup location, sending a parcel to someone
//            else waiting at the drop location.
// RECEIVE -> customer is at the drop location; the parcel is picked up from
//            someone else at the pickup location and brought to the customer.

enum class ParcelDeliveryType {
    SEND,
    RECEIVE
}