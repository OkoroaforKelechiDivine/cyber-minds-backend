package cyberminds.backend.service.user;

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
public class UserServiceImplementation implements UserService {


    @Override
    public void followFriend(AppUser user) {

    }

    @Override
    public void searchForFriends(AppUser user) {

    }

    @Override
    public void unFollowFriend(AppUser user) {

    }

    @Override
    public void acceptFriendRequest(AppUser user) {

    }

    @Override
    public void deleteFriendRequest() {

    }

    @Override
    public void numberOfFriends(int numberOfFriends) {

    }

    @Override
    public void numberOfLikes(int numberOfLikes) {

    }

    @Override
    public void numberOfPosts(int numberOfPosts) {

    }

    @Override
    public void numberOfComments(int numberOfComments) {

    }

    @Override
    public void numberOfVideos(int numberOfVideos) {

    }
}
