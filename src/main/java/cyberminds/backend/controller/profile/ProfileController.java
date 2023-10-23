package cyberminds.backend.controller.profile;


import cyberminds.backend.dto.request.ProfileDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.profile.ProfileServiceImplementation;
import cyberminds.backend.service.user.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/profile")
@Slf4j
public class ProfileController {


    @Autowired
    private ProfileServiceImplementation profileServiceImplementation;

    @Autowired
    private UserServiceImplementation userServiceImplementation;

    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createProfile(@Valid @RequestBody ProfileDTO profileDTO, @PathVariable String userId) throws AppException {
        if (profileServiceImplementation.existByUsername(profileDTO.getUsername())) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "A user with that username already exist", HttpStatus.NOT_ACCEPTABLE.toString());
            return ResponseEntity.status(406).body(responseDetails);
        }
        if (!userServiceImplementation.existsById(userId)){
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "User with ID doesn't exist", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(404).body(responseDetails);
        }
        profileServiceImplementation.createProfile(profileDTO, userId);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Profile created successfully.", HttpStatus.OK.toString());
        return ResponseEntity.status(200).body(responseDetails);
    }
}
