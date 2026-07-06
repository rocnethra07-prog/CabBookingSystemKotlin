package cab_booking.util

import cab_booking.model.types.CabType
import cab_booking.model.types.Location

object InputUtil {
    private fun getValidatedInput(prompt: String, errorMessage: String, validator: (String) -> Boolean) : String{
        while (true){
            print(prompt)
            val input = readln().trim()
            if(validator(input)){
                return input
            }
            print("! $errorMessage !")
        }
    }

    fun getNonEmptyInput(prompt: String, errorMessage: String) : String{
        return getValidatedInput(prompt, errorMessage){ it.isNotBlank()}
    }

    fun getName(
        prompt: String = "Enter Name: ",
        errorMessage: String = "Name must contain minimum 3 characters. Please try again"
    ) : String{
        return getValidatedInput(prompt, errorMessage ) { Validator.isValidName(it) }
    }

    fun getPhone(
        prompt: String = "Enter Phone: ",
        errorMessage: String = "Invalid Phone Number. Please enter a valid 10 digit number"
    ): String{
        return getValidatedInput(prompt, errorMessage) { Validator.isValidPhone(it) }
    }

    fun getEmail(
        prompt: String = "Enter email: ",
        errorMessage: String = "Invalid email format. Please enter a valid email"
    ) : String {
        return getValidatedInput(prompt, errorMessage){ Validator.isValidEmail(it) }
    }

    fun getPassword(
        prompt: String = "Enter password: ",
        errorMessage: String = "Password must be at least 8 characters, with an uppercase letter, a lowercase letter, and a special character (@#$%^&+=!-_). Spaces are not allowed.")
    : String{
        return getValidatedInput(prompt, errorMessage){ Validator.isValidPassword(it) }
    }
    fun selectLocation(prompt: String = "Select Location: "): Location {

        while (true) {

            println(prompt)

            Location.entries.forEachIndexed { index, location ->
                println("${index + 1}. $location")
            }

            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice != null && choice in 1..Location.entries.size) {
                return Location.entries[choice - 1]
            }

            println("! Invalid choice. Please try again.")
        }
    }

    fun selectCabType(prompt: String = "Select Cab Type: "): CabType {

        while (true) {

            println(prompt)

            CabType.entries.forEachIndexed { index, cabType ->
                println("${index + 1}. $cabType")
            }

            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice != null && choice in 1..CabType.entries.size) {
                return CabType.entries[choice - 1]
            }

            println("! Invalid choice. Please try again.")
        }
    }

    fun getYesOrNo(prompt: String): Boolean {

        while (true) {

            print("$prompt (Y/N): ")

            when (readln().trim().uppercase()) {
                "Y", "YES" -> return true
                "N", "NO" -> return false
                else -> println("! Please enter Y or N.")
            }
        }
    }

    fun getOptionalName(currentValue: String): String {

        while (true) {
            print("Name [$currentValue] (Press Enter to keep the current value) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (Validator.isValidName(input)) {
                return input
            }
            println("Invalid name.")
        }
    }

    fun getOptionalPhone(currentValue: String): String {

        while (true) {
            print("Phone [$currentValue] (Press Enter to keep the current value) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (Validator.isValidPhone(input)) {
                return input
            }
            println("Invalid phone number.")
        }
    }
}