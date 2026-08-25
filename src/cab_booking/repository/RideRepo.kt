package cab_booking.repository

import cab_booking.exception.ActiveRideNotFoundException
import cab_booking.exception.CompletedRideNotFoundException
import cab_booking.model.Ride
import cab_booking.model.types.DispatchStatus

object RideRepo : InMemoryRepo<Ride>() {

    override fun getKey(entity: Ride): String = entity.dispatchId

    //returns list of rides based on the predicate passed
    private fun findRides(predicate: (Ride) -> Boolean ) : List<Ride> =
        storage.values.filter(predicate)

    private val activeStatuses = setOf(DispatchStatus.BOOKED, DispatchStatus.STARTED)

    fun findCurrentRideOfDriver(driverId: String): Ride =
        findRide{ it.driverId == driverId && it.status in activeStatuses }  ?: throw ActiveRideNotFoundException()

    fun findCurrentRideOfRider(riderId: String): Ride =
        findRide { it.customerId == riderId && it.status in activeStatuses } ?: throw ActiveRideNotFoundException()

    fun hasActiveRideOfRider(riderId: String): Boolean =
        storage.values.any {
            it.customerId == riderId && it.status in activeStatuses
        }

    fun hasActiveRideOfDriver(driverId: String): Boolean =
        storage.values.any {
            it.driverId == driverId && it.status in activeStatuses
        }

    private fun findRide(predicate : (Ride) -> Boolean) =
        storage.values.find(predicate)

    fun findLastCompletedRide(riderId: String): Ride =
        findRides{
                it.customerId == riderId && it.status == DispatchStatus.COMPLETED
            }
            .maxByOrNull {
                it.completedAt!!
            }
            ?: throw CompletedRideNotFoundException()
}