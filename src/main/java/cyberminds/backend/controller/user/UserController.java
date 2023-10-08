package cyberminds.backend.controller.user;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.User;
import cyberminds.backend.service.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "true", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserServiceImplementation userService;
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody RegistrationDTO registrationDTO) {
        try {
            User createdUser = userService.createUser(registrationDTO);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (AppException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
