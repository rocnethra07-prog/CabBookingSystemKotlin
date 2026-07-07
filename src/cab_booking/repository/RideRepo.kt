package cab_booking.repository

import cab_booking.model.Ride
import cab_booking.model.types.RideStatus

object RideRepo : InMemoryRepo<Ride>() {

    override fun getKey(entity: Ride): String = entity.rideId

    fun findRidesByRider(riderId: String): List<Ride> =
        storage.values.filter {
            it.riderId == riderId
        }

    fun findRidesByDriver(driverId: String): List<Ride> =
        storage.values.filter {
            it.driverId == driverId
        }

    fun findCurrentRideOfDriver(driverId: String): Ride? =
        storage.values.find {
            it.driverId == driverId &&
                    it.rideStatus == RideStatus.BOOKED
        }

    fun findCurrentRideOfRider(riderId: String): Ride? =
        storage.values.find {
            it.riderId == riderId &&
                    it.rideStatus == RideStatus.BOOKED
        }

    fun findRidesByStatus(status: RideStatus): List<Ride> =
        storage.values.filter {
            it.rideStatus == status
        }

    fun findLastCompletedRide(riderId: String): Ride? =
        storage.values
            .filter {
                it.riderId == riderId && it.rideStatus == RideStatus.COMPLETED
            }
            .maxByOrNull {
                it.bookedAt
            }
}