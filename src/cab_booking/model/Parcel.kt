package cab_booking.model

import cab_booking.model.types.ParcelCategory
import cab_booking.util.IdGenerator
import java.math.BigDecimal

class Parcel(
    val weightKg: BigDecimal,
    val parcelCategory: ParcelCategory
){

    companion object {
        val MIN_WEIGHT_KG: BigDecimal = BigDecimal("0.1")
        val MAX_WEIGHT_KG: BigDecimal = BigDecimal("100.0")
    }

    val parcelId: String = IdGenerator.generateParcelId()

    init {
        require(weightKg in MIN_WEIGHT_KG..MAX_WEIGHT_KG) {
            "Parcel weight must be between $MIN_WEIGHT_KG kg and $MAX_WEIGHT_KG kg."
        }
    }

    override fun toString(): String {

        return """
            Weight           : $weightKg kg
            Category         : $parcelCategory
        """.trimIndent()
    }

}