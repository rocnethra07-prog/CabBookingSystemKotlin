package cab_booking.exception

import cab_booking.model.types.Location

class UserNotFoundException(message: String) :
    Exception(message)

class DriverNotFoundException(message: String) :
    Exception(message)

class CabNotFoundException(message: String) :
    Exception(message)

class CredentialsNotFoundException() :
    Exception("Authentication details not found")

class AccountLockedException(minutesLeft: Long, secondsLeft: Long) :
    Exception("Account Locked. Try again in ~$minutesLeft minute(s) $secondsLeft second(s)")

class InvalidRideStateException(message: String) :
    Exception(message)

class UnauthorizedRideActionException(message: String) :
    Exception(message)

class InvalidCredentialsException() :
    Exception("Invalid Credentials")

class DistanceNotFoundException(location1 : Location, location2 : Location) :
    Exception("Distance not configured between $location1 and $location2")

class CompletedRideNotFoundException() :
     Exception("No completed ride available for rating.")

class ActiveRideNotFoundException() :
     Exception("No active ride found at the moment")

class AvailableDriversNotFoundException(message: String) :
     Exception(message)