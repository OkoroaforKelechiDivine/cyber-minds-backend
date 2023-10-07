package cyberminds.backend.service.utility;

import cyberminds.backend.model.user.User;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class UserValidationService {

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public void validateUser(User user) {
        if (user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }

        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (user.getPhoneNNumber() == null || user.getPhoneNNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (!isStrongPassword(user.getPassword())) {
            throw new IllegalArgumentException("Weak password. Password must be at least 8 characters long, " +
                    "contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isStrongPassword(String password) {
        return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
    }
}
