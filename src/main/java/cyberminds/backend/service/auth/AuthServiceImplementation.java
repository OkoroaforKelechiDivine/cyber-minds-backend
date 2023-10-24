package cyberminds.backend.service.auth;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.model.user.Gender;
import cyberminds.backend.repository.user.UserRepository;
import cyberminds.backend.service.utils.OTPGenerator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthServiceImplementation implements AuthService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;
    ModelMapper modelMapper = new ModelMapper();
    private String encryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }
    public Boolean existByEmail(String email){
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
    public void createUser(RegistrationDTO user) throws AppException {
        if (Objects.equals(user.getEmail(), "")){
            throw new AppException("User email is empty.");
        }
        if (!isValidEmail(user.getEmail())){
            throw new AppException("Invalid user email.");
        }
        if (!isStrongPassword(user.getPassword())){
            if (user.getPassword().length() < 5){
                throw new AppException("User password should not be less than 5 characters.");
            }
            throw new AppException("User password is too weak");
        }
        AppUser appUser = new AppUser();
        appUser.setFirstName(user.getFirstName());
        appUser.setLastName(user.getLastName());
        appUser.setCreatedDate(LocalDateTime.now().toString());
        appUser.setGender(Gender.valueOf(user.getGender()));
        appUser.setEmail(user.getEmail());
        appUser.setPassword(encryptPassword(user.getPassword()));
        modelMapper.map(appUser, user);
        userRepository.save(appUser);
    }

    @Override
    public void forgotPassword(String email) throws MessagingException {
        if (!isValidEmail(email)) {
            throw new MessagingException("Invalid user email.");
        }
        if (existByEmail(email)) {
            String otp = OTPGenerator.generateOTP();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Forgot Password OTP");
            helper.setText("Your OTP for resetting the password is: " + otp);
            javaMailSender.send(message);
            log.info("This is the OTP -->{}", otp);
        }
    }
    @Override
    public void resetPassword(String email, String newPassword, String confirmPassword) throws AppException {
        if (!isValidEmail(email)) {
            throw new AppException("Invalid user email.");
        }
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AppException("User with email '" + email + "' does not exist.");
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
