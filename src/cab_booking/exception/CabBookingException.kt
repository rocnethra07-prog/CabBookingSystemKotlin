package cab_booking.exception

open class CabBookingException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : CabBookingException(message)

class DriverNotFoundException(message: String) : CabBookingException(message)

class CabNotFoundException(message: String) : CabBookingException(message)

class AuthenticationException(message: String) : CabBookingException(message)

class DriverUnavailableException(message: String) : CabBookingException(message)

class InvalidRideStateException(message: String) : CabBookingException(message)

class UnauthorizedRideActionException(message: String) : CabBookingException(message)

class EmailAlreadyRegisteredException(message: String) : CabBookingException(message)

class InvalidCredentialsException(message: String) : CabBookingException(message)