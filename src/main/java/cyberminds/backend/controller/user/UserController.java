package cyberminds.backend.controller.user;


import cyberminds.backend.dto.request.FriendsDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.service.user.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserServiceImplementation userService;

    @PostMapping("/follow")
    public ResponseEntity<?> followFriend(@RequestBody FriendsDTO request) throws AppException {
        if (userService.followFriend(request)) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User is now following the friend.", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } else {
            ResponseDetails errorDetails = new ResponseDetails(LocalDateTime.now(), "User or friend not found.", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
        }
    }
}

