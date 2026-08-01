package cab_booking.model.types

import java.math.BigDecimal

enum class CabType(val basePay: BigDecimal, val perKmRate: BigDecimal) {
    MINI(BigDecimal(40), BigDecimal(8)),

    SEDAN(BigDecimal(60), BigDecimal(11)),

    SUV(BigDecimal(80), BigDecimal("15.0"))
}