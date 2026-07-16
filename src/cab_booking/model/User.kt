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
        set(value) {
            require(Validator.isValidName(value)){"Name must contain minimum 3 characters. Name cannot be blank"}
            field = value.trim()
        }

    var phone: String = phone.trim()
        set(value) {
            require(Validator.isValidPhone(value)){"Invalid phone number format. Phone cannot be blank"}
            field = value.trim()
        }

    val email: String = email.trim().lowercase()

    init {
        require(Validator.isValidName(name)){"Name must contain minimum 3 characters. Name cannot be blank"}
        require(Validator.isValidPhone(phone)){"Invalid phone number format. Phone cannot be blank"}
        require(Validator.isValidEmail(email)){"Invalid email format. Email cannot be blank"}
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