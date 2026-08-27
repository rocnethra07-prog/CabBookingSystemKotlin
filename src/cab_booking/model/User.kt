package cab_booking.model

import cab_booking.util.IdGenerator
import cab_booking.util.Validator

sealed class User(
    name: String,
    phoneNumber: String,
    val email: String,
    val userId: String = IdGenerator.generateUserId()
) {
    // Public properties are used for simple property access (user.name, user.phone)

    var name: String = name.trim()
        private set

    var phoneNumber: String = phoneNumber.trim()
        private set

    fun updateName(name: String){
        require(Validator.isValidName(name)){ "Invalid name. Name must contain minimum 3 characters." }
        this.name = name.trim()
    }

    fun updatePhoneNumber(phoneNumber: String){
        require(Validator.isValidPhoneNumber(phoneNumber)){ "Invalid phone number format." }
        this.phoneNumber = phoneNumber.trim()
    }

    init {
        require(Validator.isValidName(name)){ "Invalid name. Name must contain minimum 3 characters." }
        require(Validator.isValidPhoneNumber(phoneNumber)){ "Invalid phone number format." }
        require(Validator.isValidEmail(email)){ "Invalid email format." }
    }

    override fun toString(): String {
        return """
            Name             : $name
            Phone            : $phoneNumber
            Email            : $email
        """.trimIndent()
    }
}