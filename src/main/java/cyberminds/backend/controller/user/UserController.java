package cyberminds.backend.controller.user;

import cyberminds.backend.dto.request.ForgotPasswordRequestDTO;
import cyberminds.backend.dto.request.PasswordResetDTO;
import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.user.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserServiceImplementation userService;

    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO applicationUser) throws AppException {
        log.info("Account creation started successfully");
        if (userService.alreadyExistByEmail(applicationUser.getEmail())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with this email already exists", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDetails);
        }
        userService.createUser(applicationUser);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDetails);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        try {
            userService.sendOTPByEmail(forgotPasswordRequestDTO.getEmail());
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "OTP sent to your email for password reset.", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } catch (MessagingException e) {
            ResponseDetails errorResponse = new ResponseDetails(LocalDateTime.now(), "Failed to send OTP email.", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            userService.resetPassword(passwordResetDTO.getEmail(), passwordResetDTO.getNewPassword(), passwordResetDTO.getConfirmPassword());
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Password reset successfully.", HttpStatus.OK.toString());
            log.info("Password reset successful and the new password is " + passwordResetDTO.getNewPassword());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException e) {
            ResponseDetails errorResponse = new ResponseDetails(LocalDateTime.now(), e.getMessage(), HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
