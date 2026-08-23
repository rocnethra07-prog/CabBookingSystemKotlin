package cab_booking.model.types

import java.math.BigDecimal

// Broad class of vehicle. Kept separate from VehicleCategory because it's
// useful for grouping/filtering (e.g. "show me all two-wheeler drivers")
// without caring about the specific fare tier.
enum class VehicleType{BIKE, AUTO, CAR}


// The single bookable "product" a customer chooses - exactly what Rapido/Uber/Ola
// call a ride category. Every category, regardless of vehicle type, carries its
// own fare rule (base fare + per-km rate). Bike and Auto simply have one category
// each; Car has three. Nothing about pricing is a special case for any of them.
enum class VehicleCategory(
    val vehicleType: VehicleType,
    val basePrice: BigDecimal,
    val perKmRate: BigDecimal
) {
    BIKE(
        VehicleType.BIKE,
        BigDecimal("15"),
        BigDecimal("4.5")
    ),

    AUTO(
        VehicleType.AUTO,
        BigDecimal("30"),
        BigDecimal("9")
    ),

    MINI(
        VehicleType.CAR,
        BigDecimal("40"),
        BigDecimal("8")
    ),

    SEDAN(
        VehicleType.CAR,
        BigDecimal("60"),
        BigDecimal("11")
    ),

    SUV(
        VehicleType.CAR,
        BigDecimal("80"),
        BigDecimal("15")
    )
}