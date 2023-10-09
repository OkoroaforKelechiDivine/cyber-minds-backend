package cyberminds.backend.controller.user;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.user.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        userService.createUser(applicationUser);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Your account has been created successfully", HttpStatus.CREATED.toString());
        return ResponseEntity.status(201).body(responseDetails);
    }
}
