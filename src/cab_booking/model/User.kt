package cab_booking.model

import cab_booking.util.IdGenerator
import cab_booking.util.Validator

sealed class User(
    name: String,
    phoneNumber: String,
    val email: String,
    // Left out everywhere in the app, so a new user gets a freshly generated ID.
    // The file storage passes the saved ID so a user keeps the same one across runs.
    val userId: String = IdGenerator.generateUserId()
) {
    // Public properties are used for simple property access (user.name, user.phone)

    var name: String = name.trim()
        private set

    var phoneNumber: String = phoneNumber.trim()
        private set

    fun updateName(name: String){
        require(Validator.isValidName(name)){ "Name must contain minimum 3 characters. Name cannot be blank" }
        this.name = name.trim()
    }

    fun updatePhoneNumber(phoneNumber: String){
        require(Validator.isValidPhoneNumber(phoneNumber)){ "Invalid phone number format. Phone cannot be blank" }
        this.phoneNumber = phoneNumber.trim()
    }

    init {
        require(Validator.isValidName(name)){ "Name must contain minimum 3 characters. Name cannot be blank" }
        require(Validator.isValidPhoneNumber(phoneNumber)){ "Invalid phone number format. Phone cannot be blank" }
        require(Validator.isValidEmail(email)){ "Invalid email format. Email cannot be blank" }
    }

    override fun toString(): String {
        return """
            User ID          : $userId
            Name             : $name
            Phone            : $phoneNumber
            Email            : $email
            Role             : ${this::class.simpleName}
        """.trimIndent()
    }
}