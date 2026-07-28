package cab_booking.util

object IdGenerator{
    private var userId = 1
    private var cabId = 1
    private var rideId = 1

    fun generateUserId() = "USR-${userId++}"

    fun generateCabId() = "CAB-${cabId++}"

    fun generateRideId() = "RID-${rideId++}"

}