package cyberminds.backend.controller.auth;

import cyberminds.backend.dto.request.ResetPasswordDTO;
import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.request.VerificationCodeDTO;
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

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO user) throws AppException {
        if (authServiceImplementation.existByPhoneNumber(user.getPhoneNumber())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with this phone number already exists", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(409).body(responseDetails);
        }
        authServiceImplementation.createUser(user);
        authServiceImplementation.sendVerificationCode(user.getPhoneNumber());
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(201).body(responseDetails);
    }

    @PostMapping("/send-verification-code")
    public void sendVerificationCode(@Valid @RequestBody VerificationCodeDTO verificationCodeDTO) throws MessagingException {
        String verificationCode = authServiceImplementation.generateVerificationCode();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String apiKey = "TLSeWNuacjmuln6MGPFSc7ytnt8kY5CBjOzBe9ubvWc12JPLEIwlJFpm4R39b7";

        // Construct the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("to", verificationCodeDTO.getPhoneNumber());
        requestBody.put("from", "SafeChat");
        requestBody.put("sms", "Your verification code is: " + verificationCode);
        requestBody.put("type", "plain");
        requestBody.put("channel", "generic");
        requestBody.put("api_key", apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send the request to Termii API
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                "https://api.ng.termii.com/api/sms/send",
                HttpMethod.POST,
                requestEntity,
                Map.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.containsKey("message") && responseBody.get("message").equals("Successfully Sent")) {
                log.info("Verification code sent successfully to {}", verificationCodeDTO.getPhoneNumber());
            } else {
                throw new MessagingException("Failed to send verification code. Response: " + responseBody);
            }
        } else {
            throw new MessagingException("Failed to send verification code. HTTP Status: " + responseEntity.getStatusCode());
        }
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