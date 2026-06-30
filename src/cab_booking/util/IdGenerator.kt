package cab_booking.util

object IdGenerator{
    private var userId = 1
    private var cabId = 1
    private var rideId = 1

    fun generateUserId() : String{
        return "USR-" + userId++
    }

    fun generateCabId() : String{
        return "CAB" + cabId++
    }

    fun generateRideId() : String{
        return "RID" + rideId++
    }
}