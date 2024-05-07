package cyberminds.backend.controller.auth;

import cyberminds.backend.dto.request.ForgotPasswordRequestDTO;
import cyberminds.backend.dto.request.ResetPasswordDTO;
import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.auth.AuthServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auths")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class AuthController {

    @Autowired
    AuthServiceImplementation authServiceImplementation;

    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO user) throws AppException {
        if (authServiceImplementation.existByPhoneNumber(user.getPhoneNumber())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with this phone number already exists", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(409).body(responseDetails);
        }
        authServiceImplementation.createUser(user);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(201).body(responseDetails);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) throws MessagingException {
        if (!authServiceImplementation.existByPhoneNumber(forgotPasswordRequestDTO.getPhoneNumber())){
            ResponseDetails userDoesNotExist = new ResponseDetails(LocalDateTime.now(), "User with that email does not exist", HttpStatus.NOT_FOUND.toString());
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(userDoesNotExist);
        }
        authServiceImplementation.forgotPassword(forgotPasswordRequestDTO.getPhoneNumber());
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "OTP sent to your email for password reset.", HttpStatus.OK.toString());
        return ResponseEntity.ok(responseDetails);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            authServiceImplementation.resetPassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getNewPassword(), resetPasswordDTO.getConfirmPassword());
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Password reset successfully.", HttpStatus.OK.toString());
            log.info("Password reset successful and the new password is " + resetPasswordDTO.getNewPassword());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException e) {
            ResponseDetails errorResponse = new ResponseDetails(LocalDateTime.now(), e.getMessage(), HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
