package cab_booking.repository

import cab_booking.exception.ActiveRideNotFoundException
import cab_booking.exception.CompletedRideNotFoundException
import cab_booking.model.Ride
import cab_booking.model.types.BookingStatus

object RideRepo : InMemoryRepo<Ride>() {

    override fun getKey(entity: Ride): String = entity.bookingId

    //returns list of rides based on the predicate passed
    private fun findRides(predicate: (Ride) -> Boolean ) : List<Ride> =
        storage.values.filter(predicate)

    private val activeStatuses = setOf(BookingStatus.BOOKED, BookingStatus.STARTED)

    fun findCurrentRideOfDriver(driverId: String): Ride =
        findRide{ it.driverId == driverId && it.status in activeStatuses }  ?: throw ActiveRideNotFoundException()

    fun findCurrentRideOfCustomer(customerId: String): Ride =
        findRide { it.customerId == customerId && it.status in activeStatuses } ?: throw ActiveRideNotFoundException()

    fun hasActiveRideOfCustomer(customerId: String): Boolean =
        storage.values.any {
            it.customerId == customerId && it.status in activeStatuses
        }

    fun hasActiveRideOfDriver(driverId: String): Boolean =
        storage.values.any {
            it.driverId == driverId && it.status in activeStatuses
        }

    private fun findRide(predicate : (Ride) -> Boolean) =
        storage.values.find(predicate)

    fun findLastCompletedRideOfCustomer(customerId: String): Ride =
        findRides{
                it.customerId == customerId && it.status == BookingStatus.COMPLETED
            }
            .maxByOrNull {
                it.completedAt!!
            }
            ?: throw CompletedRideNotFoundException()
}