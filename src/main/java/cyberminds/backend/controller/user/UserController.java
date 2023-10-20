package cyberminds.backend.controller.user;


import cyberminds.backend.dto.request.FriendsDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
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

    @GetMapping("/search-friend")
    public ResponseEntity<?> searchFriend(@RequestParam String username) {
        try {
            userService.searchFriend(username);
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Friend found", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException ex) {
            ResponseDetails errorDetails = new ResponseDetails(LocalDateTime.now(), ex.getMessage(), HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
        }
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unFollowFriend(@RequestBody FriendsDTO request) {
        try {
            userService.unFollowFriend(request.getUserId(), request.getFriendId());
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User has unfollowed the friend.", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException ex) {
            ResponseDetails errorDetails = new ResponseDetails(LocalDateTime.now(), ex.getMessage(), HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
        }
    }
}

