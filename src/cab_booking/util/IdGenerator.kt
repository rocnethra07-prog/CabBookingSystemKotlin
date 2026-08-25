package cab_booking.util

object IdGenerator{
    private var userCounter = 1
    private var bookingCounter = 1
    private var vehicleCounter = 1


    fun generateUserId() = "USR-${userCounter++}"

    fun generateBookingId() = "BKG-${bookingCounter++}"

    fun generateVehicleId() = "VEH-${vehicleCounter++}"

    fun syncUserCounter(savedIds: List<String>) {
        userCounter = counterAfter(userCounter, savedIds)
    }

    fun syncBookingCounter(savedIds: List<String>) {
        bookingCounter = counterAfter(bookingCounter, savedIds)
    }

    fun syncVehicleCounter(savedIds: List<String>) {
        vehicleCounter = counterAfter(vehicleCounter, savedIds)
    }

    private fun counterAfter(currentCounter: Int, savedIds: List<String>): Int {
        val highestSaved = savedIds
            .mapNotNull { it.substringAfter("-").toIntOrNull() }
            .maxOrNull()
            ?: 0

        return if (highestSaved + 1 > currentCounter) highestSaved + 1 else currentCounter
    }
}
