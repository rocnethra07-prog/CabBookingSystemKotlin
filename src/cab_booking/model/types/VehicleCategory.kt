package cab_booking.model.types

import java.math.BigDecimal

// Broad class of vehicle. Kept separate from VehicleCategory because it's
// useful for grouping/filtering (e.g. "show me all two-wheeler drivers")
// without caring about the specific fare rule.
enum class VehicleType{BIKE, AUTO, CAR}

enum class VehicleCategory(
    val vehicleType: VehicleType,
    val basePrice: BigDecimal,
    val perKmRate: BigDecimal,
    val maxParcelWeightKg: BigDecimal
) {
    BIKE(
        VehicleType.BIKE,
        BigDecimal("15"),
        BigDecimal("4.5"),
        BigDecimal("5")
    ),

    AUTO(
        VehicleType.AUTO,
        BigDecimal("30"),
        BigDecimal("9"),
        BigDecimal("20")
    ),

    MINI(
        VehicleType.CAR,
        BigDecimal("40"),
        BigDecimal("8"),
        BigDecimal("40")
    ),

    SEDAN(
        VehicleType.CAR,
        BigDecimal("60"),
        BigDecimal("11"),
        BigDecimal("60")
    ),

    SUV(
        VehicleType.CAR,
        BigDecimal("80"),
        BigDecimal("15"),
        BigDecimal("100")
    )

}