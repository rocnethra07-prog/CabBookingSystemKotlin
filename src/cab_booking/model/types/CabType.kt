package cab_booking.model.types

import java.math.BigDecimal
//basePay: flat charge just for booking the cab, before distance is factored in
//perKmRate: cost per km, differs by cab type since a bigger cab costs more to run
enum class CabType(val basePay: BigDecimal, val perKmRate: BigDecimal) {
    MINI(BigDecimal(40), BigDecimal("8.0")),

    SEDAN(BigDecimal(60), BigDecimal("11.0")),

    SUV(BigDecimal(80), BigDecimal("15.0"));
}