package cab_booking.model.types

import java.math.BigDecimal

//Just for now, the fare is constant base pay based on the cab type (simple)
//have to implement fare calculation
enum class CabType(val basePay: BigDecimal) {
    MINI(BigDecimal(120)),

    SEDAN(BigDecimal(160)),

    SUV(BigDecimal(200))
}