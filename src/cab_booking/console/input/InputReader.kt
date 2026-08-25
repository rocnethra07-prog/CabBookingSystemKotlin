package cab_booking.console.input

import cab_booking.exception.OperationCancelledException
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.util.Validator
import java.math.BigDecimal

object InputReader {

    // Typing this word at any prompt cancels whatever is being filled in and
    // hands control back to the screen that started it, instead of forcing
    // the person to keep answering until it's valid.
    private const val CANCEL_KEYWORD = "cancel"

    // Every prompt reads through here so the cancel word works everywhere at once.
    private fun readLine(prompt: String): String {
        print(prompt)
        val input = readln().trim()
        if (input.equals(CANCEL_KEYWORD, ignoreCase = true)) {
            throw OperationCancelledException()
        }
        return input
    }

    private fun promptUntilValidInput(prompt: String, errorMessage: String, validator: (String) -> Boolean) : String{
        while (true) {
            val input = readLine(prompt)
            if(validator(input)){
                return input
            }
            println("[x] $errorMessage\n")
        }
    }

    fun promptNonEmptyInput(prompt: String, errorMessage: String) =
        promptUntilValidInput(prompt, errorMessage){ it.isNotBlank() }


    fun promptName(
        prompt: String = "Enter Name (or 'cancel' to go back): ",
        errorMessage: String = "Name must contain minimum 3 characters. Please try again"
    ) =
        promptUntilValidInput(prompt, errorMessage ) { Validator.isValidName(it) }


    fun promptPhoneNumber(
        prompt: String = "Enter Phone (or 'cancel' to go back): ",
        errorMessage: String = "Invalid Phone Number. Please enter a valid 10 digit number"
    ) =
        promptUntilValidInput(prompt, errorMessage) { Validator.isValidPhoneNumber(it) }


    fun promptEmail(
        prompt: String = "Enter email (or 'cancel' to go back): ",
        errorMessage: String = "Invalid email format. Please enter a valid email"
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidEmail(it) }


    fun promptPassword(
        prompt: String = "Enter password (or 'cancel' to go back): ",
        errorMessage: String = "Password must be at least 8 characters, with an uppercase letter, a lowercase letter, and a special character (@#$%^&+=!-_). Spaces are not allowed."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidPassword(it) }


    fun promptLicenseNumber(
        prompt: String = "License Number (or 'cancel' to go back): ",
        errorMessage: String = "Invalid license number. Format: TN012023001234."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidLicenseNumber(it) }.uppercase()

    fun promptRegistrationNumber(
        prompt: String = "Registration Number (or 'cancel' to go back): ",
        errorMessage: String =  "Invalid registration number. Format: TN01AB0001."
    ) =
        promptUntilValidInput(prompt, errorMessage) { Validator.isValidRegistrationNumber(it) }.uppercase()


    fun chooseLocation(prompt: String = "Select Location: "): Location {

        while (true) {

            println(prompt)

            Location.entries.forEachIndexed { index, location ->
                println("${index + 1}. $location")
            }

            val choice = readLine("Choose (or 'cancel' to go back): ").toIntOrNull()

            if (choice == null || choice !in 1..Location.entries.size) {
                println("[x] Invalid choice. Please try again.")
                continue
            }

            return Location.entries[choice - 1]
        }
    }


    fun promptConfirmation(prompt: String): Boolean {

        while (true) {
            when (readLine("$prompt (Y/N or 'cancel' to go back) : ").trim().uppercase()) {
                "Y", "YES" -> return true
                "N", "NO" -> return false
                else -> println("[x] Please enter Y or N.")
            }
        }
    }

    fun promptOptionalName(currentValue: String): String {

        while (true) {
            print("Name [$currentValue] (Press Enter to keep the current value,  or 'cancel' to go back) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (!Validator.isValidName(input)) {
                println("Invalid name.")
                continue
            }
            return input
        }
    }

    fun promptOptionalPhone(currentValue: String): String {

        while (true) {
            print("Phone [$currentValue] (Press Enter to keep the current value,  or 'cancel' to go back) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (!Validator.isValidPhoneNumber(input)) {
                println("Invalid phone number.")
                continue
            }
            return input

        }
    }

    fun chooseVehicleCategory(prompt: String = "Select Vehicle Type"): VehicleCategory {
        while (true) {
            println("\n$prompt")
            ConsoleFormatter.divider()
            VehicleCategory.entries.forEachIndexed { index, category ->
                println(
                    "  ${index + 1}. $category  " +
                            "Base ₹${category.basePrice}  +  ₹${category.perKmRate}/km"
                )
            }
            ConsoleFormatter.divider()

            val choice = readLine("$prompt (or 'cancel' to go back): ").toIntOrNull()

            if (choice == null || choice !in 1..VehicleCategory.entries.size) {
                println("[x] Invalid choice. Please try again.")
                continue
            }
            return VehicleCategory.entries[choice - 1]
        }
    }

    // showWeightLimit is switched on for parcel deliveries, where the customer is
    // choosing a vehicle by how much it can carry rather than by comfort.
    fun chooseVehicleCategoryByFare(
        fareEstimates: Map<VehicleCategory, BigDecimal>,
        prompt: String = "Choose a ride",
        showWeightLimit: Boolean = false
    ): VehicleCategory {
        val categories = fareEstimates.keys.toList()

        while (true) {
            println("\n$prompt")
            ConsoleFormatter.divider()
            categories.forEachIndexed { index, category ->
                val fare = fareEstimates.getValue(category)
                if (showWeightLimit) {
                    println("  ${index + 1}. ${category.name.padEnd(6)} up to ${category.maxParcelWeightKg} kg   ₹$fare")
                }
                else {
                    println("  ${index + 1}. $category  ₹$fare")
                }
            }
            ConsoleFormatter.divider()

            val choice = readLine("Choose (or 'cancel' to go back): ").toIntOrNull()

            if (choice == null || choice !in 1..categories.size) {
                println("[x] Invalid choice. Please try again.")
                continue
            }
            return categories[choice - 1]
        }
    }

    fun promptRating(prompt: String = "Enter Rating (1-5), or 'cancel' to skip: "): Int {
        while (true) {
            val value = readLine(prompt).toIntOrNull()

            if (value == null || value !in 1..5) {
                println("[x] Please enter a rating between 1 and 5.")
                continue
            }
            return value
        }
    }

}