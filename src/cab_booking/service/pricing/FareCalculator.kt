package cab_booking.service.pricing

import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.util.DistanceMatrix
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalTime

object FareCalculator {

    private val surgeMultiple : BigDecimal = BigDecimal("1.5")

    fun calculateFare(
        cabType: CabType, pickUpLocation: Location, dropLocation: Location, bookedAt: LocalTime = LocalTime.now()
    ): BigDecimal {
        val distanceKms = DistanceMatrix.getDistanceKm(pickUpLocation, dropLocation)
        val distanceFareWithBasePay = cabType.perKmRate * BigDecimal(distanceKms) + cabType.basePay
        val totalFare = if(isSurgeHour(bookedAt)){
            distanceFareWithBasePay * surgeMultiple
        }
        else {
            distanceFareWithBasePay
        }
        return totalFare.setScale(2, RoundingMode.HALF_UP)
    }

    private fun isSurgeHour(time: LocalTime): Boolean {
        val hour = time.hour
        return hour in 8..10 || hour in 18..20  //morning 8-10, evening 6-8
    }

}