package cab_booking.service.pricing

import cab_booking.model.types.Location
import cab_booking.config.getDistanceKm
import cab_booking.model.types.VehicleCategory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

// Fare calculation logic (distance -> base fare -> surge -> rounding)
// is common to every vehicle category. Only the surge multiplier changes,
// so subclasses override just that instead of duplicating the whole formula.
abstract class FareCalculator {

    protected abstract val surgeMultiplier : BigDecimal

    fun calculateBaseFare(
        vehicleCategory: VehicleCategory, pickUpLocation: Location, dropLocation: Location, bookedAt: LocalDateTime = LocalDateTime.now()
    ): BigDecimal {
        val distanceKms = getDistanceKm(pickUpLocation, dropLocation)
        val fareBeforeSurge = vehicleCategory.perKmRate * BigDecimal(distanceKms) + vehicleCategory.basePrice
        val totalFare = if(isSurgeHour(bookedAt)){
            fareBeforeSurge * surgeMultiplier
        }
        else {
            fareBeforeSurge
        }
        return totalFare.setScale(2, RoundingMode.HALF_UP)
    }

    private fun isSurgeHour(time: LocalDateTime): Boolean {
        val hour = time.hour
        return hour in 8..10 || hour in 18..20  //morning 8-10, evening 6-8
    }

}

object RideFareCalculator : FareCalculator() {
    override val surgeMultiplier = BigDecimal("1.5")
}


object ParcelFareCalculator : FareCalculator() {
    override val surgeMultiplier = BigDecimal("1.1")
}