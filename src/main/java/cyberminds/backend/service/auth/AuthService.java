package cyberminds.backend.service.auth;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;

import javax.mail.MessagingException;

public interface AuthService {

    void createUser(RegistrationDTO blogger) throws AppException;
    void resetPassword(String email, String newPassword, String confirmPassword) throws AppException;
    void forgotPassword(String email) throws MessagingException;
}
