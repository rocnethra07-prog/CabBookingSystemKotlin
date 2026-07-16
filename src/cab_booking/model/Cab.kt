package cab_booking.model

import cab_booking.model.types.CabType
import cab_booking.util.IdGenerator

//fields are readable globally and unmodifiable
data class Cab(
    val registrationNumber: String,
    val model: String,
    val cabType: CabType
) {

    val cabId: String = IdGenerator.generateCabId()

    init {
        require(registrationNumber.isNotBlank()) { "Registration number cannot be blank." }
        require(model.isNotBlank()) { "Car model cannot be blank." }
    }

    override fun toString(): String {
        return """
            Cab ID           : $cabId
            Model            : $model
            Type             : $cabType
            Registration No. : $registrationNumber
        """.trimIndent()
    }
}