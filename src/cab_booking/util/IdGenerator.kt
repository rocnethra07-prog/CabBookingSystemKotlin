package cab_booking.util

object IdGenerator{
    private var userId = 1
    private var rideId = 1
    private var parcelId = 1
    private var vehicleId = 1


    fun generateUserId() = "USR-${userId++}"

    fun generateRideId() = "RID-${rideId++}"

    fun generateParcelId() = "PCL-${parcelId++}"

    fun generateVehicleId() = "VEH-${vehicleId++}"
}