package cab_booking.util

object IdGenerator{
    private var userCounter = 1
    private var dispatchCounter = 1
    private var vehicleCounter = 1


    fun generateUserId() = "USR-${userCounter++}"

    // Rides and parcel deliveries are both Dispatches, so they share one counter
    // and one ID space. Every dispatch ID in the system is unique on its own.
    fun generateDispatchId() = "DSP-${dispatchCounter++}"

    fun generateVehicleId() = "VEH-${vehicleCounter++}"


    // The counters start at 1 every time the app starts, so without these the second
    // run would hand out USR-1 again and overwrite the admin. After the saved data is
    // loaded, each counter is moved past the highest ID that came back from the files.

    fun syncUserCounter(savedIds: List<String>) {
        userCounter = counterAfter(userCounter, savedIds)
    }

    fun syncDispatchCounter(savedIds: List<String>) {
        dispatchCounter = counterAfter(dispatchCounter, savedIds)
    }

    fun syncVehicleCounter(savedIds: List<String>) {
        vehicleCounter = counterAfter(vehicleCounter, savedIds)
    }

    // "USR-7" -> 7, so the next generated ID is USR-8.
    // A counter is only ever moved forward, never backwards.
    private fun counterAfter(currentCounter: Int, savedIds: List<String>): Int {
        val highestSaved = savedIds
            .mapNotNull { it.substringAfter("-").toIntOrNull() }
            .maxOrNull()
            ?: 0

        return if (highestSaved + 1 > currentCounter) highestSaved + 1 else currentCounter
    }
}
