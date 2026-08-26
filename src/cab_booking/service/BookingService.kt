package cab_booking.service

import cab_booking.exception.InvalidBookingStateException
import cab_booking.model.Booking
import cab_booking.model.types.BookingStatus
import java.time.LocalDateTime

object BookingService {

    fun start(booking: Booking, action: String) {
        if (booking.status != BookingStatus.BOOKED) {
            throw InvalidBookingStateException(
                "This booking cannot be $action because it is no longer awaiting pickup."
            )
        }

        booking.updateStatus(BookingStatus.STARTED)
    }

    fun complete(booking: Booking, action: String) {
        if (booking.status != BookingStatus.STARTED) {
            throw InvalidBookingStateException(
                "This booking cannot be $action because it has not started yet."
            )
        }

        booking.updateStatus(BookingStatus.COMPLETED)
        booking.setCompletedAt(LocalDateTime.now())
    }

    fun cancel(booking: Booking, item: String) {
        if (booking.status != BookingStatus.BOOKED) {
            throw InvalidBookingStateException(
                "This $item cannot be cancelled because it is no longer active."
            )
        }

        booking.updateStatus(BookingStatus.CANCELLED)
        booking.setCancelledAt(LocalDateTime.now())
    }
}