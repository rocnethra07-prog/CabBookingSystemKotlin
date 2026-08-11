package cab_booking.model

import cab_booking.model.types.CabType
import cab_booking.util.IdGenerator
import cab_booking.util.Validator

//fields are readable globally and unmodifiable
data class Cab(
    val registrationNumber: String,
    val model: String,
    val cabType: CabType
) {

    val cabId: String = IdGenerator.generateCabId()

    init {
        require(Validator.isValidRegistrationNumber(registrationNumber)) { "Invalid registration number. Format: TN01AB0001." }
        require(model.isNotBlank()) { "Car model cannot be blank." }
    }
}