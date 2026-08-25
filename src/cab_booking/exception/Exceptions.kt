package cab_booking.exception

import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory

class UserNotFoundException(message: String) :
    Exception(message)

class CredentialsNotFoundException() :
    Exception("No login credentials found.")

class InvalidCredentialsException() :
    Exception("Incorrect email or password.")

class AccountLockedException(minutesLeft: Long, secondsLeft: Long) :
    Exception("Account Locked. Please try again in ~${minutesLeft}m ${secondsLeft}s")

class DriverNotFoundException(message: String) :
    Exception(message)

class AvailableDriversNotFoundException(vehicleCategory: VehicleCategory) :
    Exception("No $vehicleCategory drivers are available right now. Please try again in a few minutes.")

class VehicleNotFoundException(vehicleId: String) :
    Exception("We couldn't find a vehicle with ID '$vehicleId'.")

// The status rule is shared by rides and deliveries, so there is one exception.
class InvalidBookingStateException(message: String) :
    Exception(message)

// The ownership rule is the same, but the two screens word it differently.
class UnauthorizedRideActionException(message: String) :
    Exception(message)

class UnauthorizedParcelActionException(message: String) :
    Exception(message)

class ActiveRideNotFoundException() :
    Exception("You don't have any active ride right now.")

class ActiveParcelNotFoundException() :
    Exception("You don't have any active parcel delivery right now.")

class CompletedRideNotFoundException() :
    Exception("You don't have any completed rides yet.")

class DistanceNotFoundException(pickupLocation: Location, dropLocation: Location) :
    Exception("Sorry, we don't service this route yet ($pickupLocation → $dropLocation).")

// Thrown when the user types 'cancel' at any prompt.
class OperationCancelledException() :
    Exception("Operation cancelled.")
