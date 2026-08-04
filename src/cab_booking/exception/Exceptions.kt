package cab_booking.exception

import cab_booking.model.types.CabType
import cab_booking.model.types.Location

class UserNotFoundException(message: String) :
    Exception(message)

class DriverNotFoundException(message: String) :
    Exception(message)

class CabNotFoundException(message: String) :
    Exception(message)

class CredentialsNotFoundException() :
    Exception("Authentication details not found.")

class DriverUnavailableException(cabType: CabType) :
    Exception("No $cabType drivers are available right now.")

class InvalidRideStateException(message: String) :
    Exception(message)

class UnauthorizedRideActionException(message: String) :
    Exception(message)

class InvalidCredentialsException() :
    Exception("Invalid Credentials")

class DistanceNotFoundException(location1 : Location, location2 : Location) :
    Exception("Distance not configured between $location1 and $location2")