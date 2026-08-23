package cab_booking.repository

import cab_booking.exception.ActiveRideNotFoundException
import cab_booking.exception.CompletedRideNotFoundException
import cab_booking.model.Ride
import cab_booking.model.types.RideStatus

object RideRepo : InMemoryRepo<Ride>() {

    override fun getKey(entity: Ride): String = entity.rideId

    //returns list of rides based on the predicate passed
    private fun findRides(predicate: (Ride) -> Boolean ) : List<Ride> =
        storage.values.filter(predicate)

    private val activeStatuses = setOf(RideStatus.BOOKED, RideStatus.STARTED)

    fun findCurrentRideOfDriver(driverId: String): Ride =
        findRide{ it.driverId == driverId && it.rideStatus in activeStatuses }  ?: throw ActiveRideNotFoundException()

    fun findCurrentRideOfRider(riderId: String): Ride =
        findRide { it.customerId == riderId && it.rideStatus in activeStatuses } ?: throw ActiveRideNotFoundException()

    fun hasCurrentRideOfRider(riderId: String): Boolean =
        storage.values.any {
            it.customerId == riderId && it.rideStatus in activeStatuses
        }

    fun hasCurrentRideOfDriver(driverId: String): Boolean =
        storage.values.any {
            it.driverId == driverId && it.rideStatus in activeStatuses
        }

    private fun findRide(predicate : (Ride) -> Boolean) =
        storage.values.find(predicate)

    fun findLastCompletedRide(riderId: String): Ride =
        findRides{
                it.customerId == riderId && it.rideStatus == RideStatus.COMPLETED
            }
            .maxByOrNull {
                it.completedAt!!
            }
            ?: throw CompletedRideNotFoundException()
}