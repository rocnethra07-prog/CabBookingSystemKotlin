package cab_booking.exception

import cab_booking.model.types.Location

class UserNotFoundException(message: String) :
    Exception(message)

class CredentialsNotFoundException() :
    Exception("Authentication details not found")

class InvalidCredentialsException() :
    Exception("Invalid Credentials")

class AccountLockedException(minutesLeft: Long, secondsLeft: Long) :
    Exception("Account Locked. Try again in ~$minutesLeft minute(s) $secondsLeft second(s)")

class DriverNotFoundException(message: String) :
    Exception(message)

class AvailableDriversNotFoundException(message: String) :
    Exception(message)

class VehicleNotFoundException(message: String) :
    Exception(message)

// The status rule is shared by rides and deliveries, so there is one exception.
class InvalidBookingStateException(message: String) :
    Exception(message)

// The ownership rule is the same, but the two screens word it differently.
class UnauthorizedRideActionException(message: String) :
    Exception(message)

class UnauthorizedParcelActionException(message: String) :
    Exception(message)

class ActiveRideNotFoundException() :
    Exception("No active ride found at the moment")

class ActiveParcelNotFoundException() :
    Exception("No active parcel found at the moment")

class CompletedRideNotFoundException() :
    Exception("No completed ride found at the moment")

class DistanceNotFoundException(pickupLocation: Location, dropLocation: Location) :
    Exception("The selected location is outside our service area ($pickupLocation to $dropLocation)")

// Thrown when the user types 'cancel' at any prompt.
class OperationCancelledException() :
    Exception("Cancelled.")
