package cyberminds.backend.service.auth;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.request.VerificationCodeDTO;
import cyberminds.backend.exception.AppException;

import javax.mail.MessagingException;

public interface AuthService {

    void createUser(RegistrationDTO user) throws AppException, MessagingException;
    void sendOtpToEmail(String email, String otp) throws MessagingException;

}