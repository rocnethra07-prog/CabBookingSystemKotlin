package cab_booking.model

import cab_booking.exception.CabBookingException
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
            if(!Validator.isValidName(value)){
                throw CabBookingException("Name must contain minimum 3 characters. Name cannot be blank")
            }
            field = value.trim()
        }

    var phone: String = phone.trim()
        set(value) {
            if(!Validator.isValidPhone(value)){
                throw CabBookingException("Invalid phone number format. Phone cannot be blank")
            }
            field = value.trim()
        }

    val email: String = email.trim().lowercase()

    init {
        if(!Validator.isValidName(name)){
            throw CabBookingException("Name must contain minimum 3 characters. Name cannot be blank")
        }
        if(!Validator.isValidPhone(phone)){
           throw CabBookingException("Invalid phone number format. Phone cannot be blank")
        }
        if(!Validator.isValidEmail(email)){
           throw CabBookingException("Invalid email format. Email cannot be blank")
        }
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