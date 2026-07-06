package cab_booking.model

import cab_booking.exception.CabBookingException
import cab_booking.model.types.CabType
import cab_booking.util.IdGenerator

data class Cab(
    val registrationNumber: String,
    val model: String,
    val cabType: CabType
) {

    val cabId: String = IdGenerator.generateCabId()

    init {
        if(registrationNumber.isBlank()) {
            throw CabBookingException("Registration number cannot be blank.")
        }
        if(model.isBlank()) {
            throw CabBookingException("Car model cannot be blank.")
        }
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