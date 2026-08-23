package cab_booking.model.types
// SEND    -> customer is at the pickup location, sending a parcel to someone
//            else waiting at the drop location.
// RECEIVE -> customer is at the drop location; the parcel is picked up from
//            someone else at the pickup location and brought to the customer.
// The booking mechanics (pickup, drop, fare, driver assignment) are identical
// either way - only who the "other party" at the far end is, and which label
// makes sense in the UI, changes.
enum class ParcelMode {
    SEND,
    RECEIVE
}