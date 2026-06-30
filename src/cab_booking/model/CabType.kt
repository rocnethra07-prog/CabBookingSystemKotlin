package cab_booking.model

import java.math.BigDecimal

enum class CabType(val basePay: BigDecimal) {
    MINI(BigDecimal(120)),

    SEDAN(BigDecimal(160)),

    SUV(BigDecimal(200))
}