package cab_booking.model

import cab_booking.model.types.UserRole
import cab_booking.util.IdGenerator
import cab_booking.util.Validator

open class User(
    name: String,
    phone: String,
    email: String,
    val userRole: UserRole
) {
    // Public properties are used for simple property access (user.name, user.phone)
    val userId: String = IdGenerator.generateUserId()

    var name: String = name.trim()
        private set

    var phone: String = phone.trim()
        private set

    fun updateName(name: String){
        require(Validator.isValidName(name)){ "Name must contain minimum 3 characters. Name cannot be blank" }
        this.name = name.trim()
    }

    fun updatePhone(phone: String){
        require(Validator.isValidPhone(phone)){ "Invalid phone number format. Phone cannot be blank" }
        this.phone = phone.trim()
    }

    val email: String = email.trim().lowercase()

    init {
        require(Validator.isValidName(name)){ "Name must contain minimum 3 characters. Name cannot be blank" }
        require(Validator.isValidPhone(phone)){ "Invalid phone number format. Phone cannot be blank" }
        require(Validator.isValidEmail(email)){ "Invalid email format. Email cannot be blank" }
    }

    override fun toString(): String {
        return """
            User ID          : $userId
            Name             : $name
            Phone            : $phone
            Email            : $email
            Role             : $userRole
        """.trimIndent()
    }
}