package cyberminds.backend.service.auth;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.model.user.Gender;
import cyberminds.backend.repository.user.UserRepository;
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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthServiceImplementation implements AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    ModelMapper modelMapper = new ModelMapper();
    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

    private String encryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    public Boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public static String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isStrongPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

    private void storeOtp(String email, String otp) {
        otpStorage.put(email, otp);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        synchronized (otpStorage) {
                            otpStorage.remove(email);
                        }
                        this.cancel();
                    }
                },
                TimeUnit.MINUTES.toMillis(5) // 5 minutes
        );
    }

    public boolean validateOtp(String email, String otp) throws AppException {
        if (otp.equals(otpStorage.get(email))) {
            AppUser user = userRepository.findByEmail(email);
            if (user != null) {
                user.setIsVerified(true);
                userRepository.save(user);
                otpStorage.remove(email);
                return true;
            } else {
                throw new AppException("User not found.");
            }
        }
        return false;
    }

    @Override
    public void createUser(RegistrationDTO user) throws AppException, MessagingException {
        if (Objects.equals(user.getEmail(), "")) {
            throw new AppException("User email is empty.");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new AppException("Invalid user email.");
        }
        if (!isStrongPassword(user.getPassword())) {
            if (user.getPassword().length() < 5) {
                throw new AppException("User password should not be less than 5 characters.");
            }
            throw new AppException("User password is too weak");
        }

        AppUser appUser = new AppUser();
        modelMapper.map(user, appUser);
        appUser.setCreatedDate(LocalDateTime.now().toString());
        appUser.setGender(Gender.valueOf(user.getGender()));
        appUser.setEmail(user.getEmail());
        appUser.setPassword(encryptPassword(user.getPassword()));
        appUser.setIsVerified(false);
        String otp = generateVerificationCode();
        userRepository.save(appUser);

        if (existByEmail(appUser.getEmail())) {
            storeOtp(appUser.getEmail(), otp);
            sendOtpToEmail(appUser.getEmail(), otp);
        } else {
            throw new AppException("User does not exist.");
        }
    }

    @Override
    public void sendOtpToEmail(String email, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Verification OTP");
        helper.setText("Dear User, your verification OTP is: " + otp);
        javaMailSender.send(message);
    }
}
