package cab_booking.service.pricing

import cab_booking.model.types.Location
import cab_booking.config.getDistanceKm
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.VehicleCategory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

abstract class FareCalculator {

    protected abstract val surgeMultiplier : BigDecimal

     protected fun calculateBaseFare(
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

// Same rule for every vehicle category - base fare + distance * per-km rate.
// Works identically for Bike, Auto, Mini, Sedan and SUV because pricing lives
// on VehicleCategory itself, not on some vehicle-specific subclass.
object RideFareCalculator : FareCalculator() {

    override val surgeMultiplier = BigDecimal("1.5")

    fun calculateRideFare(
        vehicleCategory: VehicleCategory,
        pickUpLocation: Location,
        dropLocation: Location,
        bookedAt: LocalDateTime = LocalDateTime.now()
    ) =
        calculateBaseFare(vehicleCategory,pickUpLocation,dropLocation, bookedAt)

}


// Parcel fare uses the same base + per-km rule as a ride on the chosen vehicle
// category, plus a handling surcharge that depends on what's being carried
// (documents/clothes are free to handle, fragile/electronics/food cost extra).
object ParcelFareCalculator : FareCalculator() {

    override val surgeMultiplier = BigDecimal("0.5")

    fun calculateParcelFare(
        vehicleCategory: VehicleCategory,
        pickUpLocation: Location,
        dropLocation: Location,
        bookedAt: LocalDateTime = LocalDateTime.now(),
        parcelCategory: ParcelCategory
    ) =
        calculateBaseFare(vehicleCategory,pickUpLocation,dropLocation, bookedAt) + parcelCategory.handlingSurcharge
}