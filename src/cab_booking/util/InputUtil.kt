package cab_booking.util

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
}