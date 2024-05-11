package cyberminds.backend.service.auth;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.model.user.Gender;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthServiceImplementation implements AuthService{

    @Autowired
    UserRepository userRepository;

    ModelMapper modelMapper = new ModelMapper();
    private String encryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    public Boolean existByPhoneNumber(String phoneNumber){
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public static String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }

    private boolean validPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+234\\d{10}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }

    private boolean isStrongPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

    @Override
    public void createUser(RegistrationDTO user) throws AppException {
        if (Objects.equals(user.getPhoneNumber(), "")){
            throw new AppException("User phone number is empty.");
        }
        if (!validPhoneNumber(user.getPhoneNumber())){
            throw new AppException("Invalid user phone number.");
        }
        if (!isStrongPassword(user.getPassword())){
            if (user.getPassword().length() < 5){
                throw new AppException("User password should not be less than 5 characters.");
            }
            throw new AppException("User password is too weak");
        }
        AppUser appUser = new AppUser();
        modelMapper.map(user, appUser);
        appUser.setCreatedDate(LocalDateTime.now().toString());
        appUser.setGender(Gender.valueOf(user.getGender()));
        appUser.setPhoneNumber(user.getPhoneNumber());
        appUser.setPassword(encryptPassword(user.getPassword()));
        userRepository.save(appUser);
    }

    @Override
    public void resetPassword(String phoneNumber, String newPassword, String confirmPassword) throws AppException {
        if (!validPhoneNumber(phoneNumber)) {
            throw new AppException("Invalid user phone number.");
        }
        AppUser user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new AppException("User with phone number '" + phoneNumber + "' does not exist.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException("New password and confirm password do not match.");
        }
        if (isStrongPassword(newPassword)) {
            throw new AppException("New password is too weak.");
        }
        String hashedNewPassword = encryptPassword(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);
    }

}
