package cyberminds.backend.controller.auth;

import cyberminds.backend.dto.request.ResetPasswordDTO;
import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.auth.AuthServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.web.client.RestTemplate;

import static cyberminds.backend.service.auth.AuthServiceImplementation.generateVerificationCode;


@RestController
@RequestMapping("/api/auths")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class AuthController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String token = "HULq-qEwT7yuBJQSU8at7E6UGp4f0_CkCtRwTVUPCx8y1fjUNujSrM-EZyM6rkPN";

    private static void sendOTP(String phoneNumber) throws URISyntaxException, IOException, InterruptedException {
        final String key = "OCAdQur6oTBPvK4ElBJTxq3s";
        final String secret = "bkm(fyP&)cnkI@1aAnE#EKj#szbujD7WDScO6zzH";

        String AuthHeader = "Basic " + Base64.getEncoder().encodeToString((token+":").getBytes());

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("sender", "SafeChat");
        jsonBody.put("message", "Hello there, your SafeChat OTP code is: " + generateVerificationCode());
        jsonBody.put("recipients", (new JSONArray()).put((new JSONObject()).put("msisdn", phoneNumber)
        ));

        var client  = HttpClient.newHttpClient();
        var request  = HttpRequest.newBuilder(new URI("https://gatewayapi.com/rest/mtsms"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .header("Authorization", AuthHeader)
                .header("Content-type", "application/json").build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info(String.valueOf(response.statusCode()));
        log.info(String.valueOf(response.body()));
    }

    @Autowired
    AuthServiceImplementation authServiceImplementation;

    @PostMapping("/create")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO user) throws AppException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, URISyntaxException, IOException, InterruptedException {
        if (authServiceImplementation.existByPhoneNumber(user.getPhoneNumber())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with this phone number already exists", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(409).body(responseDetails);
        }
        authServiceImplementation.createUser(user);
        sendOTP(user.getPhoneNumber());
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(201).body(responseDetails);
    }

//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOTP(@Valid @RequestBody VerificationCodeDTO verificationCodeDTO) {
//        try {
//            String phoneNumber = verificationCodeDTO.getPhoneNumber();
//            String otp = verificationCodeDTO.getOtp();
//            String validationResponse = authServiceImplementation.validateOTP(phoneNumber, otp);
//            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), validationResponse, HttpStatus.OK.toString());
//            return ResponseEntity.ok(responseDetails);
//        } catch (AppException e) {
//            ResponseDetails errorResponse = new ResponseDetails(LocalDateTime.now(), e.getMessage(), HttpStatus.BAD_REQUEST.toString());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//        }
//    }

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