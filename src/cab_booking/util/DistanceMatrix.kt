package cab_booking.util

import cab_booking.exception.CabBookingException
import cab_booking.model.types.Location

// Distances (in km) are approximate, fixed values for this project - not live geolocation.
// Key is an unordered Set<Location>, so distance(A, B) and distance(B, A) share one entry.
object DistanceMatrix {

    private val distances: Map<Set<Location>, Double> = mapOf(
        setOf(Location.ANNANAGAR, Location.TNAGAR) to 5.0,
        setOf(Location.ANNANAGAR, Location.MAMBALAM) to 4.0,
        setOf(Location.ANNANAGAR, Location.PORUR) to 5.0,
        setOf(Location.ANNANAGAR, Location.GUINDY) to 7.0,
        setOf(Location.ANNANAGAR, Location.MEENAMBAKKAM) to 10.0,
        setOf(Location.ANNANAGAR, Location.TAMBARAM) to 17.0,
        setOf(Location.ANNANAGAR, Location.URAPPAKKAM) to 23.0,
        setOf(Location.ANNANAGAR, Location.POTHERI) to 28.0,
        setOf(Location.ANNANAGAR, Location.GUDUVANCHERY) to 31.0,

        setOf(Location.TNAGAR, Location.MAMBALAM) to 1.0,
        setOf(Location.TNAGAR, Location.PORUR) to 5.0,
        setOf(Location.TNAGAR, Location.GUINDY) to 2.0,
        setOf(Location.TNAGAR, Location.MEENAMBAKKAM) to 5.0,
        setOf(Location.TNAGAR, Location.TAMBARAM) to 12.0,
        setOf(Location.TNAGAR, Location.URAPPAKKAM) to 18.0,
        setOf(Location.TNAGAR, Location.POTHERI) to 23.0,
        setOf(Location.TNAGAR, Location.GUDUVANCHERY) to 26.0,

        setOf(Location.MAMBALAM, Location.PORUR) to 4.0,
        setOf(Location.MAMBALAM, Location.GUINDY) to 3.0,
        setOf(Location.MAMBALAM, Location.MEENAMBAKKAM) to 6.0,
        setOf(Location.MAMBALAM, Location.TAMBARAM) to 13.0,
        setOf(Location.MAMBALAM, Location.URAPPAKKAM) to 19.0,
        setOf(Location.MAMBALAM, Location.POTHERI) to 24.0,
        setOf(Location.MAMBALAM, Location.GUDUVANCHERY) to 27.0,

        setOf(Location.PORUR, Location.GUINDY) to 6.0,
        setOf(Location.PORUR, Location.MEENAMBAKKAM) to 8.0,
        setOf(Location.PORUR, Location.TAMBARAM) to 14.0,
        setOf(Location.PORUR, Location.URAPPAKKAM) to 19.0,
        setOf(Location.PORUR, Location.POTHERI) to 24.0,
        setOf(Location.PORUR, Location.GUDUVANCHERY) to 27.0,

        setOf(Location.GUINDY, Location.MEENAMBAKKAM) to 3.0,
        setOf(Location.GUINDY, Location.TAMBARAM) to 10.0,
        setOf(Location.GUINDY, Location.URAPPAKKAM) to 16.0,
        setOf(Location.GUINDY, Location.POTHERI) to 21.0,
        setOf(Location.GUINDY, Location.GUDUVANCHERY) to 24.0,

        setOf(Location.MEENAMBAKKAM, Location.TAMBARAM) to 7.0,
        setOf(Location.MEENAMBAKKAM, Location.URAPPAKKAM) to 13.0,
        setOf(Location.MEENAMBAKKAM, Location.POTHERI) to 18.0,
        setOf(Location.MEENAMBAKKAM, Location.GUDUVANCHERY) to 21.0,

        setOf(Location.TAMBARAM, Location.URAPPAKKAM) to 6.0,
        setOf(Location.TAMBARAM, Location.POTHERI) to 11.0,
        setOf(Location.TAMBARAM, Location.GUDUVANCHERY) to 14.0,

        setOf(Location.URAPPAKKAM, Location.POTHERI) to 5.0,
        setOf(Location.URAPPAKKAM, Location.GUDUVANCHERY) to 8.0,

        setOf(Location.POTHERI, Location.GUDUVANCHERY) to 3.0
    )

    fun getDistanceKm(a: Location, b: Location): Double {
        if (a == b) {
            throw CabBookingException("Pickup and drop locations cannot be the same.")
        }

        return distances[setOf(a, b)]
            ?: throw CabBookingException("No distance data available between $a and $b.")
    }

}