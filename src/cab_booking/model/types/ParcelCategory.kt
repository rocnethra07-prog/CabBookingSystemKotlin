package cab_booking.model.types

import java.math.BigDecimal


enum class ParcelCategory(val handlingSurcharge: BigDecimal) {
    DOCUMENTS(BigDecimal("0")),       // No extra charge
    CLOTHES(BigDecimal("0")),         // No extra charge
    FOOD(BigDecimal("15")),           // ₹15 extra for fast/priority handling
    ELECTRONICS(BigDecimal("25")),    // ₹25 extra for high-value insurance/care
    FRAGILE(BigDecimal("30")),        // ₹30 extra for slow, careful driving
    OTHER(BigDecimal("20"))           // Default for uncategorized parcels
}