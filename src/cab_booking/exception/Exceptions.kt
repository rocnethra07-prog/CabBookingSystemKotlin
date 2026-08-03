package cab_booking.exception

class UserNotFoundException(message: String) : RuntimeException(message)

class DriverNotFoundException(message: String) : RuntimeException(message)

class CabNotFoundException(message: String) : RuntimeException(message)

class AuthenticationException(message: String) : RuntimeException(message)

class DriverUnavailableException(message: String) : RuntimeException(message)

class InvalidRideStateException(message: String) : RuntimeException(message)

class UnauthorizedRideActionException(message: String) : RuntimeException(message)

class InvalidCredentialsException(message: String) : RuntimeException(message)

class DistanceNotFoundException(message: String) : RuntimeException(message)