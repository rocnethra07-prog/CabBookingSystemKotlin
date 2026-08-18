package cab_booking.service.result

import cab_booking.model.Ride

sealed class BookingResult{

    object DriverUnavailable : BookingResult()

    data class Success(val ride: Ride) : BookingResult()

}