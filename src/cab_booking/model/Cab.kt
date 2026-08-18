package cab_booking.model

import cab_booking.model.types.CabType
import cab_booking.util.IdGenerator
import cab_booking.util.Validator

//fields are readable globally and unmodifiable
class Cab(
    val model: String,
    val cabType: CabType,
    val registrationNumber: String
) {

    val cabId: String = IdGenerator.generateCabId()

    init {
        require(Validator.isValidRegistrationNumber(registrationNumber)) { "Invalid registration number. Format: TN01AB0001." }
        require(model.isNotBlank()) { "Car model cannot be blank." }
    }

    override fun toString(): String {
        return """
            Model        : $model
            Cab Type     : $cabType
            Base Price   : ${cabType.basePay}
            Per Km Price : ${cabType.perKmRate}
        """.trimIndent()
    }
}