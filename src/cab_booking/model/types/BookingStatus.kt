package cab_booking.model.types

// The same four states describe a passenger ride and a delivery ride.
enum class BookingStatus {
    BOOKED,
    STARTED,
    COMPLETED,
    CANCELLED
}