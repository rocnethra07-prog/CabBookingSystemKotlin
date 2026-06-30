package cab_booking.model

import cab_booking.util.IdGenerator

data class Cab(
    val cabId: String = IdGenerator.generateCabId(),
    val registrationNumber: String,
    val model: String,
    val cabType: CabType,
    val driverId : String
) {

    init {
        require(registrationNumber.isNotBlank()) {
            "Registration number cannot be blank."
        }

        require(model.isNotBlank()) {
            "Car model cannot be blank."
        }

        require(driverId.isNotBlank()){
            "Driver Id cannot be blank"
        }
    }

    override fun toString(): String {
        return """
            Cab ID           : $cabId
            Model            : $model
            Type             : $cabType
            Registration No. : $registrationNumber
            Driver ID        : $driverId
        """.trimIndent()
    }
}