package cab_booking.service

import cab_booking.model.User
import cab_booking.repository.UserRepo

object UserService {

    fun isEmailRegistered(email: String) : Boolean =
        UserRepo.existsByEmail(email)

    fun findUserById(userId: String): User =
        UserRepo.findByUserId(userId)

}