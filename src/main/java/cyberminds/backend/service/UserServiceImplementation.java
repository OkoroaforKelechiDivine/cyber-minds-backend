package cyberminds.backend.service;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.User;
import cyberminds.backend.repository.user.UserRepository;
import cyberminds.backend.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User  findById(String id){
        return userRepository.findUserById(id);
    }
    public Boolean userDoesNotExistById(String id){
        return !userRepository.existsById(id);
    }
    private String encryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    public Boolean alreadyExistByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";


    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isStrongPassword(String password) {
        return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
    }

    @Override
    public User createUser(RegistrationDTO user) throws AppException {

        if (Objects.equals(user.getEmail(), "")){
            throw new AppException("User email is empty.");
        }
        if (isValidEmail(user.getEmail())){
            throw new AppException("Invalid user email.");
        }
        if (isStrongPassword(user.getPassword())){
            if (user.getPassword().length() < 5){
                throw new AppException("User password should not be less than 5 characters.");
            }
            throw new AppException("User password is too weak");
        }
        User appUser = new User();
        appUser.setCreatedDate(LocalDateTime.now());
        appUser.setEmail(user.getEmail());
        appUser.setPassword(encryptPassword(user.getPassword()));

        if (alreadyExistByEmail(appUser.getEmail())){
            throw new AppException("User with email '" + appUser.getEmail() +  "' already exists.");
        }
        return userRepository.save(appUser);
    }
}
