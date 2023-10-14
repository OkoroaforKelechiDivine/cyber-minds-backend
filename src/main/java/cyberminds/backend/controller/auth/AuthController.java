package cyberminds.backend.controller.auth;

import cyberminds.backend.dto.request.ForgotPasswordRequestDTO;
import cyberminds.backend.dto.request.ResetPasswordDTO;
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
@RequestMapping("/api/auths")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserServiceImplementation userService;

    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO applicationUser) throws AppException {
        if (userService.alreadyExistByEmail(applicationUser.getEmail())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with this email already exists", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDetails);
        }
        userService.createUser(applicationUser);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDetails);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) throws MessagingException {
        if (!userService.alreadyExistByEmail(forgotPasswordRequestDTO.getEmail())){
            ResponseDetails userDoesNotExist = new ResponseDetails(LocalDateTime.now(), "User with that email does not exist", HttpStatus.NOT_FOUND.toString());
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(userDoesNotExist);
        }
        userService.forgotPassword(forgotPasswordRequestDTO.getEmail());
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "OTP sent to your email for password reset.", HttpStatus.OK.toString());
        return ResponseEntity.ok(responseDetails);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            userService.resetPassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getNewPassword(), resetPasswordDTO.getConfirmPassword());
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Password reset successfully.", HttpStatus.OK.toString());
            log.info("Password reset successful and the new password is " + resetPasswordDTO.getNewPassword());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException e) {
            ResponseDetails errorResponse = new ResponseDetails(LocalDateTime.now(), e.getMessage(), HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
