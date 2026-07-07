package cab_booking.repository

import cab_booking.model.Ride
import cab_booking.model.types.RideStatus

object RideRepo : InMemoryRepo<Ride>() {

    override fun getKey(entity: Ride): String = entity.rideId

    fun findRidesByRider(riderId: String): List<Ride> =
        findRides{ ride -> ride.riderId == riderId}

    fun findRidesByDriver(driverId: String): List<Ride> =
        findRides{ it.driverId == driverId} //it ->

    fun findRidesByStatus(status: RideStatus): List<Ride> =
        findRides { it.rideStatus == status }

    //returns list of rides based on the predicate passed
    private fun findRides(predicate: (Ride) -> Boolean ) : List<Ride> =
        storage.values.filter(predicate)

    fun findCurrentRideOfDriver(driverId: String): Ride? =
        findRide{ it.driverId == driverId && it.rideStatus == RideStatus.BOOKED }

    fun findCurrentRideOfRider(riderId: String): Ride? =
        findRide { it.riderId == riderId && it.rideStatus == RideStatus.BOOKED }

    //returns a ride or null based on the predicate passed
    private fun findRide(predicate : (Ride) -> Boolean) : Ride? =
        storage.values.find(predicate)

    fun findLastCompletedRide(riderId: String): Ride? =
        findRides{
                it.riderId == riderId && it.rideStatus == RideStatus.COMPLETED
            }
            .maxByOrNull {
                it.bookedAt
            }
}