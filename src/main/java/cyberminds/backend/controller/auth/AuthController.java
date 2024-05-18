package cyberminds.backend.controller.auth;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.request.ValidateOtpDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.auth.AuthServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;

import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auths")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class AuthController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    AuthServiceImplementation authServiceImplementation;

    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO user) throws AppException, MessagingException {
        if (authServiceImplementation.existByEmail(user.getEmail())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with this email already exists", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(409).body(responseDetails);
        }
        authServiceImplementation.createUser(user);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(201).body(responseDetails);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOTP(@Valid @RequestBody ValidateOtpDTO validateOtpDTO) {
        try {
            if (authServiceImplementation.validateOtp(validateOtpDTO.getEmail(), validateOtpDTO.getOtp())){
                ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Account verified", HttpStatus.OK.toString());
                return ResponseEntity.status(200).body(responseDetails);
            }
                ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Invalid OTP", HttpStatus.BAD_REQUEST.toString());
                return ResponseEntity.status(400).body(responseDetails);
        } catch (Exception e) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Error Validating OTP", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return ResponseEntity.status(500).body(responseDetails);
        }
    }
}